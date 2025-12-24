package name.modid.core.mixins.effects.witherGuard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig.PlayerConfig;
import name.modid.core.api.modifiers.config.ModifierUtils;
import name.modid.core.api.modifiers.helpers.ModifierGatheringHelper;
import name.modid.core.utils.WitherSkullOrbitFlag;
import name.modid.core.utils.WitherSkullOwner;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Mixin(PlayerEntity.class)
public abstract class PlayerWitherGuard extends LivingEntity implements WitherSkullOwner {
  @Unique
  private static final int MAX_SKULLS = 3;
  @Unique
  private static final int RECHARGE_TICKS = 5 * 20;
  @Unique
  private static final int ATTACK_DELAY_TICKS = 20;
  @Unique
  private static final double TARGET_RADIUS = 12.0;
  @Unique
  private static final double ORBIT_RADIUS = 1.8;
  @Unique
  private static final int ATTACK_COOLDOWN_TICKS = 20;
  @Unique
  private int witherSkullCount = 0;
  @Unique
  private int witherSkullRechargeTimer = RECHARGE_TICKS;
  @Unique
  private final List<WitherSkullEntity> orbitingSkulls = new ArrayList<>();
  @Unique
  private final List<Integer> skullSpawnTicks = new ArrayList<>();
  @Unique
  private int attackCooldown = 0;
  @Unique
  private boolean hadValidItemLastTick = false;
  @Unique
  private World prevWorldRef;

  protected PlayerWitherGuard(EntityType<? extends LivingEntity> type, World world) {
    super(type, world);
  }

  @Override
  public int getWitherSkullCount() {
    return witherSkullCount;
  }

  @Override
  public void setWitherSkullCount(int count) {
    this.witherSkullCount = count;
  }

  @Override
  public int getWitherSkullRechargeTimer() {
    return witherSkullRechargeTimer;
  }

  @Override
  public void setWitherSkullRechargeTimer(int ticks) {
    this.witherSkullRechargeTimer = ticks;
  }

  @Inject(method = "tick", at = @At("HEAD"))
  private void tickWitherSkulls(CallbackInfo ci) {
    if (getWorld().isClient())
      return;

    if (prevWorldRef != getWorld()) {
      if (!orbitingSkulls.isEmpty()) {
        for (WitherSkullEntity skull : orbitingSkulls) {
          if (skull != null && skull.isAlive()) {
            skull.discard();
          }
        }
        orbitingSkulls.clear();
        skullSpawnTicks.clear();
      }
      prevWorldRef = getWorld();
      hadValidItemLastTick = false;
      witherSkullCount = 0;
    }

    ServerWorld world = (ServerWorld) getWorld();
    PlayerEntity player = (PlayerEntity) (Object) this;

    if (attackCooldown > 0)
      attackCooldown--;

    orbitingSkulls.removeIf(s -> !s.isAlive());
    for (Iterator<Integer> it = skullSpawnTicks.iterator(); it.hasNext();) {
      if (skullSpawnTicks.indexOf(it.next()) >= orbitingSkulls.size()) {
        it.remove();
      }
    }

    if (!(player instanceof ServerPlayerEntity serverPlayer)) {
      return;
    }

    List<GemstoneModifier> modifiers = ModifierUtils.collectValuesFromAllEquipment(
        serverPlayer,
        armorPiece -> ModifierGatheringHelper.getModifiers(armorPiece, PlayerConfig.class));

    // TODO: check with id not name
    boolean hasWitherGuard = modifiers.stream()
        .anyMatch(mod -> {
          if (mod.getConfig() instanceof PlayerConfig config) {
            return config.eventType().getName() == "PLAYER_WITHER_GUARD";
          }
          return false;
        });

    if (!hasWitherGuard) {
      if (!orbitingSkulls.isEmpty()) {
        witherSkullCount = orbitingSkulls.size();

        for (WitherSkullEntity skull : orbitingSkulls) {
          if (skull != null && skull.isAlive()) {
            skull.discard();
          }
        }

        orbitingSkulls.clear();
        skullSpawnTicks.clear();
      }

      hadValidItemLastTick = false;
      return;
    }

    if (!hadValidItemLastTick && orbitingSkulls.isEmpty() && witherSkullCount > 0) {
      for (int i = 0; i < witherSkullCount; i++) {
        spawnWitherSkull(world);
      }

      world.playSound(null, player.getX(), player.getY(), player.getZ(),
          SoundEvents.PARTICLE_SOUL_ESCAPE, SoundCategory.PLAYERS, 0.5f, 1.2f);
      world.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME,
          player.getX(), player.getEyeY(), player.getZ(),
          20, 0.4, 0.6, 0.4, 0.02);

      witherSkullRechargeTimer = RECHARGE_TICKS;
    }

    hadValidItemLastTick = true;

    witherSkullCount = orbitingSkulls.size();
    if (witherSkullCount < MAX_SKULLS) {
      if (--witherSkullRechargeTimer <= 0) {
        spawnWitherSkull(world);
        witherSkullRechargeTimer = RECHARGE_TICKS;
      }
    }

    Box box = new Box(getPos(), getPos()).expand(TARGET_RADIUS);
    List<HostileEntity> targets = world.getEntitiesByClass(HostileEntity.class, box, HostileEntity::isAlive);

    if (attackCooldown <= 0 && !targets.isEmpty() && !orbitingSkulls.isEmpty()) {
      HostileEntity target = targets.get(0);

      for (int i = 0; i < orbitingSkulls.size(); i++) {
        WitherSkullEntity skull = orbitingSkulls.get(i);
        int spawnTick = skullSpawnTicks.get(i);

        if (this.age - spawnTick < ATTACK_DELAY_TICKS)
          continue;

        orbitingSkulls.remove(i);
        skullSpawnTicks.remove(i);

        Vec3d start = skull.getPos();
        Vec3d targetPos = target.getPos().add(0, target.getHeight(), 0);
        Vec3d dir = targetPos.subtract(start).normalize();

        ((WitherSkullOrbitFlag) (Object) skull).gemstones$setOrbiting(false);
        skull.noClip = false;
        skull.setInvulnerable(false);
        skull.setNoGravity(false);
        skull.setOwner(player);
        skull.setVelocity(dir.multiply(1.2));

        world.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME,
            start.x, start.y, start.z, 20, 0.4, 0.4, 0.4, 0.01);
        world.spawnParticles(ParticleTypes.SMOKE,
            start.x, start.y, start.z, 10, 0.2, 0.2, 0.2, 0.0);
        world.playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.ENTITY_WITHER_SHOOT, SoundCategory.PLAYERS,
            1.0f, 1.0f + world.random.nextFloat() * 0.2f);

        attackCooldown = ATTACK_COOLDOWN_TICKS;
        break;
      }
    }

    updateOrbitingSkulls(world);
  }

  @Inject(method = "remove", at = @At("HEAD"))
  private void onPlayerRemoved(RemovalReason reason, CallbackInfo ci) {
    if (getWorld().isClient())
      return;

    for (WitherSkullEntity skull : orbitingSkulls) {
      if (skull != null && skull.isAlive()) {
        skull.discard();
      }
    }

    orbitingSkulls.clear();
    skullSpawnTicks.clear();
  }

  @Unique
  private void spawnWitherSkull(ServerWorld world) {
    PlayerEntity player = (PlayerEntity) (Object) this;
    WitherSkullEntity skull = new WitherSkullEntity(world, player, movementMultiplier);

    skull.refreshPositionAndAngles(player.getX(), player.getEyeY() - 0.5, player.getZ(), 0, 0);
    skull.setVelocity(Vec3d.ZERO);
    skull.setNoGravity(true);
    skull.setSilent(true);
    skull.noClip = true;
    skull.setInvulnerable(true);

    ((WitherSkullOrbitFlag) (Object) skull).gemstones$setOrbiting(true);

    world.spawnEntity(skull);
    orbitingSkulls.add(skull);
    skullSpawnTicks.add(this.age);
  }

  @Unique
  private void updateOrbitingSkulls(ServerWorld world) {
    if (orbitingSkulls.isEmpty())
      return;

    long time = world.getTime();
    // Vec3d center = getPos().add(0, getEyeHeight(getPose()) * 0.8, 0);
    Vec3d center = getPos().add(0, getHeight(), 0);

    for (int i = 0; i < orbitingSkulls.size(); i++) {
      WitherSkullEntity skull = orbitingSkulls.get(i);
      double ang = (time * 0.05 + (2 * Math.PI / orbitingSkulls.size()) * i) % (2 * Math.PI);
      double x = center.x + ORBIT_RADIUS * Math.cos(ang);
      double z = center.z + ORBIT_RADIUS * Math.sin(ang);
      double y = center.y + Math.sin((time + i * 10) * 0.05) * 0.2;

      Vec3d dest = new Vec3d(x, y, z);
      Vec3d mot = dest.subtract(skull.getPos()).multiply(0.3);
      skull.setVelocity(mot);

      skull.noClip = true;
      skull.setInvulnerable(true);
      ((WitherSkullOrbitFlag) (Object) skull).gemstones$setOrbiting(true);
      skull.setNoGravity(true);
      skull.velocityDirty = true;
    }
  }
}