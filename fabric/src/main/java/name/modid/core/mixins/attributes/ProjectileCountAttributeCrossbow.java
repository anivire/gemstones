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
import name.modid.core.utils.HomingArrow;
import name.modid.core.utils.accessors.HomingArrowAccessor;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.Unit;
import net.minecraft.world.World;

@Mixin(CrossbowItem.class)
public class ProjectileCountAttributeCrossbow {
  @Inject(method = "shootAll", at = @At("HEAD"), cancellable = true)
  private void onCrossbowShoot(
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

    AttributeModifiersComponent mods = crossbow
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

    if (!(world instanceof ServerWorld serverWorld)) {
      return;
    }

    ChargedProjectilesComponent charged = crossbow.set(DataComponentTypes.CHARGED_PROJECTILES,
        ChargedProjectilesComponent.DEFAULT);
    if (charged == null || charged.isEmpty()) {
      return;
    }

    // Ammo was already consumed while charging, extra projectiles are intangible copies
    List<ItemStack> projectiles = new ArrayList<>(charged.getProjectiles());
    addIntangibleCopies(projectiles, requestedCount + projectiles.size() - 1);

    multishoot(serverWorld, player, hand, crossbow, projectiles, speed, divergence);

    world.playSound(
        null,
        player.getX(), player.getY(), player.getZ(),
        SoundEvents.ITEM_CROSSBOW_SHOOT,
        SoundCategory.PLAYERS,
        1.0F,
        1.0F);

    if (player instanceof ServerPlayerEntity serverPlayer) {
      Criteria.SHOT_CROSSBOW.trigger(serverPlayer, crossbow);
      serverPlayer.incrementStat(Stats.USED.getOrCreateStat(crossbow.getItem()));
    }
  }

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
      ItemStack crossbow,
      List<ItemStack> projectiles,
      float speed,
      float divergence) {
    final float SPREAD = 7.0F;
    int projectileCount = projectiles.size();
    float g = projectileCount == 1 ? 0.0F : 2.0F * SPREAD / (float) (projectileCount - 1);
    float h = (float) ((projectileCount - 1) % 2) * g / 2.0F;
    float dir = 1.0F;

    boolean isHoming = !ModifierGatheringHelper.getModifiersByEventType(crossbow, EventType.HOMING_ARROW).isEmpty();
    LivingEntity preferredTarget = isHoming ? HomingArrow.findPreferredTarget(world, shooter) : null;

    RangedWeaponItemInvoker weapon = (RangedWeaponItemInvoker) crossbow.getItem();
    EquipmentSlot slot = LivingEntity.getSlotForHand(hand);

    for (int j = 0; j < projectileCount; ++j) {
      float offset = h + dir * (float) ((j + 1) / 2) * g;
      dir = -dir;

      // Vanilla creates fireworks and arrows here, player crossbow arrows are always critical
      ItemStack projectileStack = projectiles.get(j);
      ProjectileEntity projectile = weapon.gemstones$createArrowEntity(world, shooter, crossbow, projectileStack,
          true);

      if (isHoming && projectile instanceof PersistentProjectileEntity arrow) {
        ((HomingArrowAccessor) arrow).setHoming(true);
        HomingArrow.setPreferredTarget(arrow, preferredTarget);
      }

      projectile.setVelocity(shooter, shooter.getPitch(), shooter.getYaw() + offset, 0.0F, speed, divergence);
      world.spawnEntity(projectile);

      // Same durability cost as vanilla multishot: one per arrow, three per firework
      crossbow.damage(weapon.gemstones$getWeaponStackDamage(projectileStack), shooter, slot);
      if (crossbow.isEmpty()) {
        break;
      }
    }
  }
}
