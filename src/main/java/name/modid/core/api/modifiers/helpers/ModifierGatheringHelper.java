package name.modid.core.api.modifiers.helpers;

import java.util.ArrayList;

import name.modid.Gemstones;
import name.modid.core.api.components.GemstoneComponent;
import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.AttributeConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.MultiplyAttributeConfig;
import name.modid.core.api.modifiers.types.EventType;
import name.modid.core.api.modifiers.types.GemstoneType;
import net.minecraft.item.ItemStack;

public class ModifierGatheringHelper {
  public static ArrayList<GemstoneModifier> getAttributeModifiers(ItemStack itemStack) {
    ArrayList<GemstoneModifier> modifiers = new ArrayList<>();
    GemstoneComponent[] gemstones = GemstoneSlotHelper.getGemstones(itemStack);

    for (int i = 0; i < gemstones.length; i++) {
      GemstoneComponent gem = gemstones[i];
      if (gem.gemstoneType() == null
          || gem.gemstoneType() == GemstoneType.LOCKED
          || gem.gemstoneType() == GemstoneType.EMPTY
          || gem.gemstoneType() == GemstoneType.UNDEFINED) {
        continue;
      }

      GemstoneModifier modifier = ModifierHelper.getGemstoneModifierForItem(
          gem.gemstoneType(),
          gem.gemstoneQualityType(),
          itemStack.getItem());

      if (modifier == null) {
        Gemstones.LOGGER.error("[WARN] No modifier found for gem=" + gem.gemstoneType()
            + " quality=" + gem.gemstoneQualityType()
            + " item=" + itemStack.getItem());
        continue;
      }

      if (BoostHelper.isBooster(modifier)) {
        continue;
      }

      modifier = BoostHelper.applyBoost(gemstones, i, itemStack.getItem(), modifier);

      if (modifier.getConfig() instanceof AttributeConfig
          || modifier.getConfig() instanceof MultiplyAttributeConfig) {
        modifiers.add(modifier);
      }
    }

    return modifiers;
  }

  @SuppressWarnings("unchecked")
  public static <T extends GemstoneModifier> ArrayList<T> getModifiers(ItemStack itemStack,
      Class<?> wantedClass) {
    ArrayList<T> result = new ArrayList<>();
    GemstoneComponent[] gemstones = GemstoneSlotHelper.getGemstones(itemStack);

    for (int i = 0; i < gemstones.length; i++) {
      GemstoneComponent gem = gemstones[i];
      if (gem.gemstoneType() == null
          || gem.gemstoneType() == GemstoneType.LOCKED
          || gem.gemstoneType() == GemstoneType.EMPTY
          || gem.gemstoneType() == GemstoneType.UNDEFINED) {
        continue;
      }

      GemstoneModifier modifier = ModifierHelper.getGemstoneModifierForItem(
          gem.gemstoneType(),
          gem.gemstoneQualityType(),
          itemStack.getItem());

      if (modifier == null) {
        Gemstones.LOGGER.error("[WARN] No modifier found for gem=" + gem.gemstoneType()
            + " quality=" + gem.gemstoneQualityType()
            + " item=" + itemStack.getItem());
        continue;
      }

      modifier = BoostHelper.applyBoost(gemstones, i, itemStack.getItem(), modifier);

      if (wantedClass.isInstance(modifier.getConfig())) {
        result.add((T) modifier);
      }
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  public static <T extends GemstoneModifier> ArrayList<T> getModifiersByEventType(
      ItemStack itemStack,
      EventType wantedEventType) {
    ArrayList<T> result = new ArrayList<>();
    GemstoneComponent[] gemstones = GemstoneSlotHelper.getGemstones(itemStack);

    for (int i = 0; i < gemstones.length; i++) {
      GemstoneComponent gem = gemstones[i];
      if (gem.gemstoneType() == null
          || gem.gemstoneType() == GemstoneType.LOCKED
          || gem.gemstoneType() == GemstoneType.EMPTY
          || gem.gemstoneType() == GemstoneType.UNDEFINED) {
        continue;
      }

      GemstoneModifier modifier = ModifierHelper.getGemstoneModifierForItem(
          gem.gemstoneType(),
          gem.gemstoneQualityType(),
          itemStack.getItem());

      if (modifier == null) {
        Gemstones.LOGGER.error("[WARN] No modifier found for gem=" + gem.gemstoneType()
            + " quality=" + gem.gemstoneQualityType()
            + " item=" + itemStack.getItem());
        continue;
      }

      modifier = BoostHelper.applyBoost(gemstones, i, itemStack.getItem(), modifier);

      ModifierConfig config = modifier.getConfig();
      if (config instanceof ModifierConfig.Events eventConfig
          && eventConfig.eventType() == wantedEventType) {
        result.add((T) modifier);
      }
    }

    return result;
  }
}
