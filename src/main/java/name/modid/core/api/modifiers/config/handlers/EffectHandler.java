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
import name.modid.core.api.modifiers.config.ModifierConfig.HitEffectProjectileConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierHandler;
import name.modid.core.api.modifiers.config.utils.ModifierUtils;
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
      if (ctx.getOwner() instanceof LivingEntity owner) {
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

          final boolean NOT_ME = notMeFlag;
          final boolean ONLY_PLAYERS = onlyPlayersFlag;
          List<LivingEntity> nearby = owner.getWorld().getEntitiesByClass(LivingEntity.class,
              owner.getBoundingBox().expand(totalRadius), e -> {
                if (!e.isAlive())
                  return false;
                if (ONLY_PLAYERS && !(e instanceof PlayerEntity))
                  return false;
                if (NOT_ME && e.equals(owner))
                  return false;
                return true;
              });

          for (LivingEntity entity : nearby) {
            entity.addStatusEffect(new StatusEffectInstance(effect, maxDuration * 20, maxAmplifier, false, true, true));
          }

          if (owner.getWorld().getRandom().nextInt(3) == 0
              && owner.getWorld() instanceof ServerWorld serverWorld) {
            int color = effect.value().getColor();
            float r = ((color >> 16) & 0xFF) / 255.0f;
            float g = ((color >> 8) & 0xFF) / 255.0f;
            float b = (color & 0xFF) / 255.0f;
            DustParticleEffect particle = new DustParticleEffect(new Vector3f(r, g, b), 1.0f);

            for (int i = 0; i < 8 + totalRadius * 2; i++) {
              double angle = serverWorld.getRandom().nextDouble() * Math.PI * 2;
              double dist = serverWorld.getRandom().nextDouble() * totalRadius;
              double x = owner.getX() + Math.cos(angle) * dist;
              double y = owner.getY() + 0.5 + serverWorld.getRandom().nextDouble() * (totalRadius * 2)
                  - totalRadius;
              double z = owner.getZ() + Math.sin(angle) * dist;
              serverWorld.spawnParticles(particle, x, y, z, 1, 0, 0, 0, 0);
            }
          }
        }
      }
    }
  }

  public static class Melee implements ModifierHandler<ModifierConfig.HitEffectMeleeConfig> {
    @Override
    public void apply(ArrayList<GemstoneModifier> modifiers, ModifierContext ctx) {
      boolean hasDamageContext = ctx.getBaseDamageTaken() > 0.0F;

      if (ctx.getOwner() instanceof PlayerEntity player
          && !hasDamageContext
          && player.getAttackCooldownProgress(0.0F) < 1.0F) {
        ctx.setActionResult(ActionResult.PASS);
        return;
      }

      if (ctx.getTarget() instanceof LivingEntity target) {
        Map<RegistryEntry<StatusEffect>, List<GemstoneModifier>> groupedByEffect = modifiers.stream()
            .collect(Collectors.groupingBy(mod -> ((HitEffectMeleeConfig) mod.getConfig()).effect()));

        for (Map.Entry<RegistryEntry<StatusEffect>, List<GemstoneModifier>> entry : groupedByEffect.entrySet()) {
          RegistryEntry<StatusEffect> effect = entry.getKey();
          List<GemstoneModifier> effectGroup = entry.getValue();

          List<Double> chances = new ArrayList<>();
          int maxAmplifier = -1;
          int maxDuration = 0;

          for (GemstoneModifier modifier : effectGroup) {
            HitEffectMeleeConfig config = (HitEffectMeleeConfig) modifier.getConfig();
            chances.add(config.chance().get(modifier.getRarityType()));
            maxAmplifier = Math.max(maxAmplifier, config.amplifier());
            maxDuration = Math.max(maxDuration, config.duration());
          }

          double combinedChance = ModifierUtils.cappedProcChance(chances);

          if (ModifierUtils.proc(ctx.getWorld(), combinedChance)) {
            target.addStatusEffect(new StatusEffectInstance(effect, maxDuration * 20, maxAmplifier));
          }
        }

      }
      ctx.setActionResult(ActionResult.PASS);
    }
  }

  public static class Projectile implements ModifierHandler<ModifierConfig.HitEffectProjectileConfig> {
    @Override
    public void apply(ArrayList<GemstoneModifier> modifiers, ModifierContext ctx) {
      if (ctx.getTarget() instanceof LivingEntity target) {
        Map<RegistryEntry<StatusEffect>, List<GemstoneModifier>> groupedByEffect = modifiers.stream()
            .collect(Collectors.groupingBy(mod -> ((HitEffectProjectileConfig) mod.getConfig()).effect()));

        for (Map.Entry<RegistryEntry<StatusEffect>, List<GemstoneModifier>> entry : groupedByEffect.entrySet()) {
          RegistryEntry<StatusEffect> effect = entry.getKey();
          List<GemstoneModifier> effectGroup = entry.getValue();

          List<Double> chances = new ArrayList<>();
          int maxAmplifier = -1;
          int maxDuration = 0;

          for (GemstoneModifier modifier : effectGroup) {
            HitEffectProjectileConfig config = (HitEffectProjectileConfig) modifier.getConfig();
            chances.add(config.chance().get(modifier.getRarityType()));
            maxAmplifier = Math.max(maxAmplifier, config.amplifier());
            maxDuration = Math.max(maxDuration, config.duration());
          }

          double combinedChance = ModifierUtils.cappedProcChance(chances);

          if (ModifierUtils.proc(ctx.getWorld(), combinedChance)) {
            target.addStatusEffect(new StatusEffectInstance(effect, maxDuration * 20, maxAmplifier));
          }
        }
      }
    }
  }
}
