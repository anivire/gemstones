package name.modid.helpers.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import name.modid.helpers.GemstoneSocketingHelper;
import name.modid.helpers.components.Gemstone;
import name.modid.helpers.modifiers.GemstoneModifier;
import name.modid.helpers.modifiers.ModifierHelper;
import name.modid.helpers.modifiers.modifierTypes.EventType;
import name.modid.helpers.modifiers.modifierTypes.ModifierOnBlockBreak;
import name.modid.helpers.modifiers.modifierTypes.ModifierOnDamage;
import name.modid.helpers.modifiers.modifierTypes.ModifierOnHit;
import name.modid.helpers.modifiers.modifierTypes.ModifierOnHitEffect;
import name.modid.helpers.types.GemstoneRarity;
import name.modid.helpers.types.GemstoneType;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;

public class EventRegistrationHelper {
  private static final Random RANDOM = new Random();
  private static final Set<LivingEntity> affectedEntities = new HashSet<>();
  private static final Map<LivingEntity, Integer> particleTicks = new HashMap<>();

  public static void initialize() {
    AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
      if (!world.isClient && entity instanceof LivingEntity target) {
        Item item = player.getStackInHand(hand).getItem();
        ItemStack itemStack = player.getStackInHand(hand);

        if (!GemstoneSocketingHelper.isGemstonesExists(itemStack)) {
          return ActionResult.PASS;
        }

        Gemstone[] gemstones = GemstoneSocketingHelper.getGemstones(itemStack);
        Map<Integer, Map<GemstoneType, GemstoneRarity>> itemGemstones = new HashMap<>();

        for (int i = 0; i < gemstones.length; i++) {
          Gemstone gem = gemstones[i];
          if (gem.gemstoneType() != null && gem.gemstoneType() != GemstoneType.LOCKED) {
            Map<GemstoneType, GemstoneRarity> m = new HashMap<>();
            m.put(gem.gemstoneType(), gem.gemstoneRarityType());
            itemGemstones.put(i, m);
          }
        }

        ArrayList<ModifierOnHitEffect> gemstoneModifiers = new ArrayList<>();
        for (Map.Entry<Integer, Map<GemstoneType, GemstoneRarity>> m : itemGemstones.entrySet()) {
          Map<GemstoneType, GemstoneRarity> i = m.getValue();
          for (Map.Entry<GemstoneType, GemstoneRarity> e : i.entrySet()) {
            GemstoneType gemstoneType = e.getKey();
            GemstoneRarity gemstoneRarity = e.getValue();
            GemstoneModifier gemstoneModifier =
                ModifierHelper.getGemstoneModifierForItem(gemstoneType, item);
            if (gemstoneModifier != null
                && gemstoneModifier instanceof ModifierOnHitEffect modifierOnHitEffect) {
              ModifierOnHitEffect newModifier = new ModifierOnHitEffect(
                  modifierOnHitEffect.inflitChance, modifierOnHitEffect.duration,
                  modifierOnHitEffect.amplifier, modifierOnHitEffect.itemType,
                  modifierOnHitEffect.effect, modifierOnHitEffect.isStacking,
                  modifierOnHitEffect.maxStackCount, modifierOnHitEffect.gemstoneType);
              newModifier.setRarityType(gemstoneRarity);
              gemstoneModifiers.add(newModifier);
            }
          }
        }

        GemstoneSocketingHelper.applyOnHitEffectModifiers(gemstoneModifiers, item, itemStack,
            target, world);
      }
      return ActionResult.PASS;
    });

    PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
      ItemStack itemStack = player.getMainHandStack();
      ArrayList<ModifierOnBlockBreak> modifiers =
          ModifierHelper.getOnBlockBreakModifiers(itemStack);

      if (modifiers.isEmpty()) {
        return;
      }

      GemstoneSocketingHelper.applyOnBlockBreakModifiers(modifiers, player, world);
    });

    ServerLivingEntityEvents.AFTER_DAMAGE
        .register((entity, source, baseDamageTaken, damageTaken, blocked) -> {
          ServerWorld world = (ServerWorld) entity.getWorld();
          ArrayList<ModifierOnDamage> allModifiersOnDamage = new ArrayList<>();
          ArrayList<ModifierOnHit> allModifiersOnHit = new ArrayList<>();

          if (entity instanceof LivingEntity target && source.getSource() instanceof ArrowEntity) {
            if (entity.getWorld() instanceof ServerWorld serverWorld) {
              ItemStack itemStack = source.getWeaponStack();
              allModifiersOnHit.addAll(ModifierHelper.getOnHitModifiers(itemStack));

              if (!allModifiersOnHit.isEmpty()) {
                double applyTotalChance = 0.0;
                for (ModifierOnHit modifier : allModifiersOnHit) {
                  if (modifier.eventType == EventType.TORRENT) {
                    applyTotalChance +=
                        modifier.eventChance.get(modifier.getRarityType().getValue());
                  }
                }

                if (applyTotalChance > 0 && RANDOM.nextDouble() < Math.min(applyTotalChance, 1.0)) {
                  applyTorrentEffect(serverWorld, target);
                }
              }
            }
          }


          for (ItemStack armorItem : entity.getArmorItems()) {
            if (armorItem != null && GemstoneSocketingHelper.isGemstonesExists(armorItem)) {
              allModifiersOnDamage.addAll(ModifierHelper.getOnDamageModifiers(armorItem));
            }
          }

          if (allModifiersOnDamage.isEmpty()) {
            return;
          } else {
            GemstoneSocketingHelper.applyOnDamageModifiers(allModifiersOnDamage, entity, world);
          }
        });

    ServerTickEvents.END_SERVER_TICK.register(server -> {
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
    });

    ServerLivingEntityEvents.AFTER_DEATH.register(EventHarvestMark::setupEvent);


    AttackEntityCallback.EVENT.register(EventStunned::setupEvent);
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
