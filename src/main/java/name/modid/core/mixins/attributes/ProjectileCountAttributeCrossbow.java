package name.modid.core.mixins.attributes;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.modid.core.api.modifiers.helpers.ModifierGatheringHelper;
import name.modid.core.api.modifiers.types.EventType;
import name.modid.core.content.registries.AttributesRegistry;
import name.modid.core.utils.HomingArrow;
import name.modid.core.utils.accessors.HomingArrowAccessor;
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
import net.minecraft.server.world.ServerWorld;
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
    if (!(shooter instanceof PlayerEntity player)) {
      return;
    }

    AttributeModifiersComponent mods = player.getMainHandStack()
        .getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
    int projectileCount = 1;

    for (var entry : mods.modifiers()) {
      if (AttributesRegistry.PROJECTILE_COUNT_ATTRIBUTE.equals(entry.attribute())) {
        projectileCount += (int) entry.modifier().value();
      }
    }

    int requestedCount = Math.max(1, projectileCount);
    if (requestedCount <= 1) {
      return;
    }

    ci.cancel();

    ChargedProjectilesComponent charged = crossbow.get(DataComponentTypes.CHARGED_PROJECTILES);
    if (charged == null || charged.isEmpty()) {
      return;
    }

    if (world.isClient()) {
      return;
    }

    List<ItemStack> projectiles = charged.getProjectiles();
    ItemStack projectileStack = projectiles.get(0).copy();

    var enchants = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT);
    boolean hasInfinity = enchants.getEntry(Enchantments.INFINITY)
        .map(e -> EnchantmentHelper.getLevel(e, crossbow) > 0).orElse(false);

    multishoot((ServerWorld) world, player, crossbow, projectileStack, requestedCount, speed, divergence);

    if (!player.isCreative() && !hasInfinity) {
      projectileStack.decrement(1);

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
        1.0F);

    player.incrementStat(Stats.USED.getOrCreateStat(crossbow.getItem()));
  }

  private static void multishoot(
      ServerWorld world,
      PlayerEntity shooter,
      ItemStack crossbow,
      ItemStack projectileStack,
      int projectileCount,
      float speed,
      float divergence) {
    final float SPREAD = 7.0F;

    float g = projectileCount == 1 ? 0.0F : 2.0F * SPREAD / (float) (projectileCount - 1);
    float h = (float) ((projectileCount - 1) % 2) * g / 2.0F;
    float dir = 1.0F;
    boolean isHoming = !ModifierGatheringHelper.getModifiersByEventType(crossbow, EventType.HOMING_ARROW).isEmpty();
    LivingEntity preferredTarget = isHoming ? HomingArrow.findPreferredTarget(world, shooter) : null;

    for (int j = 0; j < projectileCount; ++j) {
      float offset = h + dir * (float) ((j + 1) / 2) * g;
      dir = -dir;

      ProjectileEntity projectile;

      if (projectileStack.isOf(Items.FIREWORK_ROCKET)) {
        projectile = new FireworkRocketEntity(world, projectileStack, shooter,
            shooter.getX(), shooter.getEyeY() - 0.15, shooter.getZ(), true);
      } else {
        ArrowItem arrowItem = (ArrowItem) (projectileStack.getItem() instanceof ArrowItem
            ? projectileStack.getItem()
            : Items.ARROW);
        PersistentProjectileEntity arrow = arrowItem.createArrow(world, projectileStack.copy(), shooter, crossbow);

        if (isHoming) {
          ((HomingArrowAccessor) arrow).setHoming(true);
          HomingArrow.setPreferredTarget(arrow, preferredTarget);
        }

        arrow.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
        projectile = arrow;
      }

      float yaw = shooter.getYaw() + offset;
      float pitch = shooter.getPitch();

      projectile.setVelocity(shooter, pitch, yaw, 0.0F, speed, divergence);
      world.spawnEntity(projectile);
    }
  }
}
