package name.modid.core.content.blocks;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.MapCodec;

import name.modid.core.content.blocks.entity.JewelryTableBlockEntity;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class JewelryTable extends BlockWithEntity implements BlockEntityProvider {
  public static final MapCodec<JewelryTable> CODEC = JewelryTable.createCodec(JewelryTable::new);

  protected JewelryTable(Settings settings) {
    super(settings);
  }

  @Override
  protected MapCodec<? extends BlockWithEntity> getCodec() {
    return CODEC;
  }

  @Nullable
  @Override
  public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    return new JewelryTableBlockEntity(pos, state);

  }

  @Override
  protected BlockRenderType getRenderType(BlockState state) {
    return BlockRenderType.MODEL;
  }

  @Override
  protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
    if (state.getBlock() != newState.getBlock()) {
      BlockEntity blockEntity = world.getBlockEntity(pos);
      if (blockEntity instanceof JewelryTableBlockEntity) {
        ItemScatterer.spawn(world, pos, ((JewelryTableBlockEntity) blockEntity));
        world.updateComparators(pos, this);
      }
      super.onStateReplaced(state, world, pos, newState, moved);
    }
  }

  @Override
  protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos,
      PlayerEntity player, Hand hand, BlockHitResult hit) {
    if (world.getBlockEntity(pos) instanceof JewelryTableBlockEntity blockEntity) {
      player.openHandledScreen(blockEntity);
    }

    return ItemActionResult.SUCCESS;
  }

}
