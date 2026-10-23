package name.modid.core.utils;

import name.modid.core.api.modifiers.config.utils.ModifierUtils;
import name.modid.core.content.registries.AttributesRegistry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.item.ItemStack;

public final class BowPullHelper {
  private static final float VANILLA_PULL_TICKS = 20.0F;

  private BowPullHelper() {
  }

  // Same curve as BowItem.getPullProgress, but with the pull speed of this exact bow
  public static float getPullProgress(ItemStack bowStack, int useTicks) {
    AttributeModifiersComponent component = bowStack.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS,
        AttributeModifiersComponent.DEFAULT);
    float multiplier = ModifierUtils.getAttributeMultiplier(component, AttributesRegistry.PULL_SPEED_ATTRIBUTE);

    float progress = useTicks / (VANILLA_PULL_TICKS / multiplier);
    progress = (progress * progress + progress * 2.0F) / 3.0F;
    return Math.min(progress, 1.0F);
  }
}
