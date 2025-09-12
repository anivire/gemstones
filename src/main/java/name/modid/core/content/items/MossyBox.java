package name.modid.core.content.items;

import java.util.List;

import name.modid.Gemstones;
import name.modid.core.api.modifiers.tooltips.TooltipHelper.Icons;
import name.modid.core.api.modifiers.tooltips.TooltipHelper.InlineIcons;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
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

    ServerWorld serverWorld = (ServerWorld) world;

    world.playSound(null, user.getX(), user.getY(), user.getZ(),
        SoundEvents.BLOCK_BARREL_OPEN, SoundCategory.PLAYERS, 0.5F,
        ((world.random.nextFloat() - world.random.nextFloat()) * 0.7F + 1.5F) * 2.5F);
    world.playSound(null, user.getX(), user.getY(), user.getZ(),
        SoundEvents.BLOCK_VINE_PLACE, SoundCategory.PLAYERS, 0.4F,
        1.0F);

    LootTable lootTable = serverWorld.getServer()
        .getReloadableRegistries()
        .getLootTable(RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of(Gemstones.MOD_ID, "mossy_box_loot")));
    LootContextParameterSet.Builder ctxBuilder = new LootContextParameterSet.Builder(serverWorld)
        .add(LootContextParameters.ORIGIN, user.getPos())
        .add(LootContextParameters.THIS_ENTITY, user);
    LootContextParameterSet ctx = ctxBuilder.build(LootContextTypes.CHEST);

    lootTable.generateLoot(ctx).forEach(lootStack -> user.dropItem(lootStack, false));
    geodeStack.decrement(1);

    return TypedActionResult.success(geodeStack, true);
  }

  @Override
  public void appendTooltip(ItemStack itemStack, TooltipContext context, List<Text> tooltip,
      TooltipType type) {
    tooltip.add(Text.translatable("tooltip.gemstones.mossy_box.info"));
    tooltip.add(Text.empty());

    MutableText iconOpen = Text.literal(InlineIcons.MOUSE_RMB.getSymbol())
        .setStyle(Style.EMPTY.withFont(Identifier.of(Gemstones.MOD_ID, Icons.INLINE.getPath())))
        .formatted(Formatting.WHITE);
    MutableText arrowOpen = Text.literal(" > ")
        .setStyle(Style.EMPTY.withFont(Style.DEFAULT_FONT_ID))
        .formatted(Formatting.DARK_GRAY);
    MutableText actionOpen = Text.literal("Right-click in hand to ")
        .setStyle(Style.EMPTY.withFont(Style.DEFAULT_FONT_ID))
        .formatted(Formatting.YELLOW);
    MutableText keywordOpen = Text.literal("Open Box")
        .setStyle(Style.EMPTY.withFont(Style.DEFAULT_FONT_ID))
        .formatted(Formatting.GOLD);

    tooltip.add(iconOpen.append(arrowOpen).append(actionOpen).append(keywordOpen));
  }
}
