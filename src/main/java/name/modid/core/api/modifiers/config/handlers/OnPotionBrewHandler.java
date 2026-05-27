package name.modid.core.api.modifiers.config.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.OnPotionBrewConfig;
import name.modid.core.api.modifiers.config.utils.PotionUtils;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierHandler;
import name.modid.core.api.modifiers.types.ModifierItemCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;

public class OnPotionBrewHandler implements ModifierHandler<ModifierConfig.OnPotionBrewConfig> {
  private final Map<String, java.util.function.BiConsumer<List<GemstoneModifier>, ModifierContext>> handlers = Map.of(
      "ON_POTION_BREW_INCREASE_DURATION", this::handleIncreaseDuration);

  private static final List<String> ORDER = List.of(
      "ON_POTION_BREW_INCREASE_DURATION");

  @Override
  public boolean supports(GemstoneModifier modifier) {
    return modifier.getItemCategory() == ModifierItemCategory.ARMOR;
  }

  @Override
  public void apply(ArrayList<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (modifiers.isEmpty()) {
      return;
    }

    Map<String, List<GemstoneModifier>> grouped = modifiers.stream()
        .collect(Collectors.groupingBy(inst -> ((OnPotionBrewConfig) inst.getConfig()).eventType().getName()));

    for (String key : ORDER) {
      var group = grouped.get(key);
      if (group == null || group.isEmpty())
        continue;

      var fn = handlers.get(key);
      if (fn != null)
        fn.accept(group, ctx);
    }
  }

  private void handleIncreaseDuration(
      List<GemstoneModifier> modifiers,
      ModifierContext ctx) {
    if (ctx.getInventory() == null) {
      return;
    }

    double totalIncreasedDuration = modifiers.stream()
        .mapToDouble(m -> ((OnPotionBrewConfig) m.getConfig()).values().get(m.getRarityType())).sum();

    for (int i = 0; i < 3; i++) {
      ItemStack stack = ctx.getInventory().get(i);

      if (!(stack.getItem() instanceof PotionItem)) {
        continue;
      }

      List<StatusEffectInstance> effects = PotionUtils.getPotionEffects(stack);

      if (effects.isEmpty()) {
        continue;
      }

      List<StatusEffectInstance> newEffects = new ArrayList<>();

      for (StatusEffectInstance effect : effects) {
        newEffects.add(new StatusEffectInstance(
            effect.getEffectType(),
            effect.getDuration() + ((int) totalIncreasedDuration * 20),
            effect.getAmplifier(),
            effect.isAmbient(),
            effect.shouldShowParticles(),
            effect.shouldShowIcon()));
      }

      PotionUtils.setCustomPotionEffects(stack, newEffects);
    }
  }
}
