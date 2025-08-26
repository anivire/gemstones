package name.modid.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import name.modid.Gemstones;
import name.modid.effects.EffectRegistrationHelper;
import name.modid.helpers.components.ComponentsHelper;
import name.modid.helpers.components.Gemstone;
import name.modid.helpers.components.GemstoneSlots;
import name.modid.helpers.modifiers.ModifierHelper;
import name.modid.helpers.modifiers.category.ModifierAttribute;
import name.modid.helpers.modifiers.category.ModifierMultiplyAttribute;
import name.modid.helpers.modifiers.category.ModifierOnBlockBreak;
import name.modid.helpers.modifiers.category.ModifierOnDamage;
import name.modid.helpers.modifiers.category.ModifierOnHitEffect;
import name.modid.helpers.modifiers.category.ModifierOnHitEffectProjectile;
import name.modid.helpers.modifiers.instance.GemstoneModifier;
import name.modid.helpers.modifiers.type.EventType;
import name.modid.items.gemstones.GemstoneItem;
import name.modid.particles.ParticlesRegistrationHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.EquipmentSlot;
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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GemstoneSocketingHelper {
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
    if (itemStack == null || itemStack.isEmpty()) {
      return new Gemstone[0];
    }

    GemstoneSlots slots = itemStack.get(ComponentsHelper.GEMSTONES);
    return slots != null ? slots.gemstones() : new Gemstone[0];
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

    Gemstone[] gemstones = Arrays.copyOf(sourceGemstoneSlots.gemstones(), sourceGemstoneSlots.gemstones().length);

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
        gemstones[i] = new Gemstone(GemstoneType.EMPTY, GemstoneRarity.NONE);
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

    ArrayList<ModifierAttribute> modifiers = ModifierHelper.getAttributeModifiers(itemStack);

    applyAttributeModifiers(modifiers, item, itemStack);
  }

  public static void applyAttributeModifiers(ArrayList<ModifierAttribute> gemstoneModifiers,
      Item item, ItemStack itemStack) {
    @SuppressWarnings("deprecation")
    AttributeModifiersComponent baseModifiers = itemStack.getItem().getAttributeModifiers();
    AttributeModifiersComponent customModifiers = itemStack
        .getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);

    // Using LinkedHashMap for attributes order and eliminate possible duplicates
    Map<String, AttributeModifiersComponent.Entry> combinedModifiersMap = new LinkedHashMap<>();
    Function<AttributeModifiersComponent.Entry, String> entryKey = entry -> entry.modifier().id().toString() + "."
        + entry.slot() + "."
        + entry.attribute().value();

    // Filter modifiers
    baseModifiers.modifiers().forEach(e -> {
      if (!e.modifier().id().getNamespace().equals(Gemstones.MOD_ID)) {
        combinedModifiersMap.put(entryKey.apply(e), e);
      }
    });
    customModifiers.modifiers().forEach(e -> {
      if (!e.modifier().id().getNamespace().equals(Gemstones.MOD_ID)) {
        combinedModifiersMap.put(entryKey.apply(e), e);
      }
    });

    // Gather modifiers
    Map<RegistryEntry<EntityAttribute>, List<ModifierAttribute>> attributeToModifiers = new HashMap<>();
    for (GemstoneModifier modifier : gemstoneModifiers) {
      if (modifier instanceof ModifierAttribute singleModifier) {
        attributeToModifiers.computeIfAbsent(singleModifier.attr, k -> new ArrayList<>())
            .add(singleModifier);
      } else if (modifier instanceof ModifierMultiplyAttribute multiModifier) {
        for (ModifierAttribute attr : multiModifier.instances) {
          attributeToModifiers.computeIfAbsent(attr.attr, k -> new ArrayList<>()).add(attr);
        }
      }
    }

    for (Map.Entry<RegistryEntry<EntityAttribute>, List<ModifierAttribute>> modifierEntry : attributeToModifiers
        .entrySet()) {
      RegistryEntry<EntityAttribute> attribute = modifierEntry.getKey();
      List<ModifierAttribute> modifiers = modifierEntry.getValue();
      ModifierAttribute mod = modifiers.get(0);

      float totalValue = 0f;
      for (ModifierAttribute m : modifiers) {
        GemstoneRarity rarity = m.getRarityType();
        totalValue += m.modifierValuesList.get(rarity.getValue());
      }

      EquipmentSlot slot = ModifierHelper.getEquipmentSlot(item);

      Identifier modifierId = Identifier.of(Gemstones.MOD_ID,
          String.format("%s.%s.%s", mod.gemstoneType.toString().toLowerCase(),
              mod.itemType.toString().toLowerCase(), slot.name().toLowerCase()));

      EntityAttributeModifier scaledGemstoneModifier = new EntityAttributeModifier(modifierId, totalValue,
          mod.operation);

      AttributeModifiersComponent.Entry newEntry = new AttributeModifiersComponent.Entry(attribute,
          scaledGemstoneModifier, ModifierHelper.getAttributeModifierSlot(item));
      combinedModifiersMap.put(entryKey.apply(newEntry), newEntry);
    }

    List<AttributeModifiersComponent.Entry> finalModifiers = new ArrayList<>(combinedModifiersMap.values());

    AttributeModifiersComponent finalComponent = new AttributeModifiersComponent(finalModifiers, true);
    itemStack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, finalComponent);
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
        GemstoneRarity rarity = modifier.getRarityType();
        combinedProcChance += modifier.inflitChance.get(rarity.getValue());

        if (modifier.amplifier > maxAmplifier) {
          maxAmplifier = modifier.amplifier;
          selectedModifier = modifier;
        }
      }

      double randomValue = world.getRandom().nextDouble();
      if (randomValue < combinedProcChance && selectedModifier != null) {
        Map<RegistryEntry<StatusEffect>, StatusEffectInstance> activeEffects = target.getActiveStatusEffects();
        StatusEffectInstance existingEffect = activeEffects.get(statusEffect);
        int newAmplifier = selectedModifier.amplifier;

        if (existingEffect != null && selectedModifier.isStacking) {
          newAmplifier = Math.min(existingEffect.getAmplifier() + 1, selectedModifier.maxStackCount - 1);
        }

        double centerY = target.getY() + target.getHeight() * 0.8;

        if (statusEffect == EffectRegistrationHelper.STUNNED_EFFECT) {
          if (world.isClient) {
            double offsetX = (world.random.nextDouble() - 0.5) * 0.1;
            double offsetY = (world.random.nextDouble() - 0.5) * 0.1;
            double offsetZ = (world.random.nextDouble() - 0.5) * 0.1;
            double velocityX = (world.random.nextDouble() - 0.5) * 0.15;
            double velocityY = -0.05;
            double velocityZ = (world.random.nextDouble() - 0.5) * 0.15;

            world.addParticle(ParticlesRegistrationHelper.STUNNED_PARTICLE, target.getX() + offsetX,
                centerY + offsetY, target.getZ() + offsetZ, velocityX, velocityY, velocityZ);
          }

          if (world instanceof ServerWorld serverWorld) {
            serverWorld.playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.NEUTRAL, 0.5F,
                ((world.random.nextFloat() - world.random.nextFloat()) * 0.2F + 0.7F) * 0.8F);
            serverWorld.spawnParticles(ParticlesRegistrationHelper.STUNNED_PARTICLE, target.getX(),
                centerY, target.getZ(), 8, 0.1, 0.1, 0.1, 0.15);
          }
        }

        target
            .addStatusEffect(new StatusEffectInstance(statusEffect, selectedModifier.duration * 20,
                selectedModifier.isStacking ? newAmplifier : selectedModifier.amplifier));
      }
    }
  }

  public static void applyOnHitEffectProjectileModifiers(
      ArrayList<ModifierOnHitEffectProjectile> gemstoneModifiers, Item item, ItemStack itemStack,
      LivingEntity target, World world) {
    Map<RegistryEntry<StatusEffect>, List<ModifierOnHitEffectProjectile>> effectToModifiers = new HashMap<>();
    for (ModifierOnHitEffectProjectile modifier : gemstoneModifiers) {
      effectToModifiers.computeIfAbsent(modifier.effect, k -> new ArrayList<>()).add(modifier);
    }

    for (Map.Entry<RegistryEntry<StatusEffect>, List<ModifierOnHitEffectProjectile>> statusEntry : effectToModifiers
        .entrySet()) {
      RegistryEntry<StatusEffect> statusEffect = statusEntry.getKey();
      List<ModifierOnHitEffectProjectile> modifiers = statusEntry.getValue();

      double combinedProcChance = 0.0;
      ModifierOnHitEffectProjectile selectedModifier = null;
      int maxAmplifier = -1;

      for (ModifierOnHitEffectProjectile modifier : modifiers) {
        GemstoneRarity rarity = modifier.getRarityType();
        combinedProcChance += modifier.inflitChance.get(rarity.getValue());

        if (modifier.amplifier > maxAmplifier) {
          maxAmplifier = modifier.amplifier;
          selectedModifier = modifier;
        }
      }

      double randomValue = world.getRandom().nextDouble();
      if (randomValue < combinedProcChance && selectedModifier != null) {
        Map<RegistryEntry<StatusEffect>, StatusEffectInstance> activeEffects = target.getActiveStatusEffects();
        StatusEffectInstance existingEffect = activeEffects.get(statusEffect);
        int newAmplifier = selectedModifier.amplifier;

        if (existingEffect != null && selectedModifier.isStacking) {
          newAmplifier = Math.min(existingEffect.getAmplifier() + 1, selectedModifier.maxStackCount - 1);
        }

        double centerY = target.getY() + target.getHeight() * 0.8;

        if (statusEffect == EffectRegistrationHelper.STUNNED_EFFECT) {
          if (world.isClient) {
            double offsetX = (world.random.nextDouble() - 0.5) * 0.1;
            double offsetY = (world.random.nextDouble() - 0.5) * 0.1;
            double offsetZ = (world.random.nextDouble() - 0.5) * 0.1;
            double velocityX = (world.random.nextDouble() - 0.5) * 0.15;
            double velocityY = -0.05;
            double velocityZ = (world.random.nextDouble() - 0.5) * 0.15;

            world.addParticle(ParticlesRegistrationHelper.STUNNED_PARTICLE, target.getX() + offsetX,
                centerY + offsetY, target.getZ() + offsetZ, velocityX, velocityY, velocityZ);
          }

          if (world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticlesRegistrationHelper.STUNNED_PARTICLE, target.getX(),
                centerY, target.getZ(), 8, 0.1, 0.1, 0.1, 0.15);
          }
        }

        target
            .addStatusEffect(new StatusEffectInstance(statusEffect, selectedModifier.duration * 20,
                selectedModifier.isStacking ? newAmplifier : selectedModifier.amplifier));
      }
    }
  }

  public static void applyOnBlockBreakModifiers(ArrayList<ModifierOnBlockBreak> gemstoneModifiers,
      PlayerEntity player, World world, BlockState state, BlockPos pos) {
    Map<EventType, List<ModifierOnBlockBreak>> eventToModifiers = new HashMap<>();
    for (ModifierOnBlockBreak mod : gemstoneModifiers) {
      eventToModifiers.computeIfAbsent(mod.eventType, k -> new ArrayList<>()).add(mod);
    }

    for (Map.Entry<EventType, List<ModifierOnBlockBreak>> entry : eventToModifiers.entrySet()) {
      EventType eventType = entry.getKey();
      List<ModifierOnBlockBreak> modifiers = entry.getValue();

      switch (eventType) {
        case EXTRA_HEALTH: {
          double combinedProcChance = 0.0;
          int maxStack = 0;
          double valuePerProc = 0.0;

          for (ModifierOnBlockBreak m : modifiers) {
            GemstoneRarity rarity = m.rarityType;
            combinedProcChance += m.value.get(rarity.getValue());
            maxStack += m.additionalValue.get(rarity.getValue());
            valuePerProc = 1.0;
          }

          int buffDuration = maxStack < 3 ? 1800 : maxStack <= 5 ? 3600 : 4800;

          if (world.getRandom().nextDouble() < combinedProcChance) {
            float current = player.getAbsorptionAmount();
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, buffDuration,
                (int) maxStack - 1, false, false, true));
            player.setAbsorptionAmount(current + (float) valuePerProc);
          }

          break;
        }

        case INCREASE_GEODES_DROP: {
          double combinedProcChance = 0.0;

          if (!(state.isIn(TagsRegistrationHelper.ALL_ORES))) {
            break;
          }

          for (ModifierOnBlockBreak m : modifiers) {
            GemstoneRarity rarity = m.rarityType;
            combinedProcChance += m.value.get(rarity.getValue());
          }

          if (world.getRandom().nextDouble() < combinedProcChance) {
            ItemStack geode = state.isIn(TagsRegistrationHelper.DEEPSLATE_ORES)
                ? new ItemStack(ItemRegistrationHelper.DEEPSLATE_GEODE)
                : new ItemStack(ItemRegistrationHelper.STONE_GEODE);

            Block.dropStack(world, pos, geode);
          }

          break;
        }
        default: {
          break;
        }
      }
    }
  }

  public static void applyOnDamageModifiers(ArrayList<ModifierOnDamage> gemstoneModifiers,
      LivingEntity entity, World world) {
    Map<EventType, List<ModifierOnDamage>> eventToModifiers = new HashMap<>();
    for (ModifierOnDamage mod : gemstoneModifiers) {
      eventToModifiers.computeIfAbsent(mod.eventType, k -> new ArrayList<>()).add(mod);
    }

    for (Map.Entry<EventType, List<ModifierOnDamage>> entry : eventToModifiers.entrySet()) {
      EventType eventType = entry.getKey();
      List<ModifierOnDamage> modifiers = entry.getValue();

      if (eventType == EventType.EXTRA_HEALTH) {
        double combinedProcChance = 0.0;
        int maxStack = 0;
        double valuePerProc = 0.0;

        for (ModifierOnDamage m : modifiers) {
          GemstoneRarity rarity = m.rarityType;
          combinedProcChance += m.value.get(rarity.getValue());
          maxStack += m.additionalValue.get(rarity.getValue());
          valuePerProc = 1.0;
        }

        int buffDuration = 600;

        if (world.getRandom().nextDouble() < combinedProcChance) {
          float current = entity.getAbsorptionAmount();
          entity.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, buffDuration,
              (int) maxStack - 1, false, false, true));
          entity.setAbsorptionAmount(current + (float) valuePerProc);
        }
      }
    }
  }
}
