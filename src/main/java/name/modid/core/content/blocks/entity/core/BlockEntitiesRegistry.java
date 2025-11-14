package name.modid.core.content.blocks.entity.core;

import name.modid.Gemstones;
import name.modid.core.content.blocks.BlocksRegistry;
import name.modid.core.content.blocks.entity.JewelryTableBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class BlockEntitiesRegistry {
  public static final BlockEntityType<JewelryTableBlockEntity> JEWELRY_TABLE_BLOCK_ENTITY = Registry.register(
      Registries.BLOCK_ENTITY_TYPE,
      Identifier.of(Gemstones.MOD_ID, "jewelrt_table_block_entity"),
      BlockEntityType.Builder.create(JewelryTableBlockEntity::new, BlocksRegistry.JEWELRY_TABLE).build(null));

  public static void initialize() {
    Gemstones.LOGGER.info("Registering block entities for ", Gemstones.MOD_ID);
  }
}
