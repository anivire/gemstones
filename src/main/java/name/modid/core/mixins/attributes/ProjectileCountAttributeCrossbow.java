package name.modid.core.mixins.attributes;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.modid.core.content.registries.AttributesRegistry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

@Mixin(CrossbowItem.class)
public class ProjectileCountAttributeCrossbow {

  @Inject(method = "shootAll", at = @At("HEAD"), cancellable = true)
  private static void onCrossbowShoot(
      World world,
      LivingEntity shooter,
      Hand hand,
      ItemStack crossbow,
      float speed,
      float divergence,
      LivingEntity target,
      CallbackInfo ci) {

    if (!(shooter instanceof PlayerEntity player))
      return;

    var mods = player.getMainHandStack()
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

    ChargedProjectilesComponent charged = crossbow.get(DataComponentTypes.CHARGED_PROJECTILES);
    if (charged == null || charged.isEmpty())
      return;
    List<ItemStack> projectiles = charged.getProjectiles();

    ItemStack projectileStack = projectiles.get(0).copy();

    var enchants = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT);
    boolean hasInfinity = enchants.getEntry(Enchantments.INFINITY)
        .map(e -> EnchantmentHelper.getLevel(e, crossbow) > 0).orElse(false);

    fireProjectile(world, player, crossbow, projectileStack, speed, divergence, 0f);

    for (int i = 1; i < requestedCount; i++) {
      float side = (i % 2 == 0) ? -1f : 1f;
      float step = (float) Math.ceil(i / 2f);
      float angle = side * step * 7.0f;
      fireProjectile(world, player, crossbow, projectileStack, speed, divergence, angle);
    }

    boolean isArrow = projectileStack.isOf(Items.ARROW);
    boolean isFirework = projectileStack.isOf(Items.FIREWORK_ROCKET);

    if (!player.isCreative()) {
      if (isArrow && !hasInfinity) {
        projectileStack.decrement(1);
      }

      else if (isFirework) {
        projectileStack.decrement(1);
      }

      if (projectileStack.isEmpty()) {
        player.getInventory().removeOne(projectileStack);
      }
    }

    crossbow.set(DataComponentTypes.CHARGED_PROJECTILES, null);
    crossbow.remove(DataComponentTypes.CHARGED_PROJECTILES);

    world.playSound(
        null,
        player.getX(), player.getY(), player.getZ(),
        SoundEvents.ITEM_CROSSBOW_SHOOT,
        SoundCategory.PLAYERS,
        1.0F,
        1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F));

    player.incrementStat(Stats.USED.getOrCreateStat(crossbow.getItem()));
  }

  private static void fireProjectile(
      World world, PlayerEntity player, ItemStack crossbow,
      ItemStack projectileStack, float speed,
      float divergence, float offsetAngle) {

    ProjectileEntity projectile;

    if (projectileStack.isOf(Items.FIREWORK_ROCKET)) {
      projectile = new FireworkRocketEntity(world, projectileStack, player,
          player.getX(), player.getEyeY() - 0.15, player.getZ(), true);
    } else {
      ArrowItem arrowItem = (ArrowItem) (projectileStack.getItem() instanceof ArrowItem
          ? projectileStack.getItem()
          : Items.ARROW);
      PersistentProjectileEntity arrow = arrowItem.createArrow(world, projectileStack.copy(), player, crossbow);

      var ench = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT);
      int power = ench.getEntry(Enchantments.POWER)
          .map(e -> EnchantmentHelper.getLevel(e, crossbow)).orElse(0);
      if (power > 0)
        arrow.setDamage(arrow.getDamage() + power * 0.5 + 0.5);

      boolean flame = ench.getEntry(Enchantments.FLAME)
          .map(e -> EnchantmentHelper.getLevel(e, crossbow) > 0).orElse(false);
      if (flame)
        arrow.setOnFireFor(100);

      arrow.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
      projectile = arrow;
    }

    float yaw = player.getYaw() + offsetAngle;
    float pitch = player.getPitch();

    projectile.setVelocity(player, pitch, yaw, 0.0F, speed, divergence);
    world.spawnEntity(projectile);
  }
}