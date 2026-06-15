package name.modid.core.content.effects;

import java.util.Collections;
import java.util.List;

import name.modid.datapack.drops.DropsRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class LootTouchEffect extends StatusEffect {
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
      List<Identifier> tableIds = DropsRegistry.getLootTouchTables();
      if (tableIds.isEmpty())
        return;

      Identifier randomId = tableIds.get(world.getRandom().nextInt(tableIds.size()));
      RegistryKey<LootTable> randomKey = RegistryKey.of(
          net.minecraft.registry.RegistryKeys.LOOT_TABLE, randomId);
      LootTable lootTable = world.getServer()
          .getReloadableRegistries()
          .getLootTable(randomKey);

      LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder(world)
          .add(LootContextParameters.ORIGIN, Vec3d.ofCenter(entity.getBlockPos()))
          .addOptional(LootContextParameters.THIS_ENTITY, entity);

      LootContextParameterSet lootContext = builder.build(LootContextTypes.CHEST);
      List<ItemStack> loot = lootTable.generateLoot(lootContext);
      Collections.shuffle(loot);

      int count = Math.min(2 + world.random.nextInt(3), loot.size());
      for (int i = 0; i < count; i++) {
        Block.dropStack(world, entity.getBlockPos(), loot.get(i));
      }
    }
  }
}
