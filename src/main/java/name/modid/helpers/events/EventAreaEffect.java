package name.modid.helpers.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.joml.Vector3f;

import name.modid.helpers.modifiers.ModifierHelper;
import name.modid.helpers.modifiers.category.ModifierAreaEffect;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class EventAreaEffect {
  public static void setupEvent(ServerPlayerEntity player) {
    World world = player.getWorld();
    List<ItemStack> equippedArmorPieces = Stream.of(
        player.getEquippedStack(EquipmentSlot.HEAD),
        player.getEquippedStack(EquipmentSlot.CHEST),
        player.getEquippedStack(EquipmentSlot.LEGS),
        player.getEquippedStack(EquipmentSlot.FEET))
        .filter(stack -> !stack.isEmpty())
        .toList();

    List<ModifierAreaEffect> allModifiers = new ArrayList<>();
    for (ItemStack armorPiece : equippedArmorPieces) {
      allModifiers.addAll(ModifierHelper.getAreaEffectModifiers(armorPiece));
    }

    Map<RegistryEntry<StatusEffect>, List<ModifierAreaEffect>> grouped = allModifiers.stream()
        .filter(m -> m.getEffect() != null)
        .collect(Collectors.groupingBy(m -> m.getEffect()));

    for (Map.Entry<RegistryEntry<StatusEffect>, List<ModifierAreaEffect>> entry : grouped.entrySet()) {
      RegistryEntry<StatusEffect> effect = entry.getKey();
      List<ModifierAreaEffect> modifiers = entry.getValue();

      double totalRadius = 0.0;
      int maxAmplifier = 0;
      int maxDuration = 0;
      boolean notMeFlag = false;

      for (ModifierAreaEffect modifier : modifiers) {
        totalRadius += modifier.getRadiusLevels().get(modifier.getRarityType());
        maxAmplifier = Math.max(maxAmplifier, modifier.getAmplifier());
        maxDuration = Math.max(maxDuration, modifier.getDuration());
        if (modifier.isNotMe())
          notMeFlag = true;
      }

      final boolean NOT_ME = notMeFlag;

      List<LivingEntity> nearby = world.getEntitiesByClass(
          LivingEntity.class,
          player.getBoundingBox().expand(totalRadius),
          e -> e.isAlive()
              && !(e instanceof ServerPlayerEntity)
              && (!NOT_ME || e != player));

      for (LivingEntity entity : nearby) {
        entity.addStatusEffect(new StatusEffectInstance(
            effect,
            maxDuration * 20,
            maxAmplifier,
            false,
            true,
            true));
      }

      if (!world.isClient && world.getRandom().nextInt(3) == 0) {
        int color = effect.value().getColor();
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;

        DustParticleEffect particle = new DustParticleEffect(new Vector3f(r, g, b), 1.0f);

        for (int i = 0; i < 8 + totalRadius * 2; i++) {
          double angle = world.getRandom().nextDouble() * Math.PI * 2;
          double dist = world.getRandom().nextDouble() * totalRadius;
          double x = player.getX() + Math.cos(angle) * dist;
          double y = player.getY() + 0.5 + world.getRandom().nextDouble() * (totalRadius * 2) - totalRadius;
          double z = player.getZ() + Math.sin(angle) * dist;

          if (world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(
                particle,
                x, y, z,
                1,
                0, 0, 0,
                0);
          }
        }
      }
    }
  }
}
