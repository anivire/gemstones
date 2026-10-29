package name.modid.core.mixins.other;

import java.util.List;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.content.items.GemstoneDustItem;
import name.modid.core.content.items.GemstoneItem;
import name.modid.core.utils.accessors.GrindstoneGemAccessor;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

// The grindstone result slot is an anonymous class
@Mixin(targets = "net.minecraft.screen.GrindstoneScreenHandler$4")
public abstract class GrindstoneGemstoneResult {
  @Inject(method = "onTakeItem", at = @At("HEAD"))
  private void gemstones$onGemstoneGround(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
    if (!(player.getWorld() instanceof ServerWorld world)
        || !(player.currentScreenHandler instanceof GrindstoneGemAccessor accessor)) {
      return;
    }

    List<ItemStack> gems = accessor.gemstones$getGrindingGems();
    if (gems.isEmpty()) {
      return;
    }

    world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK,
        SoundCategory.BLOCKS, 0.9f, 0.8f + world.random.nextFloat() * 0.2f);

    for (ItemStack ground : gems) {
      if (!(ground.getItem() instanceof GemstoneItem gem)) {
        continue;
      }

      GemstoneQuality quality = gem.getRarityType();
      if (world.random.nextFloat() < quality.getGrindExperienceChance()) {
        ExperienceOrbEntity.spawn(world, player.getPos(),
            1 + world.random.nextInt(quality.getGrindMaxExperience()));
      }
    }
  }

  // Vanilla clears both input slots, a gemstone stack loses only the ground gemstone
  @WrapOperation(method = "onTakeItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Inventory;setStack(ILnet/minecraft/item/ItemStack;)V"))
  private void gemstones$takeSingleGemstone(Inventory input, int slot, ItemStack stack,
      Operation<Void> original) {
    ItemStack current = input.getStack(slot);
    if (GemstoneDustItem.isGrindable(current) && current.getCount() > 1) {
      original.call(input, slot, current.copyWithCount(current.getCount() - 1));
      return;
    }

    original.call(input, slot, stack);
  }
}
