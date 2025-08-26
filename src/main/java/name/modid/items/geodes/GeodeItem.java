package name.modid.items.geodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import name.modid.helpers.GemstoneRarity;
import name.modid.helpers.GemstoneType;
import name.modid.helpers.GemstonesRegistrationHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class GeodeItem extends Item {
  protected ArrayList<GemstoneRarity> gemstoneRarities = new ArrayList<>();
  protected ArrayList<GemstoneType> includedGemstones = new ArrayList<>();
  protected ArrayList<Double> dropChances = new ArrayList<>(Arrays.asList(0.6, 0.3, 0.1));

  public GeodeItem(Settings settings, ArrayList<GemstoneRarity> gemstoneRarities,
      ArrayList<GemstoneType> includedGemstones) {
    super(settings);
    this.gemstoneRarities = new ArrayList<>(gemstoneRarities);
    this.includedGemstones = new ArrayList<>(includedGemstones);
  }

  public ItemStack getGemstoneStack() {
    Random random = new Random();
    GemstoneType selectedType = includedGemstones.get(random.nextInt(includedGemstones.size()));
    ArrayList<GemstoneRarity> validRarities = new ArrayList<>(gemstoneRarities);
    ArrayList<Double> adjustedChances = new ArrayList<>();

    for (int i = 0; i < validRarities.size(); i++) {
      if (i < dropChances.size()) {
        adjustedChances.add(dropChances.get(i));
      } else {
        adjustedChances.add(0.0);
      }
    }

    if (validRarities.isEmpty()) {
      return ItemStack.EMPTY;
    }

    double totalChance = adjustedChances.stream().mapToDouble(Double::doubleValue).sum();
    if (totalChance <= 0) {
      return ItemStack.EMPTY;
    }
    adjustedChances.replaceAll(chance -> chance / totalChance);

    double roll = random.nextDouble();
    double cumulativeChance = 0.0;
    GemstoneRarity selectedRarity = validRarities.get(0);
    for (int i = 0; i < validRarities.size(); i++) {
      cumulativeChance += adjustedChances.get(i);
      if (roll <= cumulativeChance) {
        selectedRarity = validRarities.get(i);
        break;
      }
    }

    Item gemstoneItem = null;
    if (selectedType == GemstoneType.RUBY) {
      int index = selectedRarity.getValue();
      List<Item> rubyList = GemstonesRegistrationHelper.getRubyGemstones();

      if (rubyList != null && !rubyList.isEmpty() && index >= 0 && index < rubyList.size()) {
        gemstoneItem = rubyList.get(index);
      }
    } else if (selectedType == GemstoneType.CELESTINE) {
      int index = selectedRarity.getValue();
      List<Item> celestineList = GemstonesRegistrationHelper.getCelestineGemstones();

      if (celestineList != null && !celestineList.isEmpty() && index >= 0
          && index < celestineList.size()) {
        gemstoneItem = celestineList.get(index);
      }
    }

    if (gemstoneItem == null) {
      return ItemStack.EMPTY;
    }

    return new ItemStack(gemstoneItem);
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
  public void appendTooltip(ItemStack itemStack, TooltipContext context, List<Text> tooltip,
      TooltipType type) {
    Text[] rarityTexts = new Text[3];
    for (int i = 0; i < 3; i++) {
      if (i < gemstoneRarities.size()) {
        String rarityName = gemstoneRarities.get(i).toString().toLowerCase();
        String transformedRarityName = Character.toUpperCase(rarityName.charAt(0)) + rarityName.substring(1);

        int color = switch (gemstoneRarities.get(i)) {
          case COMMON -> 0xa8a8a8;
          case UNCOMMON -> 0x5454fc;
          case RARE -> 0xfc54fc;
          case LEGENDARY -> 0xffad00;
          default -> 0xa8a8a8;
        };

        rarityTexts[i] = Text.translatable(transformedRarityName)
            .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color)));
      }
    }
    tooltip.add(Text.translatable("tooltip.gemstones.geode.info", rarityTexts[0], rarityTexts[1],
        rarityTexts[2]).formatted(Formatting.WHITE));
    tooltip.add(Text.literal(""));
    tooltip.add(Text.translatable("tooltip.gemstones.opening_item.info").formatted(Formatting.GRAY));
  }
}
