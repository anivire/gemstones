package name.modid.datagen.providers;

import java.util.concurrent.CompletableFuture;

import name.modid.core.api.tags.TagsRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

public class BlockTagProvider extends FabricTagProvider.BlockTagProvider {
  public BlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
    super(output, registriesFuture);
  }

  @Override
  protected void configure(RegistryWrapper.WrapperLookup arg) {
    getOrCreateTagBuilder(TagsRegistry.ALL_ORES)
        .addOptionalTag(BlockTags.COAL_ORES)
        .addOptionalTag(BlockTags.IRON_ORES)
        .addOptionalTag(BlockTags.COPPER_ORES)
        .addOptionalTag(BlockTags.GOLD_ORES)
        .addOptionalTag(BlockTags.REDSTONE_ORES)
        .addOptionalTag(BlockTags.EMERALD_ORES)
        .addOptionalTag(BlockTags.LAPIS_ORES)
        .addOptionalTag(BlockTags.DIAMOND_ORES);

    getOrCreateTagBuilder(TagsRegistry.DEEPSLATE_ORES)
        .add(Blocks.DEEPSLATE_COAL_ORE)
        .add(Blocks.DEEPSLATE_IRON_ORE)
        .add(Blocks.DEEPSLATE_COPPER_ORE)
        .add(Blocks.DEEPSLATE_GOLD_ORE)
        .add(Blocks.DEEPSLATE_REDSTONE_ORE)
        .add(Blocks.DEEPSLATE_EMERALD_ORE)
        .add(Blocks.DEEPSLATE_LAPIS_ORE)
        .add(Blocks.DEEPSLATE_DIAMOND_ORE);
  }
}