package name.modid.core.content.items;

import java.util.List;

import name.modid.core.content.items.registries.ItemsRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class GemstoneDustItem extends Item {
  public GemstoneDustItem(Settings settings) {
    super(settings);
  }

  @Override
  public void appendTooltip(ItemStack itemStack, TooltipContext context, List<Text> tooltip,
      TooltipType type) {
    tooltip.add(Text.translatable("item.gemstones.gemstone_dust.info_1",
        Text.translatable("item.gemstones.gemstone_dust.info_grinding").formatted(Formatting.GOLD)));
  }

  public static int getDustYield(ItemStack stack) {
    return stack.getItem() instanceof GemstoneItem gem ? gem.getRarityType().getDustYield() : 0;
  }

  public static boolean isGrindable(ItemStack stack) {
    return getDustYield(stack) > 0;
  }

  public static boolean hasDust(PlayerEntity player, int amount) {
    if (player.getAbilities().creativeMode) {
      return true;
    }

    PlayerInventory inventory = player.getInventory();
    int found = 0;
    for (int i = 0; i < inventory.size() && found < amount; i++) {
      ItemStack stack = inventory.getStack(i);
      if (stack.isOf(ItemsRegistry.gemstoneDust())) {
        found += stack.getCount();
      }
    }
    return found >= amount;
  }

  public static boolean consumeDust(PlayerEntity player, int amount) {
    if (!hasDust(player, amount)) {
      return false;
    }
    if (player.getAbilities().creativeMode) {
      return true;
    }

    PlayerInventory inventory = player.getInventory();
    int remaining = amount;
    for (int i = 0; i < inventory.size() && remaining > 0; i++) {
      ItemStack stack = inventory.getStack(i);
      if (stack.isOf(ItemsRegistry.gemstoneDust())) {
        int taken = Math.min(remaining, stack.getCount());
        stack.decrement(taken);
        remaining -= taken;
      }
    }
    inventory.markDirty();
    return true;
  }
}
