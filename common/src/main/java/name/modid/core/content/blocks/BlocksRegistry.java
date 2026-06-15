package name.modid.core.content.blocks;

import name.modid.Gemstones;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;

public class BlocksRegistry {
  private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Gemstones.MOD_ID, RegistryKeys.BLOCK);
  private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Gemstones.MOD_ID, RegistryKeys.ITEM);

  public static final RegistrySupplier<Block> JEWELRY_TABLE = registerBlock("jewelry_table",
      () -> new JewelryTable(AbstractBlock.Settings.create()
          .nonOpaque()
          .strength(2.5F)
          .sounds(BlockSoundGroup.WOOD)
          .burnable()));

  private static <T extends Block> RegistrySupplier<T> registerBlock(String name, java.util.function.Supplier<T> block) {
    RegistrySupplier<T> registeredBlock = BLOCKS.register(name, block);
    ITEMS.register(name, () -> new BlockItem(registeredBlock.get(), new Item.Settings()));
    return registeredBlock;
  }

  public static Block jewelryTable() {
    return JEWELRY_TABLE.get();
  }

  public static void initialize() {
    Gemstones.LOGGER.info("Registering blocks for {}", Gemstones.MOD_ID);
    BLOCKS.register();
    ITEMS.register();
  }
}
