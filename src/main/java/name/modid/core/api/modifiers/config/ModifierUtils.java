package name.modid.core.api.modifiers.config;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import name.modid.core.api.modifiers.helpers.ModifierGatheringHelper;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.AttributeModifiersComponent.Entry;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
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

  public static double combinedProcChance(Collection<Double> chances) {
    double result = 1.0;
    for (double p : chances) {
      result *= (1.0 - p);
    }
    return 1.0 - result;
  }

  public static void applyStatusEffect(LivingEntity target, StatusEffectInstance effect) {
    if (target == null)
      return;
    target.addStatusEffect(effect);
  }

  public static void applyStatusEffectToTarget(ModifierContext ctx, RegistryEntry<StatusEffect> effect, int duration,
      int amplifier) {
    if (ctx.getTarget() instanceof LivingEntity target) {
      target.addStatusEffect(new StatusEffectInstance(
          effect,
          duration * 20,
          amplifier));
    }
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

        Entry modifierEntry = modifiersComponent.modifiers().stream()
            .filter(x -> x.attribute() == attribute).findFirst()
            .orElse(null);

        if (modifierEntry != null) {
          for (Entry e : modifiersComponent.modifiers()) {
            if (e.attribute() == attribute) {
              v += e.modifier().value();
            }
          }
        }
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

        Entry modifierEntry = modifiersComponent.modifiers().stream()
            .filter(x -> x.attribute() == attribute).findFirst()
            .orElse(null);

        if (modifierEntry != null) {
          for (Entry e : modifiersComponent.modifiers()) {
            if (e.attribute() == attribute) {
              v += e.modifier().value();
            }
          }
        }
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
        player.getEquippedStack(EquipmentSlot.HEAD),
        player.getEquippedStack(EquipmentSlot.CHEST),
        player.getEquippedStack(EquipmentSlot.LEGS),
        player.getEquippedStack(EquipmentSlot.FEET),
        player.getEquippedStack(EquipmentSlot.MAINHAND),
        player.getEquippedStack(EquipmentSlot.OFFHAND))
        .filter(stack -> !stack.isEmpty())
        .map(callback)
        .flatMap(List::stream)
        .toList();
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