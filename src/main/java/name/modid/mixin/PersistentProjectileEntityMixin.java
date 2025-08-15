package name.modid.mixin;

import java.util.ArrayList;
import java.util.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import name.modid.helpers.GemstoneSocketingHelper;
import name.modid.helpers.modifiers.ModifierHelper;
import name.modid.helpers.modifiers.modifierTypes.EventType;
import name.modid.helpers.modifiers.modifierTypes.ModifierOnHit;
import name.modid.helpers.modifiers.modifierTypes.ModifierOnHitEffectProjectile;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Mixin(PersistentProjectileEntity.class)
public class PersistentProjectileEntityMixin {
  private static final Random RANDOM = new Random();

  @Inject(method = "onEntityHit", at = @At("HEAD"))
  protected void onEntityHit(EntityHitResult entityHitResult, CallbackInfo ci) {
    handleHit(entityHitResult);
  }

  @Inject(method = "onBlockHit", at = @At("HEAD"))
  protected void onBlockHit(BlockHitResult blockHitResult, CallbackInfo ci) {
    handleHit(blockHitResult);
  }

  private void handleHit(HitResult hitResult) {
    PersistentProjectileEntity entity = (PersistentProjectileEntity) (Object) this;
    ArrowEntity arrow = (ArrowEntity) entity;
    ItemStack itemStack = getWeaponStack((PlayerEntity) arrow.getOwner());
    LivingEntity target = hitResult instanceof EntityHitResult entityHit
        && entityHit.getEntity() instanceof LivingEntity living ? living : null;
    World world = arrow.getWorld();

    if (world.isClient || itemStack == null) {
      return;
    }

    if (arrow != null && target != null) {
      ArrayList<ModifierOnHitEffectProjectile> onHitEffectProjectileModifiers =
          ModifierHelper.getOnHitEffectProjectileModifiers(itemStack);

      if (!onHitEffectProjectileModifiers.isEmpty()) {
        GemstoneSocketingHelper.applyOnHitEffectProjectileModifiers(onHitEffectProjectileModifiers,
            itemStack.getItem(), itemStack, target, world);
      }
    }

    if (!arrow.getWorld().isRaining()) {
      return;
    }

    Vec3d pos = hitResult.getPos();
    applyGemstoneModifiers(itemStack, world, pos, arrow, target);
  }

  private ItemStack getWeaponStack(PlayerEntity player) {
    if (player == null) {
      return null;
    }

    ItemStack mainHand = player.getMainHandStack();
    ItemStack offHand = player.getOffHandStack();

    if (isBowOrCrossbow(mainHand)) {
      return mainHand;
    } else if (isBowOrCrossbow(offHand)) {
      return offHand;
    }

    return null;
  }

  private boolean isBowOrCrossbow(ItemStack stack) {
    return stack.getItem() instanceof BowItem || stack.getItem() instanceof CrossbowItem;
  }

  private void applyGemstoneModifiers(ItemStack itemStack, World world, Vec3d pos,
      ArrowEntity arrow, LivingEntity target) {
    // ModifierOnHit
    // TODO: move to ItemGemstoneHelper or make correct realization (wont support for other
    // modifiers)
    ArrayList<ModifierOnHit> onHitModifiers = ModifierHelper.getOnHitModifiers(itemStack);
    if (!onHitModifiers.isEmpty()) {
      double applyTotalChance = 0.0;
      for (ModifierOnHit modifier : onHitModifiers) {
        if (modifier.eventType == EventType.LIGHTNING_BOLT) {
          applyTotalChance += modifier.eventChance.get(modifier.getRarityType().getValue());
        }
      }

      // EventType.LIGHTNING_BOLT
      if (applyTotalChance > 0 && RANDOM.nextDouble() < Math.min(applyTotalChance, 1.0)) {
        LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(world);
        if (lightning != null) {
          lightning.setPosition(pos.getX(), pos.getY(), pos.getZ());
          world.spawnEntity(lightning);
        }
        arrow.discard();
      }
    }
  }
}
