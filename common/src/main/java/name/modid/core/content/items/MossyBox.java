package name.modid.core.content.items;

import java.util.List;

import name.modid.Gemstones;
import name.modid.core.api.tooltips.TooltipHelper.Icons;
import name.modid.core.api.tooltips.TooltipHelper.InlineIcons;
import name.modid.datapack.drops.DropsConfig;
import name.modid.datapack.drops.DropsRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class MossyBox extends Item {
  public MossyBox(Settings settings) {
    super(settings);
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    ItemStack geodeStack = user.getStackInHand(hand);

    if (world.isClient) {
      return TypedActionResult.pass(geodeStack);
    }

    world.playSound(null, user.getX(), user.getY(), user.getZ(),
        SoundEvents.BLOCK_BARREL_OPEN, SoundCategory.PLAYERS, 0.5F,
        ((world.random.nextFloat() - world.random.nextFloat()) * 0.7F + 1.5F) * 2.5F);
    world.playSound(null, user.getX(), user.getY(), user.getZ(),
        SoundEvents.BLOCK_VINE_PLACE, SoundCategory.PLAYERS, 0.4F,
        1.0F);

    List<DropsConfig.MossyBoxPool> pools = DropsRegistry.getMossyBoxLoot();
    Random random = world.random;

    for (DropsConfig.MossyBoxPool pool : pools) {
      int rolls = rollCount(random, pool.getMinRolls(), pool.getMaxRolls());

      List<DropsConfig.MossyBoxEntry> entries = pool.getEntries();
      if (entries.isEmpty())
        continue;

      for (int r = 0; r < rolls; r++) {
        DropsConfig.MossyBoxEntry entry = pickWeightedEntry(entries, random);
        if (entry == null)
          continue;

        ItemStack drop = createDrop(entry, random);
        if (drop != null && !drop.isEmpty()) {
          user.dropItem(drop, false);
        }
      }
    }

    geodeStack.decrement(1);

    return TypedActionResult.success(geodeStack, true);
  }

  private static int rollCount(Random random, float minRolls, float maxRolls) {
    float roll = minRolls + random.nextFloat() * (maxRolls - minRolls);
    int count = (int) roll;
    if (random.nextFloat() < (roll - count)) {
      count++;
    }
    return count;
  }

  private static DropsConfig.MossyBoxEntry pickWeightedEntry(List<DropsConfig.MossyBoxEntry> entries, Random random) {
    int totalWeight = 0;
    for (DropsConfig.MossyBoxEntry entry : entries) {
      totalWeight += entry.getWeight();
    }
    if (totalWeight <= 0)
      return null;

    int roll = random.nextInt(totalWeight);
    for (DropsConfig.MossyBoxEntry entry : entries) {
      roll -= entry.getWeight();
      if (roll < 0)
        return entry;
    }
    return null;
  }

  private static ItemStack createDrop(DropsConfig.MossyBoxEntry entry, Random random) {
    Item item = Registries.ITEM.get(entry.getItem());
    if (item == Items.AIR)
      return null;

    int count = entry.getMinCount();
    if (entry.getMaxCount() > entry.getMinCount()) {
      count += random.nextInt(entry.getMaxCount() - entry.getMinCount() + 1);
    }

    ItemStack stack = new ItemStack(item, count);

    if (entry.getDamage() >= 0.0f && stack.isDamageable()) {
      int maxDamage = stack.getMaxDamage();
      stack.setDamage((int) (maxDamage * (1.0f - entry.getDamage())));
    }

    return stack;
  }

  @Override
  public void appendTooltip(ItemStack itemStack, TooltipContext context, List<Text> tooltip,
      TooltipType type) {
    tooltip.add(Text.translatable("tooltip.gemstones.mossy_box.info"));
    tooltip.add(Text.empty());

    MutableText iconInfo = Text.literal(InlineIcons.INFO.getSymbol())
        .setStyle(Style.EMPTY.withFont(Identifier.of(Gemstones.MOD_ID, Icons.INLINE.getPath())))
        .formatted(Formatting.WHITE);
    MutableText iconOpen = Text.literal(InlineIcons.MOUSE_RMB.getSymbol())
        .setStyle(Style.EMPTY.withFont(Identifier.of(Gemstones.MOD_ID, Icons.INLINE.getPath())))
        .formatted(Formatting.WHITE);

    MutableText text = Text
        .translatable("tooltip.gemstones.utils.info_action",
            iconOpen,
            Text.translatable("tooltip.gemstones.utils.info_in_hand"),
            Text.translatable("tooltip.gemstones.mossy_box.info_open").formatted(Formatting.GOLD))
        .setStyle(Style.EMPTY.withFont(Style.DEFAULT_FONT_ID))
        .formatted(Formatting.GRAY);

    tooltip.add(iconInfo.append(text));
  }
}
