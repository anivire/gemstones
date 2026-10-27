package name.modid.core.mixins.other;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.modid.core.content.items.registries.GemstonesRegistry;
import name.modid.datapack.drops.DropsConfig;
import name.modid.datapack.drops.DropsRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;

@Mixin(EnderDragonEntity.class)
public abstract class DropEnderScaleAfterDragonDeath {
  @Unique
  private static final int FIRST_EXPERIENCE_TICK = 150;

  @Unique
  private boolean enderScaleDropped;

  // Vanilla spawns the first experience orbs 150 ticks into the death animation, the scale drops
  // at the same moment. Checked at HEAD so other mods changing the method body cannot break it
  @Inject(method = "updatePostDeath", at = @At("HEAD"))
  private void dropEnderScaleWithFirstExperienceOrb(CallbackInfo ci) {
    EnderDragonEntity self = (EnderDragonEntity) (Object) this;

    if (enderScaleDropped
        || self.ticksSinceDeath < FIRST_EXPERIENCE_TICK
        || !(self.getWorld() instanceof ServerWorld world)
        || !world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
      return;
    }

    // One roll per death, not one per experience burst
    enderScaleDropped = true;

    DropsConfig.SpecialDrop config = DropsRegistry.getSpecialDrop("ender_dragon_death");
    if (config == null)
      return;

    if (!shouldDrop(config, world.random.nextFloat())) {
      return;
    }

    Item dropItem = resolveItem(config);
    if (dropItem == null)
      return;

    int count = config.getMinCount() + world.random.nextInt(config.getMaxCount() - config.getMinCount() + 1);
    Block.dropStack(world, self.getBlockPos(), new ItemStack(dropItem, count));
  }

  private static boolean shouldDrop(DropsConfig.SpecialDrop config, float roll) {
    float chance = config.getChance();
    if (chance <= 0.0f) {
      return false;
    }
    if (chance >= 1.0f) {
      return true;
    }
    return roll < chance;
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
