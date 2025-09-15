package name.modid.core.api.events;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.joml.Vector3f;

import name.modid.core.api.modifiers.categories.ModifierAreaEffect;
import name.modid.core.api.modifiers.helpers.ModifierGatheringHelper;
import name.modid.core.utils.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class EventAreaEffect {
  public static void setupEvent(ServerPlayerEntity player) {
    World world = player.getWorld();

    List<ModifierAreaEffect> allModifiers = Utils.collectPlayerArmorValues(
        player,
        armorPiece -> ModifierGatheringHelper.getAreaEffectModifiers(armorPiece));

    Map<RegistryEntry<StatusEffect>, List<ModifierAreaEffect>> grouped = allModifiers.stream()
        .filter(m -> m.getEffectEntry() != null)
        .collect(Collectors.groupingBy(m -> m.getEffectEntry()));

    for (Map.Entry<RegistryEntry<StatusEffect>, List<ModifierAreaEffect>> entry : grouped.entrySet()) {
      RegistryEntry<StatusEffect> effect = entry.getKey();
      List<ModifierAreaEffect> modifiers = entry.getValue();

      double totalRadius = 0.0;
      int maxAmplifier = 0;
      int maxDuration = 0;
      boolean notMeFlag = false;
      boolean onlyPlayersFlag = false;

      for (ModifierAreaEffect modifier : modifiers) {
        totalRadius += modifier.getRadiusLevels().get(modifier.getRarityType());
        maxAmplifier = Math.max(maxAmplifier, modifier.getAmplifier());
        maxDuration = Math.max(maxDuration, modifier.getDuration());
        if (modifier.isNotMe())
          notMeFlag = true;
        if (modifier.isOnlyPlayers())
          onlyPlayersFlag = true;
      }

      final boolean NOT_ME = notMeFlag;
      final boolean ONLY_PLAYERS = onlyPlayersFlag;

      List<LivingEntity> nearby = world.getEntitiesByClass(
          LivingEntity.class,
          player.getBoundingBox().expand(totalRadius),
          e -> {
            if (!e.isAlive())
              return false;

            if (ONLY_PLAYERS) {
              if (!(e instanceof ServerPlayerEntity))
                return false;
              if (NOT_ME && e == player)
                return false;
              return true;
            } else {
              if (NOT_ME && e == player)
                return false;
              return true;
            }
          });

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