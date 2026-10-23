package name.modid.core.mixins.attributes;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.modid.core.api.modifiers.config.utils.ModifierUtils;
import name.modid.core.api.modifiers.helpers.ModifierGatheringHelper;
import name.modid.core.api.modifiers.types.EventType;
import name.modid.core.content.registries.AttributesRegistry;
import name.modid.core.utils.BowPullHelper;
import name.modid.core.utils.HomingArrow;
import name.modid.core.utils.accessors.HomingArrowAccessor;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.Unit;
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

    AttributeModifiersComponent mods = bowStack
        .getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
    int projectileCount = (int) Math.floor(ModifierUtils.getAttributeValue(
        mods,
        AttributesRegistry.PROJECTILE_COUNT_ATTRIBUTE,
        1.0));
    int requestedCount = Math.max(1, projectileCount);

    if (requestedCount <= 1) {
      return;
    }

    ci.cancel();

    float pullProgress = BowPullHelper.getPullProgress(bowStack, bowStack.getMaxUseTime(user) - remainingUseTicks);
    if (pullProgress < 0.1F) {
      return;
    }

    ItemStack arrowStack = player.getProjectileType(bowStack);
    if (arrowStack.isEmpty() || !(world instanceof ServerWorld serverWorld)) {
      return;
    }

    // Vanilla ammo rules: Infinity works for plain arrows only, creative consumes nothing
    List<ItemStack> projectiles = new ArrayList<>(RangedWeaponItemInvoker.gemstones$load(bowStack, arrowStack, player));
    if (projectiles.isEmpty()) {
      return;
    }

    addIntangibleCopies(projectiles, requestedCount + projectiles.size() - 1);
    multishoot(serverWorld, player, player.getActiveHand(), bowStack, projectiles, pullProgress);

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

  // Extra arrows are copies that cannot be picked up, like the vanilla multishot arrows
  private static void addIntangibleCopies(List<ItemStack> projectiles, int totalCount) {
    ItemStack template = projectiles.get(0).copyWithCount(1);
    template.set(DataComponentTypes.INTANGIBLE_PROJECTILE, Unit.INSTANCE);

    while (projectiles.size() < totalCount) {
      projectiles.add(template.copy());
    }
  }

  private void multishoot(
      ServerWorld world,
      PlayerEntity shooter,
      Hand hand,
      ItemStack bowStack,
      List<ItemStack> projectiles,
      float pullStrength) {
    final float SPREAD = 10.0F;
    int projectileCount = projectiles.size();
    float g = projectileCount == 1 ? 0.0F : 2.0F * SPREAD / (float) (projectileCount - 1);
    float h = (float) ((projectileCount - 1) % 2) * g / 2.0F;
    float dir = 1.0F;

    boolean isHoming = !ModifierGatheringHelper.getModifiersByEventType(bowStack, EventType.HOMING_ARROW).isEmpty();
    LivingEntity preferredTarget = isHoming ? HomingArrow.findPreferredTarget(world, shooter) : null;

    RangedWeaponItemInvoker weapon = (RangedWeaponItemInvoker) bowStack.getItem();
    EquipmentSlot slot = LivingEntity.getSlotForHand(hand);

    for (int j = 0; j < projectileCount; ++j) {
      float offset = h + dir * (float) ((j + 1) / 2) * g;
      dir = -dir;

      ItemStack projectileStack = projectiles.get(j);
      ProjectileEntity projectile = weapon.gemstones$createArrowEntity(world, shooter, bowStack, projectileStack,
          pullStrength == 1.0F);

      if (isHoming && projectile instanceof PersistentProjectileEntity arrow) {
        ((HomingArrowAccessor) arrow).setHoming(true);
        HomingArrow.setPreferredTarget(arrow, preferredTarget);
      }

      projectile.setVelocity(shooter, shooter.getPitch(), shooter.getYaw() + offset, 0.0F,
          pullStrength * 3.0F, 1.0F);
      world.spawnEntity(projectile);

      // Same durability cost as vanilla multishot: one per projectile
      bowStack.damage(weapon.gemstones$getWeaponStackDamage(projectileStack), shooter, slot);
      if (bowStack.isEmpty()) {
        break;
      }
    }
  }
}
