package name.modid.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import name.modid.Gemstones;
import name.modid.effects.registration.EffectRegistrationHelper;
import name.modid.helpers.components.ComponentsHelper;
import name.modid.helpers.components.Gemstone;
import name.modid.helpers.components.GemstoneSlots;
import name.modid.helpers.modifiers.ModifierHelper;
import name.modid.helpers.modifiers.category.ModifierAttribute;
import name.modid.helpers.modifiers.category.ModifierMultiplyAttribute;
import name.modid.helpers.modifiers.category.ModifierOnBlockBreak;
import name.modid.helpers.modifiers.category.ModifierOnDamage;
import name.modid.helpers.modifiers.category.ModifierOnHitEffectMelee;
import name.modid.helpers.modifiers.category.ModifierOnHitEffectProjectile;
import name.modid.helpers.modifiers.category.ModifierOnHitMelee;
import name.modid.helpers.modifiers.category.ModifierOnHitProjectile;
import name.modid.helpers.modifiers.instance.GemstoneModifier;
import name.modid.helpers.modifiers.type.EventType;
import name.modid.items.gemstones.GemstoneItem;
import name.modid.particles.ParticlesRegistrationHelper;
import name.modid.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

// TODO: refactor socketing methods
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
    if (gemstoneSlots == null) {
      return null;
    }

    Gemstone[] gemstones = gemstoneSlots.gemstones();
    if (gemstones == null) {
      return null;
    }

    for (int i = 0; i < gemstones.length; i++) {
      Gemstone gemstone = gemstones[i];
      if (gemstone != null && gemstone.gemstoneType() == GemstoneType.EMPTY) {
        return i;
      }
    }

    return null;
  }

  public static ItemStack setGemstoneByIndex(ItemStack itemStack, int index, GemstoneItem gemstone) {
    GemstoneSlots sourceGemstoneSlots = getGemstonesSlot(itemStack);
    if (sourceGemstoneSlots == null || index < 0 || index >= MAX_SLOTS) {
      return null;
    }

    Gemstone[] gemstones = Arrays.copyOf(sourceGemstoneSlots.gemstones(), sourceGemstoneSlots.gemstones().length);

    gemstones[index] = new Gemstone(
        gemstone.getType(),
        gemstone.getRarityType());

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

      int freeSlots = 1 + new Random().nextInt(2);

      for (int i = 0; i < MAX_SLOTS; i++) {
        if (freeSlots != 0) {
          gemstones[i] = new Gemstone(GemstoneType.EMPTY, GemstoneRarity.NONE);
          freeSlots--;
        } else {
          gemstones[i] = new Gemstone(GemstoneType.LOCKED, GemstoneRarity.NONE);
        }
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
        attributeToModifiers.computeIfAbsent(singleModifier.getAttributeEntry(), k -> new ArrayList<>())
            .add(singleModifier);
      } else if (modifier instanceof ModifierMultiplyAttribute multiModifier) {
        for (ModifierAttribute attr : multiModifier.getInstances()) {
          attributeToModifiers.computeIfAbsent(attr.getAttributeEntry(), k -> new ArrayList<>()).add(attr);
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
        totalValue += m.getLevelValues().get(rarity);
      }

      EquipmentSlot slot = ModifierHelper.getEquipmentSlot(item);

      Identifier modifierId = Identifier.of(Gemstones.MOD_ID,
          String.format("%s.%s.%s", mod.getGemstoneType().toString().toLowerCase(),
              mod.getItemCategory().toString().toLowerCase(), slot.name().toLowerCase()));

      EntityAttributeModifier scaledGemstoneModifier = new EntityAttributeModifier(modifierId, totalValue,
          mod.getOperation());

      AttributeModifiersComponent.Entry newEntry = new AttributeModifiersComponent.Entry(attribute,
          scaledGemstoneModifier, ModifierHelper.getAttributeModifierSlot(item));
      combinedModifiersMap.put(entryKey.apply(newEntry), newEntry);
    }

    List<AttributeModifiersComponent.Entry> finalModifiers = new ArrayList<>(combinedModifiersMap.values());

    AttributeModifiersComponent finalComponent = new AttributeModifiersComponent(finalModifiers, true);
    itemStack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, finalComponent);
  }

  // TODO: refactor modifiers gathering, probably move all modifiers to components
  public static void applyOnHitEffectModifiers(ArrayList<ModifierOnHitEffectMelee> gemstoneModifiers,
      Item item, ItemStack itemStack, LivingEntity target, World world) {
    Map<RegistryEntry<StatusEffect>, List<ModifierOnHitEffectMelee>> effectToModifiers = new HashMap<>();
    for (ModifierOnHitEffectMelee modifier : gemstoneModifiers) {
      effectToModifiers.computeIfAbsent(modifier.getEffectEntry(), k -> new ArrayList<>()).add(modifier);
    }

    for (Map.Entry<RegistryEntry<StatusEffect>, List<ModifierOnHitEffectMelee>> statusEntry : effectToModifiers
        .entrySet()) {
      RegistryEntry<StatusEffect> statusEffect = statusEntry.getKey();
      List<ModifierOnHitEffectMelee> modifiers = statusEntry.getValue();

      double combinedProcChance = 0.0;
      ModifierOnHitEffectMelee selectedModifier = null;
      int maxAmplifier = -1;

      for (ModifierOnHitEffectMelee modifier : modifiers) {
        GemstoneRarity rarity = modifier.getRarityType();
        combinedProcChance += modifier.getInflitChanceValues().get(rarity);

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
      effectToModifiers.computeIfAbsent(modifier.getEffectEntry(), k -> new ArrayList<>()).add(modifier);
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
        combinedProcChance += modifier.getInflitChanceValues().get(rarity);

        if (modifier.getAmplifier() > maxAmplifier) {
          maxAmplifier = modifier.getAmplifier();
          selectedModifier = modifier;
        }
      }

      double randomValue = world.getRandom().nextDouble();
      if (randomValue < combinedProcChance && selectedModifier != null) {
        Map<RegistryEntry<StatusEffect>, StatusEffectInstance> activeEffects = target.getActiveStatusEffects();
        StatusEffectInstance existingEffect = activeEffects.get(statusEffect);
        int newAmplifier = selectedModifier.getAmplifier();

        if (existingEffect != null && selectedModifier.isStacking()) {
          newAmplifier = Math.min(existingEffect.getAmplifier() + 1, selectedModifier.getMaxStackCount() - 1);
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
            .addStatusEffect(new StatusEffectInstance(statusEffect, selectedModifier.getDuration() * 20,
                selectedModifier.isStacking() ? newAmplifier : selectedModifier.getAmplifier()));
      }
    }
  }

  public static boolean applyOnBeforeBlockBreakModifiers(ArrayList<ModifierOnBlockBreak> gemstoneModifiers,
      PlayerEntity player, World world, BlockState state, BlockPos pos) {
    Map<EventType, List<ModifierOnBlockBreak>> eventToModifiers = new HashMap<>();
    for (ModifierOnBlockBreak mod : gemstoneModifiers) {
      eventToModifiers.computeIfAbsent(mod.getEventType(), k -> new ArrayList<>()).add(mod);
    }

    for (Map.Entry<EventType, List<ModifierOnBlockBreak>> entry : eventToModifiers.entrySet()) {
      EventType eventType = entry.getKey();
      List<ModifierOnBlockBreak> modifiers = entry.getValue();

      switch (eventType) {
        case SMELTER -> {
          if (world instanceof ServerWorld serverWorld) {
            double combinedProcChance = 0.0;

            for (ModifierOnBlockBreak m : modifiers) {
              GemstoneRarity rarity = m.getRarityType();
              combinedProcChance += m.getLevelValues().get(rarity);
            }

            List<ItemStack> drops = Block.getDroppedStacks(
                state,
                serverWorld,
                pos,
                world.getBlockEntity(pos),
                player,
                player.getMainHandStack());

            if (world.getRandom().nextDouble() < combinedProcChance) {
              List<ItemStack> smeltableDrops = drops.stream()
                  .map(x -> Utils.getSmeltingResult(world, x))
                  .filter(x -> !x.isEmpty())
                  .toList();

              if (!smeltableDrops.isEmpty()) {
                for (ItemStack smelted : smeltableDrops) {
                  Block.dropStack(world, pos, smelted.copy());
                }

                world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);

                ItemStack tool = player.getMainHandStack();
                if (tool.isDamageable()) {
                  tool.damage(1, player, EquipmentSlot.MAINHAND);
                }

                world.playSound(
                    null,
                    pos,
                    SoundEvents.BLOCK_LAVA_EXTINGUISH,
                    SoundCategory.BLOCKS,
                    1.0f,
                    1.0f);
                serverWorld.spawnParticles(
                    ParticleTypes.SMOKE,
                    pos.getX() + 0.5,
                    pos.getY() + 1.0,
                    pos.getZ() + 0.5,
                    10,
                    0.3,
                    0.3,
                    0.3,
                    0.01);
                serverWorld.spawnParticles(
                    ParticleTypes.FLAME,
                    pos.getX() + 0.5,
                    pos.getY() + 0.8,
                    pos.getZ() + 0.5,
                    8,
                    0.2,
                    0.2,
                    0.2,
                    0.01);

                return false;
              } else {
                return true;
              }
            } else {
              return true;
            }
          }
          return true;
        }
        default -> {
          return true;
        }
      }
    }
    return true;

  }

  public static void applyOnBlockBreakModifiers(ArrayList<ModifierOnBlockBreak> gemstoneModifiers,
      PlayerEntity player, World world, BlockState state, BlockPos pos) {
    Map<EventType, List<ModifierOnBlockBreak>> eventToModifiers = new HashMap<>();
    for (ModifierOnBlockBreak mod : gemstoneModifiers) {
      eventToModifiers.computeIfAbsent(mod.getEventType(), k -> new ArrayList<>()).add(mod);
    }

    for (Map.Entry<EventType, List<ModifierOnBlockBreak>> entry : eventToModifiers.entrySet()) {
      EventType eventType = entry.getKey();
      List<ModifierOnBlockBreak> modifiers = entry.getValue();

      switch (eventType) {
        case HEAL -> {
          double combinedProcChance = 0.0;
          double maxHeal = 0.0;

          for (ModifierOnBlockBreak m : modifiers) {
            GemstoneRarity rarity = m.getRarityType();
            combinedProcChance += m.getLevelValues().get(rarity);
            maxHeal += m.getAdditionalLevelValues().get(rarity);
          }

          if (world.getRandom().nextDouble() < combinedProcChance) {
            player.setHealth(player.getHealth() + (float) maxHeal);
          }

          break;
        }

        case EXTRA_HEALTH -> {
          double combinedProcChance = 0.0;
          int maxStack = 0;
          double valuePerProc = 0.0;

          for (ModifierOnBlockBreak m : modifiers) {
            GemstoneRarity rarity = m.getRarityType();
            combinedProcChance += m.getLevelValues().get(rarity);
            maxStack += m.getAdditionalLevelValues().get(rarity);
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

        case INCREASE_GEODES_DROP -> {
          double combinedProcChance = 0.0;
          if (!(state.isIn(TagsRegistrationHelper.ALL_ORES))) {
            break;
          }

          for (ModifierOnBlockBreak m : modifiers) {
            GemstoneRarity rarity = m.getRarityType();
            combinedProcChance += m.getLevelValues().get(rarity);
          }

          if (world.getRandom().nextDouble() < combinedProcChance) {
            ItemStack geode = state.isIn(TagsRegistrationHelper.DEEPSLATE_ORES)
                ? new ItemStack(ItemRegistrationHelper.DEEPSLATE_GEODE)
                : new ItemStack(ItemRegistrationHelper.STONE_GEODE);

            Block.dropStack(world, pos, geode);
          }
        }
        case ADDITIONAL_GOLD_DROP -> {
          double combinedProcChance = 0.0;
          if (!(state.isIn(TagsRegistrationHelper.ALL_ORES))) {
            break;
          }

          for (ModifierOnBlockBreak m : modifiers) {
            GemstoneRarity rarity = m.getRarityType();
            combinedProcChance += m.getLevelValues().get(rarity);
          }

          if (world.getRandom().nextDouble() < combinedProcChance) {
            ItemStack goldIngot = new ItemStack(Items.GOLD_INGOT);
            goldIngot.setCount(1);

            ItemStack goldNugget = new ItemStack(Items.GOLD_NUGGET);
            goldNugget.setCount(world.getRandom().nextBetween(3, 6));

            Block.dropStack(world, pos, world.getRandom().nextDouble() >= 0.6F ? goldIngot : goldNugget);
          }
        }
        case REGENERATE_BLOCK -> {
          double combinedProcChance = 0.0;

          for (ModifierOnBlockBreak m : modifiers) {
            GemstoneRarity rarity = m.getRarityType();
            combinedProcChance += m.getLevelValues().get(rarity);
          }

          if (world.getRandom().nextDouble() < combinedProcChance) {
            world.setBlockState(pos, state, Block.FORCE_STATE);

            if (world instanceof ServerWorld serverWorld) {
              world.playSound(
                  null,
                  pos,
                  SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
                  SoundCategory.BLOCKS,
                  1.0f,
                  1.0f);

              serverWorld.spawnParticles(
                  ParticleTypes.CLOUD,
                  pos.getX() + 0.5,
                  pos.getY() + 0.5,
                  pos.getZ() + 0.5,
                  20,
                  0.3,
                  0.3,
                  0.3,
                  0.2);
            }
          }

        }
        default -> {
          return;
        }
      }
    }
  }

  public static void applyOnHitMeleeModifiers(
      ArrayList<ModifierOnHitMelee> gemstoneModifiers,
      ItemStack itemStack,
      LivingEntity entity,
      DamageSource source,
      float baseDamageTaken,
      float damageTaken,
      boolean blocked) {
    World world = source.getAttacker().getEntityWorld();
    Entity attacker = source.getAttacker();
    Map<EventType, List<ModifierOnHitMelee>> eventToModifiers = new HashMap<>();
    for (ModifierOnHitMelee mod : gemstoneModifiers) {
      eventToModifiers
          .computeIfAbsent(mod.getEventType(), k -> new ArrayList<>())
          .add(mod);
    }

    for (Map.Entry<EventType, List<ModifierOnHitMelee>> entry : eventToModifiers.entrySet()) {
      EventType eventType = entry.getKey();
      List<ModifierOnHitMelee> modifiers = entry.getValue();

      double combinedProcChance = modifiers.stream()
          .mapToDouble(m -> m.getEventChances().get(m.getRarityType()))
          .sum();

      boolean proc = new Random().nextDouble() < Math.min(combinedProcChance, 1.0);

      switch (eventType) {
        case LIFE_STEAL -> {
          if (attacker instanceof LivingEntity livingEntity && world instanceof ServerWorld serverWorld) {
            if (proc) {
              livingEntity.heal(damageTaken * 0.1F + 1.0F);

              serverWorld.playSound(
                  null,
                  livingEntity.getBlockPos(),
                  SoundEvents.ENTITY_PHANTOM_BITE,
                  SoundCategory.BLOCKS,
                  0.5f,
                  0.8f);

              serverWorld.spawnParticles(
                  ParticleTypes.HEART,
                  livingEntity.getX(),
                  livingEntity.getBodyY(0.5),
                  livingEntity.getZ(),
                  6,
                  0.6, 0.6, 0.6,
                  0.4);
            }
          }
        }
        default -> {
        }
      }
    }
  }

  public static void applyOnHitProjectileModifiers(
      ArrayList<ModifierOnHitProjectile> gemstoneModifiers,
      ItemStack itemStack,
      ServerWorld world,
      Vec3d pos,
      ArrowEntity arrow,
      @Nullable LivingEntity target) {
    Entity attacker = arrow.getOwner();
    Map<EventType, List<ModifierOnHitProjectile>> eventToModifiers = new HashMap<>();
    for (ModifierOnHitProjectile mod : gemstoneModifiers) {
      eventToModifiers
          .computeIfAbsent(mod.getEventType(), k -> new ArrayList<>())
          .add(mod);
    }

    for (Map.Entry<EventType, List<ModifierOnHitProjectile>> entry : eventToModifiers.entrySet()) {
      EventType eventType = entry.getKey();
      List<ModifierOnHitProjectile> modifiers = entry.getValue();

      double combinedProcChance = modifiers.stream()
          .mapToDouble(m -> m.getEventChances().get(m.getRarityType()))
          .sum();

      boolean proc = new Random().nextDouble() < Math.min(combinedProcChance, 1.0);

      switch (eventType) {
        case LIFE_STEAL -> {
          if (attacker instanceof LivingEntity livingEntity && target != null
              && world instanceof ServerWorld serverWorld) {
            if (proc) {
              livingEntity.heal((float) arrow.getDamage() * 0.1F + 1.0F);

              serverWorld.playSound(
                  null,
                  livingEntity.getBlockPos(),
                  SoundEvents.ENTITY_PHANTOM_BITE,
                  SoundCategory.BLOCKS,
                  0.5f,
                  0.8f);

              serverWorld.spawnParticles(
                  ParticleTypes.HEART,
                  livingEntity.getX(),
                  livingEntity.getBodyY(0.5),
                  livingEntity.getZ(),
                  6,
                  0.6, 0.6, 0.6,
                  0.4);
            }
          }
        }
        case LIGHTNING_BOLT -> {
          if (proc && world.isRaining()) {
            LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(world);
            if (lightning != null) {
              lightning.setPosition(pos.getX(), pos.getY(), pos.getZ());
              world.spawnEntity(lightning);
            }
            arrow.discard();
          }
        }

        case COPY_ENTITY_DROP -> {
          if (!proc || target == null)
            break;

          if (target instanceof WitherEntity
              || target instanceof EnderDragonEntity
              || target instanceof ElderGuardianEntity) {
            break;
          }

          LootTable lootTable = world.getServer()
              .getReloadableRegistries()
              .getLootTable(target.getLootTable());

          LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder(world)
              .add(LootContextParameters.ORIGIN, Vec3d.ofCenter(target.getBlockPos()))
              .add(LootContextParameters.THIS_ENTITY, target)
              .add(LootContextParameters.DAMAGE_SOURCE, world.getDamageSources().arrow(arrow, arrow.getOwner()))
              .addOptional(LootContextParameters.ATTACKING_ENTITY, arrow.getOwner());

          LootContextParameterSet lootContext = builder.build(LootContextTypes.ENTITY);
          List<ItemStack> loot = lootTable.generateLoot(lootContext);

          for (ItemStack stack : loot) {
            Block.dropStack(world, target.getBlockPos(), stack);
          }
        }

        case SMALL_FLAT_EXPLOSION -> {
          if (proc) {
            // safe block/entity explosion
            double x = target != null ? target.getX() : pos.getX();
            double y = target != null ? target.getY() : pos.getY();
            double z = target != null ? target.getZ() : pos.getZ();

            world.createExplosion(
                target,
                null,
                null,
                x, y, z,
                3,
                false,
                World.ExplosionSourceType.BLOCK);

            arrow.discard();
          }
        }

        default -> {
        }
      }
    }
  }

  public static void applyOnDamageModifiers(ArrayList<ModifierOnDamage> gemstoneModifiers,
      LivingEntity entity, World world) {
    Map<EventType, List<ModifierOnDamage>> eventToModifiers = new HashMap<>();
    for (ModifierOnDamage mod : gemstoneModifiers) {
      eventToModifiers.computeIfAbsent(mod.getEventType(), k -> new ArrayList<>()).add(mod);
    }

    for (Map.Entry<EventType, List<ModifierOnDamage>> entry : eventToModifiers.entrySet()) {
      EventType eventType = entry.getKey();
      List<ModifierOnDamage> modifiers = entry.getValue();

      if (eventType == EventType.EXTRA_HEALTH) {
        double combinedProcChance = 0.0;
        int maxStack = 0;
        double valuePerProc = 0.0;

        for (ModifierOnDamage m : modifiers) {
          GemstoneRarity rarity = m.getRarityType();
          combinedProcChance += m.getValues().get(rarity);
          maxStack += m.getAdditionalValues().get(rarity);
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

  private static boolean isBowOrCrossbow(ItemStack stack) {
    return stack.getItem() instanceof BowItem || stack.getItem() instanceof CrossbowItem;
  }

  public static ItemStack getWeaponStack(PlayerEntity player) {
    if (player == null) {
      return null;
    }

    ItemStack mainHand = player.getMainHandStack();
    ItemStack offHand = player.getOffHandStack();

    if (isBowOrCrossbow(mainHand)) {
      return mainHand;
    } else if (isBowOrCrossbow(offHand)) {
      return offHand;
    }

    return null;
  }
}
