package name.modid.core.mixins.other;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.modid.core.content.items.registries.GemstonesRegistry;
import name.modid.datapack.drops.DropsConfig;
import name.modid.datapack.drops.DropsRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

@Mixin(ExperienceBottleEntity.class)
public abstract class DropCrystallizedExperienceGemstone {
  @Inject(method = "onCollision", at = @At("TAIL"))
  private void dropCrystallizedExperience(
      net.minecraft.util.hit.HitResult hitResult,
      CallbackInfo ci) {
    ExperienceBottleEntity self = (ExperienceBottleEntity) (Object) this;

    DropsConfig.SpecialDrop config = DropsRegistry.getSpecialDrop("experience_bottle");
    if (config == null)
      return;

    if (self.getWorld().isClient()
        || self.getWorld().random.nextFloat() > config.getChance()) {
      return;
    }

    ServerWorld world = (ServerWorld) self.getWorld();
    BlockPos pos = self.getBlockPos();

    Item dropItem = resolveItem(config);
    if (dropItem == null)
      return;

    int count = config.getMinCount() + world.random.nextInt(config.getMaxCount() - config.getMinCount() + 1);
    Block.dropStack(world, pos, new ItemStack(dropItem, count));
  }

  private static Item resolveItem(DropsConfig.SpecialDrop config) {
    if (config.getGemstoneType() != null) {
      List<Item> gemstones = GemstonesRegistry.getGemstonesByType(config.getGemstoneType());
      if (!gemstones.isEmpty()) {
        return gemstones.get(0);
      }
    }

    if (config.getItem() != null) {
      Item item = Registries.ITEM.get(config.getItem());
      if (item != net.minecraft.item.Items.AIR) {
        return item;
      }
    }

    return null;
  }
}
