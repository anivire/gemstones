package name.modid.core.content.loot;

import name.modid.Gemstones;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class LootConditionRegistry {
  public static final LootConditionType GEODE_CHANCE = Registry.register(
      Registries.LOOT_CONDITION_TYPE,
      Identifier.of(Gemstones.MOD_ID, "geode_chance"),
      new LootConditionType(GeodeDropChanceCondition.CODEC));

  public static final LootConditionType BLOCK_IN_TAG = Registry.register(
      Registries.LOOT_CONDITION_TYPE,
      Identifier.of(Gemstones.MOD_ID, "block_in_tag"),
      new LootConditionType(BlockInTagLootCondition.CODEC));

  public static void initialize() {
  }
}
