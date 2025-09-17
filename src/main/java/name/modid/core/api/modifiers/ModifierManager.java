package name.modid.core.api.modifiers;

import java.util.ArrayList;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.handlers.AttributeModifierHandler;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ModifierManager {
  public static void applyAttributeModifiers(ArrayList<GemstoneModifier> modifiers, ItemStack stack) {
    ArrayList<GemstoneModifier> attributeModifiers = new ArrayList<>(modifiers.stream()
        .filter(x -> x.getConfig() instanceof ModifierConfig.AttributeConfig)
        .toList());

    if (!attributeModifiers.isEmpty()) {
      AttributeModifierHandler.apply(new ArrayList<>(attributeModifiers), stack);
    }
  }

  public static void applyModifiers(ArrayList<GemstoneModifier> modifiers, ModifierContext ctx) {
    ArrayList<GemstoneModifier> otherModifiers = new ArrayList<>(modifiers.stream()
        .filter(x -> !(x.getConfig() instanceof ModifierConfig.AttributeConfig))
        .toList());

    if (!otherModifiers.isEmpty()) {
      for (GemstoneModifier modifier : otherModifiers) {
        ModifierConfig c = modifier.getConfig();
        GemstoneQuality rarity = modifier.getRarityType();

        switch (c) {
          case ModifierConfig.HitMeleeConfig config -> {
            double chance = config.chance().get(rarity);
            if (proc(ctx.world(), chance)) {
              // ModifierHandler.handleMeleeEvent(config.eventType(), ctx);
            }
          }
          case ModifierConfig.HitProjectileConfig config -> {
            double chance = config.chance().get(rarity);
            if (proc(ctx.world(), chance)) {
              // ModifierHandler.handleProjectileEvent(config.eventType(), ctx);
            }
          }
          case ModifierConfig.HitEffectMeleeConfig config -> {
            double chance = config.chance().get(rarity);
            if (proc(ctx.world(), chance)) {
              // ModifierHandler.applyStatusEffect(config, ctx.target(), ctx.world());
            }
          }
          case ModifierConfig.HitEffectProjectileConfig config -> {
            double chance = config.chance().get(rarity);
            if (proc(ctx.world(), chance)) {
              // ModifierHandler.applyStatusEffect(config, ctx.target(), ctx.world());
            }
          }
          case ModifierConfig.BlockBreakConfig config -> {
            double chance = config.values().get(rarity);
            if (proc(ctx.world(), chance)) {
              // ModifierHandler.handleBlockBreak(config.eventType(), ctx);
            }
          }
          case ModifierConfig.DamageConfig config -> {
            double chance = config.values().get(rarity);
            if (proc(ctx.world(), chance)) {
              // ModifierHandler.handleDamage(config, ctx);
            }
          }
          case ModifierConfig.AreaEffectConfig config -> {
            double radius = config.radiusLevels().get(rarity);
            // ModifierHandler.handleAreaEffect(config, ctx, radius);
          }
          case ModifierConfig.CustomConditionConfig config -> {
            double chance = config.value().get(rarity);
            if (proc(ctx.world(), chance)) {
              // ModifierHandler.handleCustom(config.eventType(), config, ctx);
            }
          }
          default -> throw new IllegalStateException("Unexpected modifier type: " + c);
        }
      }
    }
  }

  private static boolean proc(World world, double chance) {
    return world.getRandom().nextDouble() < chance;
  }
}