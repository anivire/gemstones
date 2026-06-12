package name.modid.core.content.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class BlockInTagLootCondition implements LootCondition {
  public static final MapCodec<BlockInTagLootCondition> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance
          .group(
              TagKey.codec(RegistryKeys.BLOCK)
                  .fieldOf("tag")
                  .forGetter(c -> c.tag))
          .apply(instance, BlockInTagLootCondition::new));

  private final TagKey<Block> tag;

  public BlockInTagLootCondition(TagKey<Block> tag) {
    this.tag = tag;
  }

  @Override
  public LootConditionType getType() {
    return LootConditionRegistry.blockInTag();
  }

  @Override
  public boolean test(LootContext context) {
    BlockState state = context.get(LootContextParameters.BLOCK_STATE);
    return state != null && state.isIn(tag);
  }

  public static Builder builder(TagKey<Block> tag) {
    return () -> new BlockInTagLootCondition(tag);
  }
}
