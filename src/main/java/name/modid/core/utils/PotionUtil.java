package name.modid.core.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;

public class PotionUtil {
  public static List<StatusEffectInstance> getPotionEffects(ItemStack stack) {
    PotionContentsComponent contents = stack.get(DataComponentTypes.POTION_CONTENTS);
    if (contents != null) {
      return new ArrayList<StatusEffectInstance>((Collection<? extends StatusEffectInstance>) contents.getEffects());
    }
    return new ArrayList<>();
  }

  public static void setCustomPotionEffects(ItemStack stack, List<StatusEffectInstance> effects) {
    PotionContentsComponent contents = stack.get(DataComponentTypes.POTION_CONTENTS);
    if (contents == null)
      return;

    PotionContentsComponent newContents = new PotionContentsComponent(
        PotionContentsComponent.DEFAULT.potion(),
        contents.customColor(),
        effects);

    stack.set(DataComponentTypes.POTION_CONTENTS, newContents);
  }
}