package name.modid.mixin;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import name.modid.helpers.modifiers.GemstoneModifierHelper;
import name.modid.helpers.modifiers.types.ModifierOnHit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PersistentProjectileEntity.class)
public class PersistentProjectileEntityMixin {
  private static final Random RANDOM = new Random();

  @Inject(method = "onEntityHit", at = @At("HEAD"))
  protected void onEntityHit(EntityHitResult entityHitResult, CallbackInfo ci) {
    PersistentProjectileEntity entity = (PersistentProjectileEntity) (Object) this;
    if (!(entity instanceof ArrowEntity arrow) || arrow.getWorld().isClient || !arrow.getWorld().isRaining()) {
      return;
    }

    if (arrow.getOwner() instanceof PlayerEntity player) {
      ItemStack bow = player.getMainHandStack();
      ServerWorld world = (ServerWorld) arrow.getWorld();
      Vec3d pos = entityHitResult.getPos();

      applyGemstoneModifiers(bow, world, pos, arrow);
    }
  }

  @Inject(method = "onBlockHit", at = @At("HEAD"))
  protected void onBlockHit(BlockHitResult blockHitResult, CallbackInfo ci) {
    PersistentProjectileEntity entity = (PersistentProjectileEntity) (Object) this;
    if (!(entity instanceof ArrowEntity arrow) || arrow.getWorld().isClient || !arrow.getWorld().isRaining()) {
      return;
    }

    if (arrow.getOwner() instanceof PlayerEntity player) {
      ItemStack bow = player.getMainHandStack();
      ServerWorld world = (ServerWorld) arrow.getWorld();
      Vec3d pos = blockHitResult.getPos();

      applyGemstoneModifiers(bow, world, pos, arrow);
    }
  }

  private void spawnLightning(ServerWorld world, Vec3d pos) {
    LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
    lightning.setPosition(pos.getX(), pos.getY(), pos.getZ());
    world.spawnEntity(lightning);
  }

  private void applyGemstoneModifiers(ItemStack itemStack, ServerWorld world, Vec3d pos, ArrowEntity arrow) {
    if (!(itemStack.getItem() instanceof BowItem) && !(itemStack.getItem() instanceof CrossbowItem)) {
      return;
    }

    ArrayList<ModifierOnHit> modifiers = GemstoneModifierHelper.getOnHitModifiers(itemStack);
    double totalChance = 0.0;

    for (ModifierOnHit modifier : modifiers) {
      if (modifier.eventType == ModifierOnHit.EventType.LIGHTNING_BOLT) {
        totalChance += modifier.eventChance.get(modifier.getRarityType().getValue());
      }
    }

    if (totalChance > 0 && RANDOM.nextDouble() < Math.min(totalChance, 1.0)) {
      spawnLightning(world, pos);
      arrow.discard();
    }
  }
}