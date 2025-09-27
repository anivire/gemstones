package name.modid.core.api.modifiers.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class PotionUtils {
  public static List<StatusEffectInstance> getPotionEffects(ItemStack stack) {
    PotionContentsComponent contents = stack.get(DataComponentTypes.POTION_CONTENTS);
    if (contents != null) {
      List<StatusEffectInstance> effects = new ArrayList<>();
      for (StatusEffectInstance effect : contents.getEffects()) {
        effects.add(effect);
      }
      return effects;
    }
    return new ArrayList<>();
  }

  public static void setCustomPotionEffects(ItemStack stack, List<StatusEffectInstance> newEffects) {
    PotionContentsComponent contents = stack.get(DataComponentTypes.POTION_CONTENTS);
    if (contents == null)
      return;

    Text originalName = stack.getName();

    PotionContentsComponent newContents = new PotionContentsComponent(
        Optional.empty(),
        contents.customColor(),
        newEffects);

    stack.set(DataComponentTypes.POTION_CONTENTS, newContents);
    stack.set(DataComponentTypes.ITEM_NAME, originalName);
  }
}