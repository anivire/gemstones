package name.modid.mixin;

import java.util.ArrayList;
import java.util.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import name.modid.helpers.ItemGemstoneHelper;
import name.modid.helpers.modifiers.GemstoneModifierHelper;
import name.modid.helpers.modifiers.modifierTypes.EventType;
import name.modid.helpers.modifiers.modifierTypes.ModifierOnHit;
import name.modid.helpers.modifiers.modifierTypes.ModifierOnHitEffect;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

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
    if (!(entity instanceof ArrowEntity arrow) || arrow.getWorld().isClient
        || !arrow.getWorld().isRaining()) {
      return;
    }

    if (!(arrow.getOwner() instanceof PlayerEntity player)) {
      return;
    }

    LivingEntity target = hitResult instanceof EntityHitResult entityHit
        && entityHit.getEntity() instanceof LivingEntity living ? living : null;
    ItemStack weapon = getWeaponStack(player);
    if (weapon == null) {
      return;
    }

    ServerWorld world = (ServerWorld) arrow.getWorld();
    Vec3d pos = hitResult.getPos();
    applyGemstoneModifiers(weapon, world, pos, arrow, target);
  }

  private ItemStack getWeaponStack(PlayerEntity player) {
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

  private void applyGemstoneModifiers(ItemStack itemStack, ServerWorld world, Vec3d pos,
      ArrowEntity arrow, LivingEntity target) {
    // ModifierOnHit
    // TODO: move to ItemGemstoneHelper or make correct realization (wont support for other
    // modifiers)
    ArrayList<ModifierOnHit> onHitModifiers = GemstoneModifierHelper.getOnHitModifiers(itemStack);
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

    // ModifierOnHitEffect
    if (target != null) {
      ArrayList<ModifierOnHitEffect> effectModifiers =
          GemstoneModifierHelper.getOnHitEffectModifiers(itemStack);
      if (!effectModifiers.isEmpty()) {
        ItemGemstoneHelper.applyOnHitEffectModifiers(effectModifiers, itemStack.getItem(),
            itemStack, target, world);
      }
    }
  }
}
