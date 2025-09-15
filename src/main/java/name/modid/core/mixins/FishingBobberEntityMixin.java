package name.modid.core.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import name.modid.core.api.modifiers.helpers.ModifierGatheringHelper;
import name.modid.core.api.modifiers.impl.EventType;
import name.modid.core.content.items.registries.ItemsRegistry;
import name.modid.core.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(FishingBobberEntity.class)
public abstract class FishingBobberEntityMixin {
  @Shadow
  private int hookCountdown;
  @Shadow
  private Entity hookedEntity;

  @Inject(method = "use", at = @At("HEAD"), cancellable = true)
  private void onFishing(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
    FishingBobberEntity self = (FishingBobberEntity) (Object) this;
    if (!(self.getOwner() instanceof ServerPlayerEntity player)) {
      return;
    }

    if (hookedEntity == null && hookCountdown <= 0) {
      return;
    }

    double totalIncreasedChanceValue = Utils.collectPlayerArmorValues(
        player,
        armorPiece -> ModifierGatheringHelper.getCustomConditionModifiers(armorPiece).stream()
            .filter(m -> m.getEventType() == EventType.INCREASE_MOSSY_BOX_DROP)
            .map(m -> m.getValues().get(m.getRarityType()))
            .toList())
        .stream()
        .mapToDouble(Double::doubleValue)
        .sum();

    if (player.getWorld().random.nextFloat() < totalIncreasedChanceValue) {
      ItemStack specialDrop = new ItemStack(ItemsRegistry.MOSSY_BOX);
      ItemEntity itemEntity = new ItemEntity(
          player.getWorld(),
          self.getX(), self.getY(), self.getZ(),
          specialDrop);

      double dx = player.getX() - self.getX();
      double dy = player.getY() - self.getY();
      double dz = player.getZ() - self.getZ();

      itemEntity.setVelocity(
          dx * 0.1,
          dy * 0.1 + Math.sqrt(Math.sqrt(dx * dx + dy * dy + dz * dz)) * 0.08,
          dz * 0.1);
      self.getWorld().spawnEntity(itemEntity);
      player.getWorld().spawnEntity(new ExperienceOrbEntity(
          player.getWorld(),
          player.getX(), player.getY() + 0.5, player.getZ() + 0.5,
          self.getRandom().nextInt(6) + 1));
    }
  }
}