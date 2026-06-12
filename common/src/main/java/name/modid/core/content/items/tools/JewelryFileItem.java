package name.modid.core.content.items.tools;

import java.util.List;

import name.modid.Gemstones;
import name.modid.core.api.components.ComponentsRegistry;
import name.modid.core.api.components.PolishingComponent;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.tooltips.TooltipHelper.Icons;
import name.modid.core.api.tooltips.TooltipHelper.InlineIcons;
import name.modid.core.content.items.GemstoneItem;
import name.modid.core.content.items.registries.GemstonesRegistry;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
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
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class JewelryFileItem extends Item implements ConsumableTool {

  public JewelryFileItem(Settings settings) {
    super(settings);
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
    ItemStack fileStack = player.getStackInHand(hand);
    if (hand != Hand.MAIN_HAND)
      return TypedActionResult.pass(fileStack);
    if (fileStack.contains(ComponentsRegistry.polishingUseLock()))
      return TypedActionResult.fail(fileStack);

    ItemStack gemStack = player.getOffHandStack();
    if (!canPolishGem(gemStack))
      return TypedActionResult.pass(fileStack);

    if (!world.isClient && !gemStack.contains(ComponentsRegistry.polishing())) {
      gemStack.set(ComponentsRegistry.polishing(),
          new PolishingComponent(0, 0, PolishingRules.nextStageDurationTicks(world.random)));
    }

    player.setCurrentHand(hand);
    return TypedActionResult.consume(fileStack);
  }

  @Override
  public int getMaxUseTime(ItemStack stack, net.minecraft.entity.LivingEntity user) {
    return 72000;
  }

  @Override
  public void usageTick(World world, net.minecraft.entity.LivingEntity user, ItemStack fileStack,
      int remainingUseTicks) {
    if (!(user instanceof PlayerEntity player))
      return;
    if (!(fileStack.getItem() instanceof JewelryFileItem))
      return;

    ItemStack gemStack = player.getOffHandStack();
    if (!canPolishGem(gemStack)) {
      if (!world.isClient)
        gemStack.remove(ComponentsRegistry.polishing());
      player.stopUsingItem();
      return;
    }

    PolishingComponent progress = gemStack.get(ComponentsRegistry.polishing());
    if (progress == null) {
      if (world.isClient)
        return;
      progress = new PolishingComponent(0, 0, PolishingRules.nextStageDurationTicks(world.random));
      gemStack.set(ComponentsRegistry.polishing(), progress);
    }

    if (world.isClient) {
      spawnGemParticles(world, player, gemStack);
      return;
    }

    int nextTick = progress.ticksInStage() + 1;
    if (nextTick < progress.stageDuration()) {
      gemStack.set(ComponentsRegistry.polishing(), progress.withTickProgress(nextTick));
      playWorkingSound(world, player, nextTick);
      return;
    }

    completeStage(world, player, fileStack, gemStack, progress);
  }

  @Override
  public ItemStack finishUsing(ItemStack stack, World world, net.minecraft.entity.LivingEntity user) {
    return stack;
  }

  @Override
  public void onStoppedUsing(ItemStack stack, World world, net.minecraft.entity.LivingEntity user,
      int remainingUseTicks) {
  }

  @Override
  public UseAction getUseAction(ItemStack stack) {
    return UseAction.BRUSH;
  }

  @Override
  public boolean isItemBarVisible(ItemStack stack) {
    return stack.isDamageable() && stack.getDamage() > 0;
  }

  @Override
  public void appendTooltip(ItemStack itemStack, TooltipContext context, List<Text> tooltip,
      TooltipType type) {
    tooltip.add(Text.translatable("item.gemstones.jewelry_file.info_1",
        Text.translatable("item.gemstones.jewelry_file.info_polishing").formatted(Formatting.GOLD)));

    MutableText iconInfo = Text.literal(InlineIcons.INFO.getSymbol())
        .setStyle(Style.EMPTY.withFont(Identifier.of(Gemstones.MOD_ID, Icons.INLINE.getPath())))
        .formatted(Formatting.WHITE);
    MutableText iconUse = Text.literal(InlineIcons.MOUSE_RMB.getSymbol())
        .setStyle(Style.EMPTY.withFont(Identifier.of(Gemstones.MOD_ID, Icons.INLINE.getPath())))
        .formatted(Formatting.WHITE);
    MutableText infoIndent = Text.literal(InlineIcons.INFO_INDENT.getSymbol())
        .setStyle(Style.EMPTY.withFont(Identifier.of(Gemstones.MOD_ID, Icons.INLINE.getPath())));

    tooltip.add(Text.empty());

    MutableText text = Text
        .translatable("item.gemstones.jewelry_file.info_2",
            iconUse,
            Text.translatable("item.gemstones.jewelry_file.info_offhand").formatted(Formatting.GOLD))
        .setStyle(Style.EMPTY.withFont(Style.DEFAULT_FONT_ID))
        .formatted(Formatting.GRAY);

    MutableText breakChanceText = Text
        .translatable("item.gemstones.jewelry_file.info_break_chance",
            Text.translatable("item.gemstones.jewelry_file.info_break_chance_desc").formatted(Formatting.RED))
        .setStyle(Style.EMPTY.withFont(Style.DEFAULT_FONT_ID))
        .formatted(Formatting.GRAY);

    MutableText breakChanceTextPercent = Text
        .translatable("item.gemstones.jewelry_file.info_break_chance_2",
            Text.translatable("item.gemstones.jewelry_file.info_quality").formatted(Formatting.GOLD))
        .setStyle(Style.EMPTY.withFont(Style.DEFAULT_FONT_ID))
        .formatted(Formatting.GRAY);

    tooltip.add(iconInfo.copy().append(text));
    tooltip.add(infoIndent.copy().append(breakChanceText));
    tooltip.add(infoIndent.copy().append(breakChanceTextPercent));
  }

  public static boolean canPolishGem(ItemStack stack) {
    if (!(stack.getItem() instanceof GemstoneItem gemItem))
      return false;
    GemstoneQuality currentQuality = gemItem.getRarityType();
    if (currentQuality == null || currentQuality.getPolishStages() <= 0)
      return false;
    GemstoneQuality nextQuality = currentQuality.next();
    if (nextQuality == null)
      return false;
    return GemstonesRegistry.getGemstonesByType(gemItem.getType()).stream()
        .anyMatch(item -> item instanceof GemstoneItem gi && gi.getRarityType() == nextQuality);
  }

  public static ItemStack getUpgradedGem(GemstoneItem currentGem) {
    GemstoneQuality nextQuality = currentGem.getRarityType().next();
    if (nextQuality == null)
      return ItemStack.EMPTY;
    return GemstonesRegistry.getGemstonesByType(currentGem.getType()).stream()
        .filter(item -> item instanceof GemstoneItem gi && gi.getRarityType() == nextQuality)
        .findFirst()
        .map(Item::getDefaultStack)
        .orElse(ItemStack.EMPTY);
  }

  private void completeStage(World world, PlayerEntity player, ItemStack fileStack, ItemStack gemStack,
      PolishingComponent progress) {
    fileStack.damage(1, player, EquipmentSlot.MAINHAND);

    GemstoneItem gemItem = (GemstoneItem) gemStack.getItem();
    if (world.random.nextFloat() < gemItem.getRarityType().getBreakChance()) {
      breakGem(world, player, gemStack);
      player.stopUsingItem();
      return;
    }

    int completedStages = progress.completedStages() + 1;
    int totalStages = gemItem.getRarityType().getPolishStages();
    if (completedStages >= totalStages) {
      fileStack.set(ComponentsRegistry.polishingUseLock(), true);
      upgradeGem(world, player, gemStack, gemItem);
      spawnSuccessEffects(world, player);
      player.stopUsingItem();
      return;
    }

    gemStack.set(ComponentsRegistry.polishing(),
        progress.nextStage(PolishingRules.nextStageDurationTicks(world.random)));
    world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME,
        SoundCategory.PLAYERS, 0.35f, 1.0f + world.random.nextFloat() * 0.25f);
  }

  private void upgradeGem(World world, PlayerEntity player, ItemStack gemStack, GemstoneItem gemItem) {
    ItemStack upgraded = getUpgradedGem(gemItem);
    if (upgraded.isEmpty()) {
      gemStack.remove(ComponentsRegistry.polishing());
      return;
    }

    if (gemStack.getCount() == 1) {
      player.setStackInHand(Hand.OFF_HAND, upgraded);
      return;
    }

    gemStack.decrement(1);
    gemStack.remove(ComponentsRegistry.polishing());
    if (!player.getInventory().insertStack(upgraded))
      player.dropItem(upgraded, false);
  }

  private void breakGem(World world, PlayerEntity player, ItemStack gemStack) {
    gemStack.remove(ComponentsRegistry.polishing());
    gemStack.decrement(1);
    if (gemStack.isEmpty())
      player.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);

    world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK,
        SoundCategory.PLAYERS, 0.9f, 0.8f + world.random.nextFloat() * 0.2f);
    world.spawnEntity(new ExperienceOrbEntity(world, player.getX(), player.getY() + 0.5, player.getZ(),
        1 + world.random.nextInt(3)));
  }

  private void playWorkingSound(World world, PlayerEntity player, int tickInStage) {
    if (tickInStage % 8 == 0) {
      world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_GRINDSTONE_USE,
          SoundCategory.PLAYERS, 0.35f, 0.9f + world.random.nextFloat() * 0.2f);
    }
  }

  private void spawnGemParticles(World world, PlayerEntity player, ItemStack gem) {
    if (player.age % 2 != 0)
      return;

    Vec3d center = player.getBoundingBox().getCenter().add(player.getRotationVector().multiply(0.28));

    for (int i = 0; i < 3; i++) {
      world.addParticle(
          new ItemStackParticleEffect(ParticleTypes.ITEM, gem),
          center.x + (world.random.nextDouble() - 0.5) * 0.28,
          center.y + world.random.nextDouble() * 0.5,
          center.z + (world.random.nextDouble() - 0.5) * 0.28,
          (world.random.nextDouble() - 0.5) * 0.035,
          0.015 + world.random.nextDouble() * 0.025,
          (world.random.nextDouble() - 0.5) * 0.035);
    }
  }

  private void spawnSuccessEffects(World world, PlayerEntity player) {
    if (world instanceof ServerWorld serverWorld) {
      serverWorld.spawnParticles(ParticleTypes.HAPPY_VILLAGER,
          player.getX(), player.getY() + 1.0, player.getZ(),
          6, 0.25, 0.25, 0.25, 0.05);
    }
    world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME,
        SoundCategory.PLAYERS, 1.0f, 1.55f);
    world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_PLAYER_LEVELUP,
        SoundCategory.PLAYERS, 0.45f, 1.85f);
  }
}
