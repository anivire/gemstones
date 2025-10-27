package name.modid.core.mixins.attributes;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.modid.core.content.registries.AttributesRegistry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;

@Mixin(BowItem.class)
public class ProjectileCountAttributeBow {
  @Inject(method = "onStoppedUsing", at = @At("HEAD"), cancellable = true)
  private void onRelease(
      ItemStack bowStack,
      World world,
      LivingEntity user,
      int remainingUseTicks,
      CallbackInfo ci) {

    if (!(user instanceof PlayerEntity player))
      return;

    AttributeModifiersComponent mods = player.getMainHandStack()
        .getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);

    int projectileCount = 1;
    for (var entry : mods.modifiers()) {
      if (AttributesRegistry.PROJECTILE_COUNT_ATTRIBUTE.equals(entry.attribute())) {
        projectileCount += (int) entry.modifier().value();
      }
    }

    int requestedCount = Math.max(1, projectileCount);
    if (requestedCount <= 1)
      return;

    ci.cancel();

    float pullProgress = BowItem.getPullProgress(bowStack.getMaxUseTime(user) - remainingUseTicks);
    if (pullProgress < 0.1F)
      return;

    ItemStack arrowStack = player.getProjectileType(bowStack);
    if (arrowStack.isEmpty())
      return;

    var enchants = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT);
    boolean hasInfinity = enchants.getEntry(Enchantments.INFINITY)
        .map(e -> EnchantmentHelper.getLevel(e, bowStack) > 0)
        .orElse(false);

    fireArrow(world, player, bowStack, arrowStack, pullProgress, 0f);

    for (int i = 1; i < requestedCount; ++i) {
      float side = (i % 2 == 0) ? -1f : 1f;
      float step = (float) Math.ceil(i / 2.0);
      float angle = side * step * 7.0f;
      fireArrow(world, player, bowStack, arrowStack, pullProgress, angle);
    }

    if (!player.isCreative() && !(hasInfinity && arrowStack.isOf(Items.ARROW))) {
      arrowStack.decrement(1);
      if (arrowStack.isEmpty()) {
        player.getInventory().removeOne(arrowStack);
      }
    }

    world.playSound(
        null,
        player.getX(),
        player.getY(),
        player.getZ(),
        SoundEvents.ENTITY_ARROW_SHOOT,
        SoundCategory.PLAYERS,
        1.0F,
        1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + pullProgress * 0.5F);

    player.incrementStat(Stats.USED.getOrCreateStat(bowStack.getItem()));
  }

  private void fireArrow(
      World world, PlayerEntity player, ItemStack bowStack, ItemStack arrowStack,
      float pullProgress, float angleOffset) {

    var enchants = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT);
    ArrowItem arrowItem = (ArrowItem) (arrowStack.getItem() instanceof ArrowItem
        ? arrowStack.getItem()
        : Items.ARROW);

    PersistentProjectileEntity arrow = arrowItem.createArrow(world, arrowStack.copy(), player, bowStack);

    if (pullProgress == 1.0F)
      arrow.setCritical(true);

    int power = enchants.getEntry(Enchantments.POWER)
        .map(e -> EnchantmentHelper.getLevel(e, bowStack)).orElse(0);
    if (power > 0)
      arrow.setDamage(arrow.getDamage() + (double) power * 0.5 + 0.5);

    boolean flame = enchants.getEntry(Enchantments.FLAME)
        .map(e -> EnchantmentHelper.getLevel(e, bowStack) > 0).orElse(false);
    if (flame)
      arrow.setOnFireFor(100);

    float yaw = player.getYaw() + angleOffset;
    float pitch = player.getPitch();

    arrow.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;

    arrow.setVelocity(player, pitch, yaw, 0.0F, pullProgress * 3.0F, 1.0F);
    world.spawnEntity(arrow);
  }
}