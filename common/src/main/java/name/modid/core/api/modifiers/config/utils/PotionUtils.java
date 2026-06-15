package name.modid.core.api.modifiers.config.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import name.modid.core.api.components.ComponentsRegistry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

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
    Optional<Identifier> originalPotion = contents.potion()
        .map(potion -> Registries.POTION.getId(potion.value()))
        .or(() -> Optional.ofNullable(stack.get(ComponentsRegistry.originalPotion())));

    PotionContentsComponent newContents = new PotionContentsComponent(
        Optional.empty(),
        contents.customColor(),
        newEffects);

    stack.set(DataComponentTypes.POTION_CONTENTS, newContents);
    originalPotion.ifPresentOrElse(
        id -> stack.set(ComponentsRegistry.originalPotion(), id),
        () -> stack.remove(ComponentsRegistry.originalPotion()));
    stack.set(DataComponentTypes.ITEM_NAME, originalName);
  }

  public static Optional<RegistryEntry<Potion>> getBrewablePotion(ItemStack stack) {
    PotionContentsComponent contents = stack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);
    if (contents.potion().isPresent()) {
      return contents.potion();
    }

    Identifier originalPotion = stack.get(ComponentsRegistry.originalPotion());
    if (originalPotion == null) {
      return Optional.empty();
    }

    RegistryKey<Potion> key = RegistryKey.of(RegistryKeys.POTION, originalPotion);
    return Registries.POTION.getEntry(key).map(entry -> entry);
  }
}
