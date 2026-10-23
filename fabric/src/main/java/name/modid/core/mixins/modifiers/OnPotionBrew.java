package name.modid.core.mixins.modifiers;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.modid.core.content.events.CustomEvents;
import name.modid.core.utils.accessors.BrewingStandBlockEntityAccess;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(BrewingStandBlockEntity.class)
public abstract class OnPotionBrew implements BrewingStandBlockEntityAccess {
  @Unique
  private static final int POTION_SLOTS = 3;

  @Unique
  private static final String LAST_BREWER_KEY = "gemstones_last_brewer";

  @Unique
  private static final ThreadLocal<ItemStack[]> POTIONS_BEFORE_BREW = new ThreadLocal<>();

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

  @Inject(method = "readNbt", at = @At("TAIL"))
  private void readLastBrewer(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup, CallbackInfo ci) {
    this.lastBrewer = nbt.containsUuid(LAST_BREWER_KEY) ? nbt.getUuid(LAST_BREWER_KEY) : null;
  }

  @Inject(method = "writeNbt", at = @At("TAIL"))
  private void writeLastBrewer(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup, CallbackInfo ci) {
    if (this.lastBrewer != null) {
      nbt.putUuid(LAST_BREWER_KEY, this.lastBrewer);
    }
  }

  @Inject(method = "craft", at = @At("HEAD"))
  private static void rememberPotions(
      World world,
      BlockPos pos,
      DefaultedList<ItemStack> inventory,
      CallbackInfo ci) {
    ItemStack[] before = new ItemStack[POTION_SLOTS];
    for (int i = 0; i < POTION_SLOTS; i++) {
      before[i] = inventory.get(i).copy();
    }

    POTIONS_BEFORE_BREW.set(before);
  }

  @Inject(method = "craft", at = @At("TAIL"))
  private static void onBrew(
      World world,
      BlockPos pos,
      DefaultedList<ItemStack> inventory,
      CallbackInfo ci) {
    ItemStack[] before = POTIONS_BEFORE_BREW.get();
    POTIONS_BEFORE_BREW.remove();

    if (before == null
        || !(world.getBlockEntity(pos) instanceof BrewingStandBlockEntity stand)) {
      return;
    }

    UUID lastBrewer = ((BrewingStandBlockEntityAccess) (Object) stand).getLastBrewer();
    if (lastBrewer == null) {
      return;
    }

    ServerPlayerEntity player = world.getServer().getPlayerManager().getPlayer(lastBrewer);
    if (player == null) {
      return;
    }

    // Only potions that were actually brewed in this step get the bonus, otherwise finished
    // potions could be extended endlessly by brewing something else next to them
    DefaultedList<ItemStack> brewedPotions = DefaultedList.ofSize(POTION_SLOTS, ItemStack.EMPTY);
    boolean anyBrewed = false;
    for (int i = 0; i < POTION_SLOTS; i++) {
      ItemStack current = inventory.get(i);
      if (!ItemStack.areEqual(before[i], current)) {
        brewedPotions.set(i, current);
        anyBrewed = true;
      }
    }

    if (!anyBrewed) {
      return;
    }

    CustomEvents.ON_POTION_BREW.invoker().onPotionBrew(player, brewedPotions);
  }
}
