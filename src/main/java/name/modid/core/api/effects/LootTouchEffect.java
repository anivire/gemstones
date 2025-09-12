package name.modid.core.api.effects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class LootTouchEffect extends StatusEffect {
  ArrayList<RegistryKey<LootTable>> lootTables = new ArrayList<>(
      Arrays.asList(
          LootTables.SIMPLE_DUNGEON_CHEST,
          LootTables.SHIPWRECK_SUPPLY_CHEST,
          LootTables.ABANDONED_MINESHAFT_CHEST));

  public LootTouchEffect() {
    super(StatusEffectCategory.BENEFICIAL, 0x00ff00);
  }

  @Override
  public boolean canApplyUpdateEffect(int duration, int amplifier) {
    return true;
  }

  @Override
  public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
    return true;
  }

  @Override
  public void onEntityRemoval(LivingEntity entity, int amplifier, Entity.RemovalReason reason) {
    if (reason == Entity.RemovalReason.KILLED && !entity.getWorld().isClient()) {
      ServerWorld world = (ServerWorld) entity.getWorld();
      RegistryKey<LootTable> randomKey = lootTables.get(
          world.getRandom().nextInt(lootTables.size()));

      LootTable lootTable = world.getServer()
          .getReloadableRegistries()
          .getLootTable(randomKey);

      LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder(world)
          .add(LootContextParameters.ORIGIN, Vec3d.ofCenter(entity.getBlockPos()))
          .addOptional(LootContextParameters.THIS_ENTITY, entity);

      LootContextParameterSet lootContext = builder.build(LootContextTypes.CHEST);
      List<ItemStack> loot = lootTable.generateLoot(lootContext);

      for (ItemStack stack : loot) {
        Block.dropStack(world, entity.getBlockPos(), stack);
      }
    }
  }
}
