package name.modid.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.modid.helpers.modifiers.ModifierHelper;
import name.modid.helpers.modifiers.category.ModifierCustomCondition;
import name.modid.helpers.modifiers.type.ConditionType;
import name.modid.utils.BrewingStandBlockEntityAccess;
import name.modid.utils.PotionUtil;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(BrewingStandBlockEntity.class)
public abstract class BrewingStandBlockEntityMixin implements BrewingStandBlockEntityAccess {
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
    if (!(world.getBlockEntity(pos) instanceof BrewingStandBlockEntity stand))
      return;

    UUID lastBrewer = ((BrewingStandBlockEntityMixin) (Object) stand).getLastBrewer();
    if (lastBrewer == null)
      return;

    ServerPlayerEntity player = world.getServer().getPlayerManager().getPlayer(lastBrewer);
    if (player == null)
      return;

    double totalIncreasedDurationValue = 0.0;

    List<ItemStack> equippedArmorPieces = Stream.of(
        player.getEquippedStack(EquipmentSlot.HEAD),
        player.getEquippedStack(EquipmentSlot.CHEST),
        player.getEquippedStack(EquipmentSlot.LEGS),
        player.getEquippedStack(EquipmentSlot.FEET))
        .filter(stack -> !stack.isEmpty())
        .toList();

    for (ItemStack armorPiece : equippedArmorPieces) {
      ArrayList<ModifierCustomCondition> modifiers = ModifierHelper.getCustomConditionModifiers(armorPiece);
      if (modifiers.stream().anyMatch(o -> o.getConditionType() == ConditionType.POTION_DURATION)) {
        for (ModifierCustomCondition m : modifiers) {
          totalIncreasedDurationValue += m.getValues().get(m.getRarityType());
        }
      }
    }

    for (int i = 0; i < 3; i++) {
      ItemStack stack = inventory.get(i);
      if (stack.getItem() instanceof PotionItem) {
        List<StatusEffectInstance> effects = PotionUtil.getPotionEffects(stack);
        List<StatusEffectInstance> newEffects = new ArrayList<>();

        for (StatusEffectInstance effect : effects) {
          newEffects.add(new StatusEffectInstance(
              effect.getEffectType(),
              effect.getDuration() + ((int) totalIncreasedDurationValue * 20),
              effect.getAmplifier(),
              effect.isAmbient(),
              effect.shouldShowParticles(),
              effect.shouldShowIcon()));
        }

        PotionUtil.setCustomPotionEffects(stack, newEffects);
      }
    }
  }
}
