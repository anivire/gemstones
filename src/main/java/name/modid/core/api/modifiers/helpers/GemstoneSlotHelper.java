package name.modid.core.api.modifiers.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import name.modid.core.api.components.ComponentsRegistry;
import name.modid.core.api.components.GemstoneComponent;
import name.modid.core.api.components.GemstoneSlotsComponent;
import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierManager;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.modifiers.types.GemstoneType;
import name.modid.core.content.items.GemstoneItem;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;

public class GemstoneSlotHelper {
  public static final int MAX_SLOTS = 5;

  public static ArrayList<GemstoneComponent> contains(ItemStack itemStack, GemstoneType gemstoneType) {
    ArrayList<GemstoneComponent> gemstones = itemStack.get(ComponentsRegistry.GEMSTONES) != null
        ? new ArrayList<>(Arrays.asList(itemStack.get(ComponentsRegistry.GEMSTONES).gemstones()))
        : new ArrayList<>();

    gemstones.removeIf(g -> g.gemstoneType() != gemstoneType);
    return gemstones;
  }

  public static boolean isItemValid(Item item) {
    return item instanceof PickaxeItem || item instanceof BowItem || item instanceof ArmorItem
        || item instanceof SwordItem || item instanceof AxeItem || item instanceof ShovelItem
        || item instanceof CrossbowItem;
  }

  public static boolean isGemstonesExists(ItemStack itemStack) {
    return itemStack.get(ComponentsRegistry.GEMSTONES) != null;
  }

  public static GemstoneSlotsComponent getGemstonesSlot(ItemStack itemStack) {
    return itemStack.get(ComponentsRegistry.GEMSTONES);
  }

  public static GemstoneComponent[] getGemstones(ItemStack itemStack) {
    if (itemStack == null || itemStack.isEmpty()) {
      return new GemstoneComponent[0];
    }

    GemstoneSlotsComponent slots = itemStack.get(ComponentsRegistry.GEMSTONES);
    return slots != null ? slots.gemstones() : new GemstoneComponent[0];
  }

  public static Integer getFirstEmptySlotIndex(ItemStack itemStack) {
    GemstoneSlotsComponent gemstoneSlots = getGemstonesSlot(itemStack);
    if (gemstoneSlots == null) {
      return null;
    }

    GemstoneComponent[] gemstones = gemstoneSlots.gemstones();
    if (gemstones == null) {
      return null;
    }

    for (int i = 0; i < gemstones.length; i++) {
      GemstoneComponent gemstone = gemstones[i];
      if (gemstone != null && gemstone.gemstoneType() == GemstoneType.EMPTY) {
        return i;
      }
    }

    return null;
  }

  public static ItemStack setGemstoneByIndex(ItemStack itemStack, int index, GemstoneItem gemstone) {
    GemstoneSlotsComponent sourceGemstoneSlots = getGemstonesSlot(itemStack);
    if (sourceGemstoneSlots == null || index < 0 || index >= MAX_SLOTS) {
      return null;
    }

    GemstoneComponent[] gemstones = Arrays.copyOf(sourceGemstoneSlots.gemstones(),
        sourceGemstoneSlots.gemstones().length);

    gemstones[index] = new GemstoneComponent(
        gemstone.getType(),
        gemstone.getRarityType());

    itemStack.set(ComponentsRegistry.GEMSTONES, new GemstoneSlotsComponent(gemstones));
    updateSocketsAttributes(itemStack, itemStack.getItem());

    return itemStack;
  }

  public static void initializeSockets(ItemStack itemStack, Item item) {
    if (isItemValid(item)) {
      GemstoneSlotsComponent currentSlots = itemStack.get(ComponentsRegistry.GEMSTONES);
      if (currentSlots == null || currentSlots.gemstones().length != MAX_SLOTS) {
        GemstoneComponent[] gemstones = new GemstoneComponent[MAX_SLOTS];

        int freeSlots = 1 + new Random().nextInt(2);

        for (int i = 0; i < MAX_SLOTS; i++) {
          gemstones[i] = new GemstoneComponent(GemstoneType.EMPTY, GemstoneQuality.NONE);
          // if (freeSlots != 0) {
          // gemstones[i] = new GemstoneComponent(GemstoneType.EMPTY,
          // GemstoneQuality.NONE);
          // freeSlots--;
          // } else {
          // gemstones[i] = new GemstoneComponent(GemstoneType.LOCKED,
          // GemstoneQuality.NONE);
          // }
        }

        itemStack.set(ComponentsRegistry.GEMSTONES, new GemstoneSlotsComponent(gemstones));
        updateSocketsAttributes(itemStack, item);
      }
    }
  }

  public static void updateSocketsAttributes(ItemStack itemStack, Item item) {
    if (isItemValid(item)) {
      GemstoneComponent[] gemstones = getGemstones(itemStack);
      if (gemstones != null) {
        ArrayList<GemstoneModifier> modifiers = ModifierGatheringHelper.getAttributeModifiers(itemStack);
        ModifierManager.applyAttributeModifiers(modifiers, itemStack);
      }
    }
  }

  public static int getFirstFilledSlotIndex(ItemStack stack) {
    var gems = getGemstones(stack);
    if (gems == null)
      return -1;
    for (int i = 0; i < gems.length; i++) {
      var g = gems[i];
      if (g != null && g.gemstoneType() != GemstoneType.EMPTY)
        return i;
    }
    return -1;
  }

  public static void clearGemstoneAtIndex(ItemStack stack, int index) {
    var slots = getGemstonesSlot(stack);
    if (slots == null)
      return;
    var arr = Arrays.copyOf(slots.gemstones(), slots.gemstones().length);
    if (index < 0 || index >= arr.length)
      return;
    arr[index] = new GemstoneComponent(GemstoneType.EMPTY, GemstoneQuality.NONE);
    stack.set(ComponentsRegistry.GEMSTONES, new GemstoneSlotsComponent(arr));
    updateSocketsAttributes(stack, stack.getItem());
  }

  public static ItemStack makeGemItemFromSocket(ItemStack base, int index) {
    GemstoneSlotsComponent slots = getGemstonesSlot(base);
    if (slots == null)
      return ItemStack.EMPTY;

    GemstoneComponent[] arr = slots.gemstones();
    if (arr == null || index < 0 || index >= arr.length)
      return ItemStack.EMPTY;

    GemstoneComponent comp = arr[index];
    if (comp == null)
      return ItemStack.EMPTY;

    GemstoneType type = comp.gemstoneType();
    GemstoneQuality quality = comp.gemstoneQualityType();
    if (type == null || type == GemstoneType.EMPTY)
      return ItemStack.EMPTY;

    return getGemItemByTypeAndQuality(type, quality);
  }

  public static ItemStack getGemItemByTypeAndQuality(GemstoneType type, GemstoneQuality quality) {
    if (type == null || type == GemstoneType.EMPTY || quality == null)
      return ItemStack.EMPTY;

    for (net.minecraft.item.Item item : name.modid.core.content.items.registries.GemstonesRegistry
        .getGemstonesByType(type)) {
      if (item instanceof name.modid.core.content.items.GemstoneItem gi) {
        if (gi.getRarityType() == quality) {
          return new net.minecraft.item.ItemStack(item);
        }
      }
    }
    return net.minecraft.item.ItemStack.EMPTY;
  }

  public static int getLastFilledSlotIndex(ItemStack stack) {
    GemstoneComponent[] gems = getGemstones(stack);
    if (gems == null)
      return -1;
    for (int i = gems.length - 1; i >= 0; i--) {
      GemstoneComponent g = gems[i];
      if (g != null && g.gemstoneType() != GemstoneType.EMPTY) {
        return i;
      }
    }
    return -1;
  }
}
