package name.modid.helpers.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import name.modid.helpers.modifiers.ModifierHelper;
import name.modid.helpers.modifiers.category.ModifierOnHit;
import name.modid.helpers.modifiers.type.EventType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class EventOnHit {
  private static final Random RANDOM = new Random();
  private static final Set<LivingEntity> affectedEntities = new HashSet<>();
  private static final Map<LivingEntity, Integer> particleTicks = new HashMap<>();

  public static void setupEvent(LivingEntity entity, DamageSource source, float baseDamageTaken, float damageTaken,
      boolean blocked) {
    ArrayList<ModifierOnHit> allModifiersOnHit = new ArrayList<>();

    if (entity instanceof LivingEntity target && source.getSource() instanceof ArrowEntity) {
      if (entity.getWorld() instanceof ServerWorld serverWorld) {
        ItemStack itemStack = source.getWeaponStack();
        allModifiersOnHit.addAll(ModifierHelper.getOnHitModifiers(itemStack));

        if (!allModifiersOnHit.isEmpty()) {
          double applyTotalChance = 0.0;
          for (ModifierOnHit modifier : allModifiersOnHit) {
            if (modifier.eventType == EventType.TORRENT) {
              applyTotalChance += modifier.eventChance.get(modifier.getRarityType().getValue());
            }
          }

          if (applyTotalChance > 0 && RANDOM.nextDouble() < Math.min(applyTotalChance, 1.0)) {
            applyTorrentEffect(serverWorld, target);
          }
        }
      }
    }
  }

  public static void setupEffect(MinecraftServer server) {
    Set<LivingEntity> toRemove = new HashSet<>();
    for (LivingEntity target : affectedEntities) {
      if (!target.isAlive() || target.isRemoved()) {
        toRemove.add(target);
        continue;
      }

      ServerWorld world = (ServerWorld) target.getWorld();
      world.spawnParticles(ParticleTypes.FALLING_WATER, target.getX(), target.getY() - 0.5,
          target.getZ(), 15, 0.4, -0.4, 0.4, 0.2);

      int ticks = particleTicks.getOrDefault(target, 0) + 1;
      particleTicks.put(target, ticks);

      if (target.getVelocity().y < 0 || ticks >= 20) {
        toRemove.add(target);
        particleTicks.remove(target);
      }
    }
    affectedEntities.removeAll(toRemove);
  }

  private static void applyTorrentEffect(ServerWorld world, LivingEntity target) {
    Vec3d startPos = target.getPos();
    double upwardImpulse = 0.8;
    target.addVelocity(0, upwardImpulse, 0);
    target.velocityModified = true;

    world.spawnParticles(ParticleTypes.GUST, startPos.x, startPos.y + 0.5, startPos.z, 3, 0.5, 0.5,
        0.5, 0.1);

    affectedEntities.add(target);
  }
}
