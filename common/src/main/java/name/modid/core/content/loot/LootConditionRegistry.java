package name.modid.core.content.loot;

import name.modid.Gemstones;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.registry.RegistryKeys;

public class LootConditionRegistry {
  private static final DeferredRegister<LootConditionType> LOOT_CONDITIONS = DeferredRegister.create(
      Gemstones.MOD_ID,
      RegistryKeys.LOOT_CONDITION_TYPE);

  private static final RegistrySupplier<LootConditionType> GEODE_CHANCE_SUPPLIER = LOOT_CONDITIONS.register(
      "geode_chance",
      () -> new LootConditionType(GeodeDropChanceCondition.CODEC));
  private static final RegistrySupplier<LootConditionType> BLOCK_IN_TAG_SUPPLIER = LOOT_CONDITIONS.register(
      "block_in_tag",
      () -> new LootConditionType(BlockInTagLootCondition.CODEC));

  public static LootConditionType geodeChance() {
    return GEODE_CHANCE_SUPPLIER.get();
  }

  public static LootConditionType blockInTag() {
    return BLOCK_IN_TAG_SUPPLIER.get();
  }

  public static void initialize() {
    LOOT_CONDITIONS.register();
  }
}
