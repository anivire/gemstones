package name.modid.core.api.modifiers.config.utils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.helpers.ModifierHelper;
import name.modid.core.api.modifiers.helpers.ModifierGatheringHelper;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.AttributeModifiersComponent.Entry;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class ModifierUtils {

  public static boolean proc(ServerWorld world, double chance) {
    return world.getRandom().nextDouble() < chance;
  }

  public static double cappedProcChance(Collection<Double> chances) {
    double result = 1.0;
    for (double p : chances) {
      result *= (1.0 - p);
    }
    return 1.0 - result;
  }

  public static List<GemstoneModifier> collectGemstoneModifiersFromAllEquipment(
      ServerPlayerEntity player,
      Class<? extends ModifierConfig> configClass) {
    return collectValuesFromAllEquipment(
        player,
        itemStack -> ModifierGatheringHelper.getModifiers(itemStack, configClass));
  }

  public static double collectAttributeValuesFromArmor(LivingEntity entity, RegistryEntry<EntityAttribute> attribute) {
    double v = 0;

    for (EquipmentSlot slot : EquipmentSlot.values()) {
      if (slot == EquipmentSlot.CHEST
          || slot == EquipmentSlot.FEET
          || slot == EquipmentSlot.HEAD
          || slot == EquipmentSlot.LEGS) {
        ItemStack itemStack = entity.getEquippedStack(slot);
        AttributeModifiersComponent modifiersComponent = itemStack
            .getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);

        v += getAttributeDelta(modifiersComponent, attribute, 1.0);
      }
    }

    return v;
  }

  public static double collectAttributeValuesFromItem(LivingEntity entity, RegistryEntry<EntityAttribute> attribute) {
    double v = 0;

    for (EquipmentSlot slot : EquipmentSlot.values()) {
      if (slot == EquipmentSlot.MAINHAND) {
        ItemStack itemStack = entity.getEquippedStack(slot);
        AttributeModifiersComponent modifiersComponent = itemStack
            .getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);

        v += getAttributeDelta(modifiersComponent, attribute, 1.0);
      }
    }

    return v;
  }

  public static <T> List<T> collectValuesFromArmor(
      ServerPlayerEntity player,
      Function<ItemStack, List<T>> callback) {
    return Stream.of(
        player.getEquippedStack(EquipmentSlot.HEAD),
        player.getEquippedStack(EquipmentSlot.CHEST),
        player.getEquippedStack(EquipmentSlot.LEGS),
        player.getEquippedStack(EquipmentSlot.FEET))
        .filter(stack -> !stack.isEmpty())
        .map(callback)
        .flatMap(List::stream)
        .toList();
  }

  public static <T> List<T> collectValuesFromAllEquipment(
      ServerPlayerEntity player,
      Function<ItemStack, List<T>> callback) {
    return Stream.of(
        EquipmentSlot.HEAD,
        EquipmentSlot.CHEST,
        EquipmentSlot.LEGS,
        EquipmentSlot.FEET,
        EquipmentSlot.MAINHAND,
        EquipmentSlot.OFFHAND)
        .map(slot -> Map.entry(slot, player.getEquippedStack(slot)))
        .filter(entry -> isStackActiveInSlot(entry.getValue(), entry.getKey()))
        .map(Map.Entry::getValue)
        .filter(stack -> !stack.isEmpty())
        .map(callback)
        .flatMap(List::stream)
        .toList();
  }

  public static float getAttributeMultiplier(
      AttributeModifiersComponent modifiersComponent,
      RegistryEntry<EntityAttribute> attribute) {
    return (float) Math.max(0.01, getAttributeValue(modifiersComponent, attribute, 1.0));
  }

  public static double getAttributeDelta(
      AttributeModifiersComponent modifiersComponent,
      RegistryEntry<EntityAttribute> attribute,
      double baseValue) {
    return getAttributeValue(modifiersComponent, attribute, baseValue) - baseValue;
  }

  public static double getAttributeValue(
      AttributeModifiersComponent modifiersComponent,
      RegistryEntry<EntityAttribute> attribute,
      double baseValue) {
    double value = baseValue;
    double multipliedTotal = 1.0;

    for (Entry e : modifiersComponent.modifiers()) {
      if (!e.attribute().equals(attribute)) {
        continue;
      }

      double modifierValue = e.modifier().value();
      Operation operation = e.modifier().operation();

      if (operation == Operation.ADD_VALUE) {
        value += modifierValue;
      } else if (operation == Operation.ADD_MULTIPLIED_BASE) {
        value += baseValue * modifierValue;
      } else if (operation == Operation.ADD_MULTIPLIED_TOTAL) {
        multipliedTotal *= 1.0 + modifierValue;
      }
    }

    return value * multipliedTotal;
  }

  private static boolean isStackActiveInSlot(ItemStack stack, EquipmentSlot slot) {
    return !stack.isEmpty()
        && ModifierHelper.getEquipmentSlot(stack.getItem()) == slot;
  }

  public static ItemStack getSmeltingResult(World world, ItemStack input) {
    Optional<RecipeEntry<SmeltingRecipe>> recipeEntry = world.getRecipeManager()
        .getFirstMatch(RecipeType.SMELTING, new SingleStackRecipeInput(input), world);

    return recipeEntry
        .map(entry -> entry.value().getResult(world.getRegistryManager()))
        .orElse(ItemStack.EMPTY);
  }

  public static boolean isArmorEquiped(LivingEntity livingEntity) {
    if (livingEntity.getEquippedStack(EquipmentSlot.HEAD).isEmpty()
        && livingEntity.getEquippedStack(EquipmentSlot.CHEST).isEmpty()
        && livingEntity.getEquippedStack(EquipmentSlot.LEGS).isEmpty()
        && livingEntity.getEquippedStack(EquipmentSlot.FEET).isEmpty()) {
      return false;
    } else {
      return true;
    }
  }
}
