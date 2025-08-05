package name.modid.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import name.modid.Gemstones;
import name.modid.helpers.components.ComponentsHelper;
import name.modid.helpers.components.Gemstone;
import name.modid.helpers.components.GemstoneSlots;
import name.modid.helpers.modifiers.GemstoneModifier;
import name.modid.helpers.modifiers.GemstoneModifierHelper;
import name.modid.helpers.modifiers.modifierTypes.EventType;
import name.modid.helpers.modifiers.modifierTypes.ModifierAttribute;
import name.modid.helpers.modifiers.modifierTypes.ModifierMultiplyAttribute;
import name.modid.helpers.modifiers.modifierTypes.ModifierOnBlockBreak;
import name.modid.helpers.modifiers.modifierTypes.ModifierOnHitEffect;
import name.modid.helpers.types.GemstoneRarityType;
import name.modid.helpers.types.GemstoneType;
import name.modid.items.gemstones.GemstoneItem;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ItemGemstoneHelper {
  public static final int MAX_SLOTS = 5;

  public static boolean isItemValid(Item item) {
    return item instanceof PickaxeItem || item instanceof BowItem || item instanceof ArmorItem
        || item instanceof SwordItem || item instanceof AxeItem || item instanceof ShovelItem
        || item instanceof CrossbowItem;
  }

  public static ArrayList<Gemstone> contains(ItemStack itemStack, GemstoneType gemstoneType) {
    ArrayList<Gemstone> gemstones = itemStack.get(ComponentsHelper.GEMSTONES) != null
        ? new ArrayList<>(Arrays.asList(itemStack.get(ComponentsHelper.GEMSTONES).gemstones()))
        : new ArrayList<>();

    gemstones.removeIf(g -> g.gemstoneType() != gemstoneType);
    return gemstones;
  }

  public static boolean isGemstonesExists(ItemStack itemStack) {
    return itemStack.get(ComponentsHelper.GEMSTONES) != null;
  }

  public static Gemstone[] getGemstones(ItemStack itemStack) {
    return itemStack.get(ComponentsHelper.GEMSTONES).gemstones();
  }

  public static GemstoneSlots getGemstonesSlot(ItemStack itemStack) {
    return itemStack.get(ComponentsHelper.GEMSTONES);
  }

  public static Integer getGemstoneFirstEmptyIndex(ItemStack itemStack) {
    GemstoneSlots gemstoneSlots = getGemstonesSlot(itemStack);
    if (gemstoneSlots == null)
      return null;

    Gemstone[] gemstones = gemstoneSlots.gemstones();
    if (gemstones == null)
      return null;

    for (int i = 0; i < gemstones.length; i++) {
      Gemstone gemstone = gemstones[i];
      if (gemstone != null && gemstone.gemstoneType() == GemstoneType.EMPTY) {
        return i;
      }
    }

    return null;
  }

  public static ItemStack setGemstoneByIndex(ItemStack itemStack, int index,
      GemstoneItem gemstone) {
    GemstoneSlots sourceGemstoneSlots = getGemstonesSlot(itemStack);
    if (sourceGemstoneSlots == null && index < 0 || index >= MAX_SLOTS)
      return null;

    Gemstone[] gemstones =
        Arrays.copyOf(sourceGemstoneSlots.gemstones(), sourceGemstoneSlots.gemstones().length);

    gemstones[index] = new Gemstone(gemstone.getType(), gemstone.getRarityType());
    itemStack.set(ComponentsHelper.GEMSTONES, new GemstoneSlots(gemstones));
    updateItemSlotBonuses(itemStack, itemStack.getItem());

    return itemStack;
  }

  public static void initItemSlots(ItemStack itemStack, Item item) {
    if (!isItemValid(item))
      return;

    GemstoneSlots currentSlots = itemStack.get(ComponentsHelper.GEMSTONES);
    if (currentSlots == null || currentSlots.gemstones().length != MAX_SLOTS) {
      Gemstone[] gemstones = new Gemstone[MAX_SLOTS];

      for (int i = 0; i < MAX_SLOTS; i++) {
        gemstones[i] = new Gemstone(GemstoneType.EMPTY, GemstoneRarityType.NONE);
      }

      itemStack.set(ComponentsHelper.GEMSTONES, new GemstoneSlots(gemstones));
      updateItemSlotBonuses(itemStack, item);
    }
  }

  public static void updateItemSlotBonuses(ItemStack itemStack, Item item) {
    if (!isItemValid(item) && !isGemstonesExists(itemStack))
      return;

    Gemstone[] gemstones = getGemstones(itemStack);
    if (gemstones == null)
      return;

    // Get all gemstones from item
    Map<Integer, Map<GemstoneType, GemstoneRarityType>> itemGemstones = new HashMap<>();
    for (int i = 0; i < gemstones.length; i++) {
      Gemstone gem = gemstones[i];

      if (gem.gemstoneType() != null && gem.gemstoneType() != GemstoneType.LOCKED) {
        Map<GemstoneType, GemstoneRarityType> m = new HashMap<>();
        m.put(gem.gemstoneType(), gem.gemstoneRarityType());
        itemGemstones.put(i, m);
      }
    }

    // Collect gemstone modifiers and apply bonuses
    ArrayList<GemstoneModifier> gemstoneModifiers = new ArrayList<>();
    for (Map.Entry<Integer, Map<GemstoneType, GemstoneRarityType>> m : itemGemstones.entrySet()) {
      Map<GemstoneType, GemstoneRarityType> i = m.getValue();

      for (Map.Entry<GemstoneType, GemstoneRarityType> e : i.entrySet()) {
        GemstoneType gemstoneType = e.getKey();
        GemstoneRarityType gemstoneRarity = e.getValue();
        GemstoneModifier gemstoneModifier =
            GemstoneModifierHelper.getGemstoneModifierForItem(gemstoneType, item);

        if (gemstoneModifier != null) {
          if (gemstoneModifier instanceof ModifierAttribute
              || gemstoneModifier instanceof ModifierMultiplyAttribute) {
            if (gemstoneModifier instanceof ModifierMultiplyAttribute gemstoneModifierInstances) {
              for (ModifierAttribute instance : gemstoneModifierInstances.instances) {
                instance.setRarityType(gemstoneRarity);
              }
            } else {
              gemstoneModifier.setRarityType(gemstoneRarity);
            }

            gemstoneModifiers.add(gemstoneModifier);
          }
        }
      }
    }

    applyAttributeModifiers(gemstoneModifiers, item, itemStack);
  }

  public static void applyAttributeModifiers(ArrayList<GemstoneModifier> gemstoneModifiers,
      Item item, ItemStack itemStack) {
    AttributeModifiersComponent itemAttributeModifiers = itemStack
        .getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
    List<AttributeModifiersComponent.Entry> newModifiers = new ArrayList<>();

    // Preserve non-gemstone modifiers
    for (AttributeModifiersComponent.Entry entry : itemAttributeModifiers.modifiers()) {
      if (!entry.modifier().id().getNamespace().equals(Gemstones.MOD_ID)) {
        newModifiers.add(entry);
      }
    }

    // Group modifiers by attribute
    Map<RegistryEntry<EntityAttribute>, List<ModifierAttribute>> attributeToModifiers =
        new HashMap<>();
    for (GemstoneModifier modifier : gemstoneModifiers) {
      if (modifier instanceof ModifierAttribute singleModifier) {
        attributeToModifiers.computeIfAbsent(singleModifier.attr, k -> new ArrayList<>())
            .add(singleModifier);
      } else if (modifier instanceof ModifierMultiplyAttribute multiModifier) {
        // Handle multiple attributes from ModifierMultiplyAttribute
        for (ModifierAttribute attr : multiModifier.instances) {
          attributeToModifiers.computeIfAbsent(attr.attr, k -> new ArrayList<>()).add(attr);
        }
      }
    }

    // Process each attribute and its modifiers
    for (Map.Entry<RegistryEntry<EntityAttribute>, List<ModifierAttribute>> modifierEntry : attributeToModifiers
        .entrySet()) {
      RegistryEntry<EntityAttribute> attribute = modifierEntry.getKey();
      List<ModifierAttribute> modifiers = modifierEntry.getValue();
      ModifierAttribute mod = modifiers.get(0);

      float totalValue = 0.0f;

      for (ModifierAttribute modifier : modifiers) {
        GemstoneRarityType rarity = modifier.getRarityType();
        totalValue += modifier.modifierValuesList.get(rarity.getValue());
      }

      // Create a unique modifier ID
      EntityAttributeModifier scaledGemstoneModifier =
          new EntityAttributeModifier(
              Identifier.of(Gemstones.MOD_ID,
                  String.format("%s_gemstone_%s_modifier_slot%s",
                      mod.gemstoneType.toString().toLowerCase(),
                      mod.itemType.toString().toLowerCase(), UUID.randomUUID().toString())),
              totalValue, mod.operation);

      newModifiers.add(new AttributeModifiersComponent.Entry(attribute, scaledGemstoneModifier,
          GemstoneModifierHelper.getAttributeModifierSlot(item)));
    }

    itemAttributeModifiers =
        new AttributeModifiersComponent(newModifiers, itemAttributeModifiers.showInTooltip());
    itemStack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, itemAttributeModifiers);
  }

  public static void applyOnHitEffectModifiers(ArrayList<ModifierOnHitEffect> gemstoneModifiers,
      Item item, ItemStack itemStack, LivingEntity target, World world) {
    Map<RegistryEntry<StatusEffect>, List<ModifierOnHitEffect>> effectToModifiers = new HashMap<>();
    for (ModifierOnHitEffect modifier : gemstoneModifiers) {
      effectToModifiers.computeIfAbsent(modifier.effect, k -> new ArrayList<>()).add(modifier);
    }

    for (Map.Entry<RegistryEntry<StatusEffect>, List<ModifierOnHitEffect>> statusEntry : effectToModifiers
        .entrySet()) {
      RegistryEntry<StatusEffect> statusEffect = statusEntry.getKey();
      List<ModifierOnHitEffect> modifiers = statusEntry.getValue();

      double combinedProcChance = 0.0;
      ModifierOnHitEffect selectedModifier = null;
      int maxAmplifier = -1;

      for (ModifierOnHitEffect modifier : modifiers) {
        GemstoneRarityType rarity = modifier.getRarityType();
        combinedProcChance += modifier.inflitChance.get(rarity.getValue());

        if (modifier.amplifier > maxAmplifier) {
          maxAmplifier = modifier.amplifier;
          selectedModifier = modifier;
        }
      }

      double randomValue = world.getRandom().nextDouble();
      if (randomValue < combinedProcChance && selectedModifier != null) {
        Map<RegistryEntry<StatusEffect>, StatusEffectInstance> activeEffects =
            target.getActiveStatusEffects();
        StatusEffectInstance existingEffect = activeEffects.get(statusEffect);
        int newAmplifier = selectedModifier.amplifier;

        if (existingEffect != null && selectedModifier.isStacking) {
          newAmplifier =
              Math.min(existingEffect.getAmplifier() + 1, selectedModifier.maxStackCount - 1);
        }

        target
            .addStatusEffect(new StatusEffectInstance(statusEffect, selectedModifier.duration * 20,
                selectedModifier.isStacking ? newAmplifier : selectedModifier.amplifier));
      }
    }
  }

  public static void applyOnBlockBreakModifiers(ArrayList<ModifierOnBlockBreak> gemstoneModifiers,
      PlayerEntity player, World world) {
    Map<EventType, List<ModifierOnBlockBreak>> eventToModifiers = new HashMap<>();
    for (ModifierOnBlockBreak mod : gemstoneModifiers) {
      eventToModifiers.computeIfAbsent(mod.eventType, k -> new ArrayList<>()).add(mod);
    }

    for (Map.Entry<EventType, List<ModifierOnBlockBreak>> entry : eventToModifiers.entrySet()) {
      EventType eventType = entry.getKey();
      List<ModifierOnBlockBreak> modifiers = entry.getValue();

      if (eventType == EventType.EXTRA_HEALTH) {
        double combinedProcChance = 0.0;
        int maxStack = 0;
        double valuePerProc = 0.0;

        for (ModifierOnBlockBreak m : modifiers) {
          GemstoneRarityType rarity = m.rarityType;
          combinedProcChance += m.value.get(rarity.getValue());
          maxStack += m.additionalValue.get(rarity.getValue());
          valuePerProc = 1.0;
        }

        int buffDuration = maxStack > 3 ? 1800 : maxStack > 5 ? 3600 : 4800;

        if (world.getRandom().nextDouble() < combinedProcChance) {
          float current = player.getAbsorptionAmount();
          player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, buffDuration,
              (int) maxStack - 1, false, false, true));
          player.setAbsorptionAmount(current + (float) valuePerProc);
        }
      }
    }
  }
}
