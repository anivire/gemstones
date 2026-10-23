package name.modid.core.api.modifiers.config.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.AreaEffectConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.HitEffectMeleeConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.HitEffectProjectileConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierHandler;
import name.modid.core.api.modifiers.config.utils.ModifierUtils;
import name.modid.core.api.modifiers.types.ModifierItemCategory;
import name.modid.core.content.effects.StunnedEffect;
import name.modid.core.content.registries.EffectsRegistry;
import name.modid.core.content.registries.TagsRegistry;
import name.modid.core.utils.guardianSmite.GuardianSmiteHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.ActionResult;

public class EffectHandler {
  // Loot Touch (Restless Flame): no bosses and at most once per 30 seconds for each player,
  // otherwise a mob farm turns into an endless dungeon loot source
  private static final long LOOT_TOUCH_COOLDOWN_TICKS = 30 * 20;
  private static final Map<UUID, Long> LOOT_TOUCH_COOLDOWNS = new HashMap<>();

  private static boolean canApplyEffect(RegistryEntry<StatusEffect> effect, LivingEntity target) {
    return !effect.equals(EffectsRegistry.stunnedEntry())
        || !StunnedEffect.isImmuneToStun(target);
  }

  private static boolean canApplyLootTouch(ModifierContext ctx, LivingEntity target) {
    if (target.getType().isIn(TagsRegistry.BOSSES) || !(ctx.getOwner() instanceof PlayerEntity player)) {
      return false;
    }

    long now = ctx.getWorld().getTime();
    Long cooldownEnd = LOOT_TOUCH_COOLDOWNS.get(player.getUuid());
    return cooldownEnd == null || now >= cooldownEnd;
  }

  private static void startLootTouchCooldown(ModifierContext ctx) {
    if (ctx.getOwner() instanceof PlayerEntity player) {
      long now = ctx.getWorld().getTime();
      LOOT_TOUCH_COOLDOWNS.values().removeIf(end -> end <= now);
      LOOT_TOUCH_COOLDOWNS.put(player.getUuid(), now + LOOT_TOUCH_COOLDOWN_TICKS);
    }
  }

  private static void applyHitEffect(
      LivingEntity target,
      RegistryEntry<StatusEffect> effect,
      int durationSeconds,
      int amplifier,
      boolean stacking,
      int maxStackCount) {
    int appliedAmplifier = amplifier;

    if (stacking) {
      StatusEffectInstance current = target.getStatusEffect(effect);
      int maxAmplifier = maxStackCount > 0 ? maxStackCount - 1 : amplifier;

      appliedAmplifier = current == null
          ? amplifier
          : Math.min(current.getAmplifier() + 1, maxAmplifier);
    }

    target.addStatusEffect(new StatusEffectInstance(effect, durationSeconds * 20, appliedAmplifier));

    // A fresh Guardian Smite gets its full set of boosted hits again
    if (effect.equals(EffectsRegistry.guardianSmiteEntry())) {
      GuardianSmiteHelper.resetCharges(target);
    }
  }

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
            if (!canApplyEffect(effect, entity)) {
              continue;
            }

            entity.addStatusEffect(new StatusEffectInstance(effect, maxDuration * 20, maxAmplifier, false, true, true));
          }
        }
      }
    }
  }

  public static class Melee implements ModifierHandler<ModifierConfig.HitEffectMeleeConfig> {
    @Override
    public boolean supports(GemstoneModifier modifier) {
      return modifier.getItemCategory() == ModifierItemCategory.MELEE
          || modifier.getItemCategory() == ModifierItemCategory.TOOLS;
    }

    @Override
    public void apply(ArrayList<GemstoneModifier> modifiers, ModifierContext ctx) {
      // Spam-clicking must not proc on-hit effects with full chance
      if (ctx.getOwner() instanceof PlayerEntity && !ctx.isFullStrengthAttack()) {
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
          boolean stacking = false;
          int maxStackCount = 0;

          for (GemstoneModifier modifier : effectGroup) {
            HitEffectMeleeConfig config = (HitEffectMeleeConfig) modifier.getConfig();
            chances.add(config.chance().get(modifier.getRarityType()));
            maxAmplifier = Math.max(maxAmplifier, config.amplifier());
            maxDuration = Math.max(maxDuration, config.duration());
            stacking = stacking || config.stacking();
            maxStackCount = Math.max(maxStackCount, config.maxStacks());
          }

          double combinedChance = ModifierUtils.cappedProcChance(chances);
          boolean isLootTouch = effect.equals(EffectsRegistry.lootTouchEntry());

          if (canApplyEffect(effect, target)
              && (!isLootTouch || canApplyLootTouch(ctx, target))
              && ModifierUtils.proc(ctx.getWorld(), combinedChance)) {
            applyHitEffect(target, effect, maxDuration, maxAmplifier, stacking, maxStackCount);

            if (isLootTouch) {
              startLootTouchCooldown(ctx);
            }
          }
        }

      }
      ctx.setActionResult(ActionResult.PASS);
    }
  }

  public static class Projectile implements ModifierHandler<ModifierConfig.HitEffectProjectileConfig> {
    @Override
    public boolean supports(GemstoneModifier modifier) {
      return modifier.getItemCategory() == ModifierItemCategory.RANGED;
    }

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
          boolean stacking = false;
          int maxStackCount = 0;

          for (GemstoneModifier modifier : effectGroup) {
            HitEffectProjectileConfig config = (HitEffectProjectileConfig) modifier.getConfig();
            chances.add(config.chance().get(modifier.getRarityType()));
            maxAmplifier = Math.max(maxAmplifier, config.amplifier());
            maxDuration = Math.max(maxDuration, config.duration());
            stacking = stacking || config.stacking();
            maxStackCount = Math.max(maxStackCount, config.maxStacks());
          }

          double combinedChance = ModifierUtils.cappedProcChance(chances);

          if (canApplyEffect(effect, target)
              && ModifierUtils.proc(ctx.getWorld(), combinedChance)) {
            applyHitEffect(target, effect, maxDuration, maxAmplifier, stacking, maxStackCount);
          }
        }
      }
    }
  }
}
