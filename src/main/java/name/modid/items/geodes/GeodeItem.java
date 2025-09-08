package name.modid.items.geodes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;

import name.modid.Gemstones;
import name.modid.config.data.geodes.GeodesConfig;
import name.modid.config.data.geodes.GeodesRegistry;
import name.modid.helpers.GemstoneRarity;
import name.modid.helpers.GemstoneType;
import name.modid.helpers.GemstonesRegistrationHelper;
import name.modid.helpers.modifiers.tooltips.TooltipHelper.Icons;
import name.modid.helpers.modifiers.tooltips.TooltipHelper.InlineIcons;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
import net.minecraft.world.World;

public class GeodeItem extends Item {
  private final String geodeId;

  public GeodeItem(Settings settings, String id) {
    super(settings);
    this.geodeId = id;
  }

  private GeodesConfig getConfig() {
    return GeodesRegistry.getConfig(this.geodeId);
  }

  public ItemStack getGemstoneStack() {
    GeodesConfig config = getConfig();
    if (config == null)
      return ItemStack.EMPTY;

    Random random = new Random();

    // Gemstone type
    float totalGemChance = config.gemstones.values().stream().reduce(0f, Float::sum);
    float randGem = random.nextFloat() * totalGemChance;

    GemstoneType selectedType = null;
    float cumulativeGem = 0f;
    for (var entry : config.gemstones.entrySet()) {
      cumulativeGem += entry.getValue();
      if (randGem <= cumulativeGem) {
        selectedType = entry.getKey();
        break;
      }
    }
    if (selectedType == null)
      return ItemStack.EMPTY;

    // Gemstone rarity
    float totalRarityChance = config.rarities.values().stream().reduce(0f, Float::sum);
    float randRarity = random.nextFloat() * totalRarityChance;

    GemstoneRarity selectedRarity = null;
    float cumulativeRarity = 0f;
    for (var entry : config.rarities.entrySet()) {
      cumulativeRarity += entry.getValue();
      if (randRarity <= cumulativeRarity) {
        selectedRarity = entry.getKey();
        break;
      }
    }
    if (selectedRarity == null)
      return ItemStack.EMPTY;

    List<Item> candidates = GemstonesRegistrationHelper.getGemstonesByType(selectedType);
    int index = selectedRarity.getValue();

    if (!candidates.isEmpty() && index < candidates.size()) {
      return new ItemStack(candidates.get(index));
    }
    return ItemStack.EMPTY;
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    ItemStack geodeStack = user.getStackInHand(hand);

    if (world.isClient) {
      return TypedActionResult.pass(geodeStack);
    }

    ItemStack gemstoneStack = getGemstoneStack();
    if (gemstoneStack.isEmpty()) {
      return TypedActionResult.fail(geodeStack);
    }

    world.playSound(null, user.getX(), user.getY(), user.getZ(),
        SoundEvents.BLOCK_AMETHYST_CLUSTER_BREAK, SoundCategory.PLAYERS, 0.5F,
        ((world.random.nextFloat() - world.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);

    user.dropItem(gemstoneStack, false);

    geodeStack.decrement(1);
    return TypedActionResult.success(geodeStack, true);
  }

  @Override
  public void appendTooltip(ItemStack itemStack,
      TooltipContext context,
      List<Text> tooltip,
      TooltipType type) {

    tooltip.add(Text
        .translatable("tooltip.gemstones." + Registries.ITEM.getId(itemStack.getItem()).getPath() + ".info")
        .formatted(Formatting.WHITE));

    if (Screen.hasShiftDown()) {
      tooltip.add(Text.empty());
      tooltip.add(Text.translatable("tooltip.gemstones.geode.info").formatted(Formatting.GRAY));

      GeodesConfig config = getConfig();
      if (config != null && !config.gemstones.isEmpty()) {

        float totalWeight = config.gemstones.values().stream().reduce(0f, Float::sum);

        for (var entry : config.gemstones.entrySet()) {
          GemstoneType t = entry.getKey();
          float rawWeight = entry.getValue();

          Item gemItem = getItemFromType(t);
          if (gemItem != null && totalWeight > 0f) {
            ItemStack gemStack = new ItemStack(gemItem);

            MutableText icon = Text.literal(GemstoneType.getGemstoneLiteral(t))
                .setStyle(Style.EMPTY.withFont(Identifier.of(Gemstones.MOD_ID, Icons.INLINE_GEMSTONE.getPath())))
                .formatted(Formatting.WHITE);

            MutableText name = Text.literal("")
                .setStyle(Style.EMPTY.withFont(Identifier.of("minecraft", "default")))
                .append(gemStack.toHoverableText());

            double percent = (rawWeight / totalWeight) * 100.0;
            BigDecimal v = BigDecimal.valueOf(percent)
                .setScale(2, RoundingMode.HALF_UP)
                .stripTrailingZeros();

            MutableText chance = Text.literal(" " + v.toPlainString() + "%")
                .formatted(Formatting.WHITE);

            tooltip.add(Text.literal("∙ ").formatted(Formatting.DARK_GRAY)
                .append(icon).append(Text.literal(" ")).append(name).append(chance));
          }
        }
      }
    }

    MutableText iconInfo = Text.literal(InlineIcons.SHIFT.getSymbol())
        .setStyle(Style.EMPTY.withFont(Identifier.of(Gemstones.MOD_ID, Icons.INLINE.getPath())))
        .formatted(Formatting.WHITE);
    MutableText arrowInfo = Text.literal(" > ")
        .setStyle(Style.EMPTY.withFont(Style.DEFAULT_FONT_ID))
        .formatted(Formatting.DARK_GRAY);
    MutableText actionInfo = Text.literal("Hold Shift to see ")
        .setStyle(Style.EMPTY.withFont(Style.DEFAULT_FONT_ID))
        .formatted(Formatting.YELLOW);
    MutableText keywordInfo = Text.literal("Gemstones Drop Chances")
        .setStyle(Style.EMPTY.withFont(Style.DEFAULT_FONT_ID))
        .formatted(Formatting.GOLD);

    MutableText iconOpen = Text.literal(InlineIcons.MOUSE_RMB.getSymbol())
        .setStyle(Style.EMPTY.withFont(Identifier.of(Gemstones.MOD_ID, Icons.INLINE.getPath())))
        .formatted(Formatting.WHITE);
    MutableText arrowOpen = Text.literal(" > ")
        .setStyle(Style.EMPTY.withFont(Style.DEFAULT_FONT_ID))
        .formatted(Formatting.DARK_GRAY);
    MutableText actionOpen = Text.literal("Right-click in hand to ")
        .setStyle(Style.EMPTY.withFont(Style.DEFAULT_FONT_ID))
        .formatted(Formatting.YELLOW);
    MutableText keywordOpen = Text.literal("Open Geode")
        .setStyle(Style.EMPTY.withFont(Style.DEFAULT_FONT_ID))
        .formatted(Formatting.GOLD);

    tooltip.add(Text.empty());
    tooltip.add(iconInfo.append(arrowInfo).append(actionInfo).append(keywordInfo));
    tooltip.add(iconOpen.append(arrowOpen).append(actionOpen).append(keywordOpen));
  }

  private Item getItemFromType(GemstoneType type) {
    List<Item> list = GemstonesRegistrationHelper.getGemstonesByType(type);
    return list.isEmpty() ? null : list.get(0);
  }
}