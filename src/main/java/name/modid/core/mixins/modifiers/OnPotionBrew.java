package name.modid.core.mixins.modifiers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig.OnPotionBrewConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierContext.ContextBuilder;
import name.modid.core.api.modifiers.config.ModifierManager;
import name.modid.core.api.modifiers.config.ModifierUtils;
import name.modid.core.api.modifiers.helpers.ModifierGatheringHelper;
import name.modid.core.utils.accessors.BrewingStandBlockEntityAccess;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(BrewingStandBlockEntity.class)
public abstract class OnPotionBrew implements BrewingStandBlockEntityAccess {
  @Unique
  private UUID lastBrewer;

  @Override
  public void setLastBrewer(UUID uuid) {
    this.lastBrewer = uuid;
  }

  @Override
  public UUID getLastBrewer() {
    return this.lastBrewer;
  }

  @Inject(method = "craft", at = @At("TAIL"))
  private static void onBrew(
      World world,
      BlockPos pos,
      DefaultedList<ItemStack> inventory,
      CallbackInfo ci) {
    if (world instanceof ServerWorld serverWorld) {
      if (!(world.getBlockEntity(pos) instanceof BrewingStandBlockEntity stand))
        return;

      UUID lastBrewer = ((OnPotionBrew) (Object) stand).getLastBrewer();
      if (lastBrewer == null)
        return;

      ServerPlayerEntity player = world.getServer().getPlayerManager().getPlayer(lastBrewer);
      if (player == null)
        return;

      List<GemstoneModifier> modifiers = ModifierUtils.collectValuesFromArmor(
          player,
          armorPiece -> ModifierGatheringHelper
              .getModifiers(armorPiece, OnPotionBrewConfig.class)
              .stream()
              .toList());

      ContextBuilder ctxBuilder = new ContextBuilder(serverWorld).withInventory(inventory);
      ModifierContext ctx = ctxBuilder.build();
      ModifierManager.applyModifiers(new ArrayList<>(modifiers), ctx);
    }
  }
}
