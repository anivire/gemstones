package name.modid.core.api.modifiers.config.handlers;

import java.util.ArrayList;
import java.util.List;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.OnPotionBrewConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierHandler;
import name.modid.core.api.modifiers.config.PotionUtils;
import name.modid.core.api.modifiers.types.EventType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;

public class OnPotionBrewHandler
    implements ModifierHandler<ModifierConfig.OnPotionBrewConfig> {
  @Override
  public void apply(ArrayList<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (modifiers.isEmpty())
      return;

    EventType type = ((OnPotionBrewConfig) modifiers.get(0).getConfig()).eventType();

    switch (type) {
      case ON_POTION_BREW_INCREASE_DURATION -> handleIncreaseDuration(modifiers, ctx);
      default -> {
      }
    }
  }

  private void handleIncreaseDuration(
      List<GemstoneModifier> modifiers,
      ModifierContext ctx) {
    if (ctx.getInventory() == null) {
      return;
    }

    double totalIncreasedDurationValue = 0.0;
    for (GemstoneModifier modifier : modifiers) {
      OnPotionBrewConfig config = (OnPotionBrewConfig) modifier.getConfig();
      totalIncreasedDurationValue += config.values().get(modifier.getRarityType());
    }

    for (int i = 0; i < 3; i++) {
      ItemStack stack = ctx.getInventory().get(i);
      if (!(stack.getItem() instanceof PotionItem))
        continue;

      List<StatusEffectInstance> effects = PotionUtils.getPotionEffects(stack);

      if (effects.isEmpty())
        continue;

      List<StatusEffectInstance> newEffects = new ArrayList<>();
      for (StatusEffectInstance effect : effects) {
        newEffects.add(new StatusEffectInstance(
            effect.getEffectType(),
            effect.getDuration() + ((int) totalIncreasedDurationValue * 20),
            effect.getAmplifier(),
            effect.isAmbient(),
            effect.shouldShowParticles(),
            effect.shouldShowIcon()));
      }

      PotionUtils.setCustomPotionEffects(stack, newEffects);
    }
  }
}