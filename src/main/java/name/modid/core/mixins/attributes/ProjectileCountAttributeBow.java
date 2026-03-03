package name.modid.core.mixins.attributes;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.modid.core.api.modifiers.helpers.ModifierGatheringHelper;
import name.modid.core.api.modifiers.types.EventType;
import name.modid.core.content.registries.AttributesRegistry;
import name.modid.core.utils.accessors.HomingArrowAccessor;
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
import net.minecraft.server.world.ServerWorld;
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
    if (!(user instanceof PlayerEntity player)) {
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

    float pullProgress = BowItem.getPullProgress(bowStack.getMaxUseTime(user) - remainingUseTicks);
    if (pullProgress < 0.1F) {
      return;
    }

    ItemStack arrowStack = player.getProjectileType(bowStack);
    if (arrowStack.isEmpty()) {
      return;
    }

    if (world.isClient()) {
      return;
    }

    multishoot((ServerWorld) world, player, bowStack, arrowStack, projectileCount, pullProgress);

    var enchants = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT);
    boolean hasInfinity = enchants.getEntry(Enchantments.INFINITY)
        .map(e -> EnchantmentHelper.getLevel(e, bowStack) > 0)
        .orElse(false);

    if (!player.isCreative() && !hasInfinity) {
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

  private void multishoot(
      ServerWorld world,
      PlayerEntity shooter,
      ItemStack bowStack,
      ItemStack projectileStack,
      int projectileCount,
      float pullStrength) {
    final float SPREAD = 10.0F;

    float g = projectileCount == 1 ? 0.0F : 2.0F * SPREAD / (float) (projectileCount - 1);
    float h = (float) ((projectileCount - 1) % 2) * g / 2.0F;
    float dir = 1.0F;

    for (int j = 0; j < projectileCount; ++j) {
      float offset = h + dir * (float) ((j + 1) / 2) * g;
      dir = -dir;

      PersistentProjectileEntity entity = ((ArrowItem) (projectileStack.getItem() instanceof ArrowItem
          ? projectileStack.getItem()
          : Items.ARROW)).createArrow(world, projectileStack.copy(), shooter, bowStack);

      if (!ModifierGatheringHelper.getModifiersByEventType(bowStack, EventType.HOMING_ARROW).isEmpty()) {
        ((HomingArrowAccessor) entity).setHoming(true);
      }

      entity.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
      entity.setVelocity(shooter, shooter.getPitch(), shooter.getYaw() + offset, 0.0F,
          pullStrength * 3.0F, 1.0F);

      if (pullStrength == 1.0F) {
        entity.setCritical(true);
      }

      world.spawnEntity(entity);
    }
  }
}