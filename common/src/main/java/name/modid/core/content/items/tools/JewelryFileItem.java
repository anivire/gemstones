package name.modid.core.content.items.tools;

import java.util.List;

import name.modid.core.api.components.ComponentsRegistry;
import name.modid.core.api.components.PolishingComponent;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.tooltips.TooltipHelper.InlineIcons;
import name.modid.core.api.tooltips.TooltipText;
import name.modid.core.content.items.GemstoneDustItem;
import name.modid.core.content.items.GemstoneItem;
import name.modid.core.content.items.registries.ItemsRegistry;
import name.modid.core.content.items.registries.GemstonesRegistry;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
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

    PolishingComponent progress = gemStack.get(ComponentsRegistry.polishing());
    boolean stagePaid = progress != null && progress.stagePaid();
    int dustCost = getStageDustCost(gemStack);
    if (!stagePaid && !GemstoneDustItem.hasDust(player, dustCost)) {
      if (!world.isClient)
        sendMissingDust(player, dustCost);
      return TypedActionResult.fail(fileStack);
    }

    if (!world.isClient) {
      if (progress == null)
        progress = PolishingComponent.start(PolishingRules.nextStageDurationTicks(world.random));
      gemStack.set(ComponentsRegistry.polishing(), payStage(player, gemStack, progress));
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
      progress = PolishingComponent.start(PolishingRules.nextStageDurationTicks(world.random));
      gemStack.set(ComponentsRegistry.polishing(), progress);
    }

    if (!world.isClient && !progress.stagePaid()) {
      progress = payStage(player, gemStack, progress);
      gemStack.set(ComponentsRegistry.polishing(), progress);
      if (!progress.stagePaid()) {
        player.stopUsingItem();
        return;
      }
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

    tooltip.add(Text.empty());
    tooltip.add(TooltipText.info(Text
        .translatable("item.gemstones.jewelry_file.info_2",
            TooltipText.icon(InlineIcons.MOUSE_RMB),
            Text.translatable("item.gemstones.jewelry_file.info_offhand").formatted(Formatting.GOLD))
        .formatted(Formatting.GRAY)));
    tooltip.add(TooltipText.indented(Text
        .translatable("item.gemstones.jewelry_file.info_dust",
            Text.empty().append(ItemsRegistry.gemstoneDust().getDefaultStack().toHoverableText()))
        .formatted(Formatting.GRAY)));
    tooltip.add(TooltipText.indented(Text
        .translatable("item.gemstones.jewelry_file.info_dust_2",
            Text.translatable("item.gemstones.jewelry_file.info_quality").formatted(Formatting.GOLD))
        .formatted(Formatting.GRAY)));
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
    int completedStages = progress.completedStages() + 1;
    int totalStages = gemItem.getRarityType().getPolishStages();
    if (completedStages >= totalStages) {
      fileStack.set(ComponentsRegistry.polishingUseLock(), true);
      upgradeGem(world, player, gemStack, gemItem);
      spawnSuccessEffects(world, player);
      player.stopUsingItem();
      return;
    }

    PolishingComponent next = payStage(player, gemStack,
        progress.nextStage(PolishingRules.nextStageDurationTicks(world.random)));
    gemStack.set(ComponentsRegistry.polishing(), next);
    world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME,
        SoundCategory.PLAYERS, 0.35f, 1.0f + world.random.nextFloat() * 0.25f);

    if (!next.stagePaid())
      player.stopUsingItem();
  }

  // Dust is paid when a stage starts, an unpaid stage waits until the player has enough dust
  private static PolishingComponent payStage(PlayerEntity player, ItemStack gemStack, PolishingComponent progress) {
    if (progress.stagePaid())
      return progress;

    int dustCost = getStageDustCost(gemStack);
    if (!GemstoneDustItem.consumeDust(player, dustCost)) {
      sendMissingDust(player, dustCost);
      return progress;
    }

    return progress.paid();
  }

  private static int getStageDustCost(ItemStack gemStack) {
    return gemStack.getItem() instanceof GemstoneItem gemItem ? gemItem.getRarityType().getPolishDustCost() : 0;
  }

  private static void sendMissingDust(PlayerEntity player, int amount) {
    player.sendMessage(Text.translatable("item.gemstones.jewelry_file.missing_dust",
        Text.literal(String.valueOf(amount)).formatted(Formatting.GOLD),
        ItemsRegistry.gemstoneDust().getName().copy().formatted(Formatting.GOLD))
        .formatted(Formatting.RED), true);
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
