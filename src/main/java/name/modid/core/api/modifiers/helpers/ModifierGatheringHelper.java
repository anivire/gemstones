package name.modid.core.api.modifiers.helpers;

import java.util.ArrayList;

import name.modid.core.api.components.GemstoneComponent;
import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig.AttributeConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.MultiplyAttributeConfig;
import name.modid.core.api.modifiers.types.GemstoneType;
import net.minecraft.item.ItemStack;

public class ModifierGatheringHelper {
  public static ArrayList<GemstoneModifier> getAttributeModifiers(ItemStack itemStack) {
    ArrayList<GemstoneModifier> modifiers = new ArrayList<>();

    for (GemstoneComponent gem : GemstoneSlotHelper.getGemstones(itemStack)) {
      if (gem.gemstoneType() == null || gem.gemstoneType() == GemstoneType.LOCKED) {
        continue;
      }

      GemstoneModifier modifier = ModifierHelper.getGemstoneModifierForItem(
          gem.gemstoneType(),
          gem.gemstoneQualityType(),
          itemStack.getItem());

      if (modifier.getConfig() instanceof AttributeConfig
          || modifier.getConfig() instanceof MultiplyAttributeConfig) {
        modifiers.add(modifier);
      }
    }

    return modifiers;
  }

  @SuppressWarnings("unchecked")
  private static <T extends GemstoneModifier> ArrayList<T> getModifiers(ItemStack stack, Class<T> wantedClass) {
    ArrayList<T> result = new ArrayList<>();
    for (GemstoneComponent gem : GemstoneSlotHelper.getGemstones(stack)) {
      if (gem.gemstoneType() == null || gem.gemstoneType() == GemstoneType.LOCKED)
        continue;

      GemstoneModifier modifier = ModifierHelper.getGemstoneModifierForItem(
          gem.gemstoneType(),
          gem.gemstoneQualityType(),
          stack.getItem());

      if (wantedClass.isInstance(modifier.getConfig())) {
        result.add((T) modifier);
      }
    }
    return result;
  }
}
