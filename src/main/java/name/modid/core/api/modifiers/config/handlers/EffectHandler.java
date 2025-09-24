package name.modid.core.api.modifiers.config.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.joml.Vector3f;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.AreaEffectConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.HitEffectMeleeConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierHandler;
import name.modid.core.api.modifiers.config.ModifierUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;

public class EffectHandler {
  public static class Area implements ModifierHandler<ModifierConfig.AreaEffectConfig> {
    @Override
    public void apply(ArrayList<GemstoneModifier> modifiers, ModifierContext ctx) {
      Map<RegistryEntry<StatusEffect>, List<GemstoneModifier>> groupedByEffect = modifiers.stream()
          .collect(Collectors.groupingBy(mod -> ((AreaEffectConfig) mod.getConfig()).effect()));

      for (Map.Entry<RegistryEntry<StatusEffect>, List<GemstoneModifier>> entry : groupedByEffect.entrySet()) {
        RegistryEntry<StatusEffect> effect = entry.getKey();
        List<GemstoneModifier> effectGroup = entry.getValue();

        double totalRadius = 0.0;
        int maxAmplifier = -1;
        int maxDuration = 0;
        boolean notMeFlag = false;
        boolean onlyPlayersFlag = false;

        for (GemstoneModifier modifier : effectGroup) {
          AreaEffectConfig config = (AreaEffectConfig) modifier.getConfig();
          totalRadius += config.radiusLevels().get(modifier.getRarityType());
          maxAmplifier = Math.max(maxAmplifier, config.amplifier());
          maxDuration = Math.max(maxDuration, config.duration());
          if (config.notMe())
            notMeFlag = true;
          if (config.onlyPlayers())
            onlyPlayersFlag = true;
        }

        if (totalRadius <= 0)
          continue;

        final boolean finalNotMe = notMeFlag;
        final boolean finalOnlyPlayers = onlyPlayersFlag;
        List<LivingEntity> nearby = ctx.getOwner().getWorld().getEntitiesByClass(LivingEntity.class,
            ctx.getOwner().getBoundingBox().expand(totalRadius), e -> {
              if (!e.isAlive())
                return false;
              if (finalOnlyPlayers && !(e instanceof PlayerEntity))
                return false;
              if (finalNotMe && e.equals(ctx.getOwner()))
                return false;
              return true;
            });

        for (LivingEntity entity : nearby) {
          entity.addStatusEffect(new StatusEffectInstance(effect, maxDuration * 20, maxAmplifier, false, true, true));
        }

        if (ctx.getOwner().getWorld().getRandom().nextInt(3) == 0
            && ctx.getOwner().getWorld() instanceof ServerWorld serverWorld) {
          int color = effect.value().getColor();
          float r = ((color >> 16) & 0xFF) / 255.0f;
          float g = ((color >> 8) & 0xFF) / 255.0f;
          float b = (color & 0xFF) / 255.0f;
          DustParticleEffect particle = new DustParticleEffect(new Vector3f(r, g, b), 1.0f);

          for (int i = 0; i < 8 + totalRadius * 2; i++) {
            double angle = serverWorld.getRandom().nextDouble() * Math.PI * 2;
            double dist = serverWorld.getRandom().nextDouble() * totalRadius;
            double x = ctx.getOwner().getX() + Math.cos(angle) * dist;
            double y = ctx.getOwner().getY() + 0.5 + serverWorld.getRandom().nextDouble() * (totalRadius * 2)
                - totalRadius;
            double z = ctx.getOwner().getZ() + Math.sin(angle) * dist;
            serverWorld.spawnParticles(particle, x, y, z, 1, 0, 0, 0, 0);
          }
        }
      }
    }
  }

  public static class Melee implements ModifierHandler<ModifierConfig.HitEffectMeleeConfig> {
    @Override
    public void apply(ArrayList<GemstoneModifier> modifiers, ModifierContext ctx) {
      Map<RegistryEntry<StatusEffect>, List<GemstoneModifier>> groupedByEffect = modifiers.stream()
          .collect(Collectors.groupingBy(mod -> ((HitEffectMeleeConfig) mod.getConfig()).effect()));

      for (Map.Entry<RegistryEntry<StatusEffect>, List<GemstoneModifier>> entry : groupedByEffect.entrySet()) {
        RegistryEntry<StatusEffect> effect = entry.getKey();
        List<GemstoneModifier> effectGroup = entry.getValue();

        double combinedChance = 0.0;
        int maxAmplifier = -1;
        int maxDuration = 0;

        for (GemstoneModifier modifier : effectGroup) {
          HitEffectMeleeConfig config = (HitEffectMeleeConfig) modifier.getConfig();
          combinedChance += config.chance().get(modifier.getRarityType());
          maxAmplifier = Math.max(maxAmplifier, config.amplifier());
          maxDuration = Math.max(maxDuration, config.duration());
        }

        if (ModifierUtils.proc(ctx.getWorld(), combinedChance)) {
          ctx.getTarget().addStatusEffect(new StatusEffectInstance(effect, maxDuration * 20, maxAmplifier));
        }
      }

      ctx.setActionResult(ActionResult.PASS);
    }
  }

  public static class Projectile implements ModifierHandler<ModifierConfig.HitEffectProjectileConfig> {
    @Override
    public void apply(ArrayList<GemstoneModifier> modifiers, ModifierContext ctx) {
    }
  }

}