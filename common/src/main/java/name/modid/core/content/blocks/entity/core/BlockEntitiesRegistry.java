package name.modid.core.content.blocks.entity.core;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import name.modid.Gemstones;
import name.modid.core.content.blocks.BlocksRegistry;
import name.modid.core.content.blocks.entity.JewelryTableBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.RegistryKeys;

public class BlockEntitiesRegistry {
  public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Gemstones.MOD_ID,
      RegistryKeys.BLOCK_ENTITY_TYPE);

  public static final RegistrySupplier<BlockEntityType<JewelryTableBlockEntity>> JEWELRY_TABLE_BLOCK_ENTITY = BLOCK_ENTITIES
      .register("jewelry_table_block_entity", () -> BlockEntityType.Builder.create(
          JewelryTableBlockEntity::new,
          BlocksRegistry.JEWELRY_TABLE.get()).build(null));

  public static void initialize() {
    Gemstones.LOGGER.info("Registering block entities for {}", Gemstones.MOD_ID);
    BLOCK_ENTITIES.register();
  }
}