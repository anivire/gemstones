package name.modid.core.content.blocks;

import name.modid.Gemstones;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class BlocksRegistry {
  public static final Block JEWELRY_TABLE = registerBlock("jewelry_table",
      new JewelryTable(AbstractBlock.Settings.create()
          .nonOpaque()
          .strength(2.5F)
          .sounds(BlockSoundGroup.WOOD)
          .burnable()));

  private static Block registerBlock(String name, Block block) {
    registerBlockItem(name, block);
    return Registry.register(Registries.BLOCK, Identifier.of(Gemstones.MOD_ID, name), block);
  }

  private static void registerBlockItem(String name, Block block) {
    Registry.register(Registries.ITEM, Identifier.of(Gemstones.MOD_ID, name),
        new BlockItem(block, new Item.Settings()));
  }

  public static void initialize() {
    Gemstones.LOGGER.info("Registering blocks for {}", Gemstones.MOD_ID);
  }
}
