package com.mtrpsdlcd.block;

import com.mtrpsdlcd.block.entity.MyPSDTopLcd12BE;
import com.mtrpsdlcd.registry.Items;
import javax.annotation.Nonnull;
import mtr.block.BlockPSDTop;
import mtr.block.IBlock;
import mtr.block.BlockPSDTop.EnumPersistent;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MyPSDTopLcd12 extends MyPSDTop implements IStandaloneTopModule {
   public MyPSDTopLcd12() {
   }

   @Nonnull
   public Item asItem() {
      return (Item)Items.PSD_TOP_LCD12.get();
   }

   @Nonnull
   public BlockEntityMapper createBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new MyPSDTopLcd12BE(blockPos, blockState);
   }

   @Nonnull
   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return IBlock.getVoxelShapeByDirection(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 6.0D, (Direction)IBlock.getStatePropertySafe(state, BlockPSDTop.FACING));
   }

   @Nonnull
   public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockHitResult) {
      return IBlock.checkHoldingItem(world, player, (item) -> {
         if (item == mtr.Items.BRUSH.get()) {
            BlockPSDTop.EnumPersistent current = (BlockPSDTop.EnumPersistent)IBlock.getStatePropertySafe(state, BlockPSDTop.PERSISTENT);
            Direction facing = (Direction)IBlock.getStatePropertySafe(state, BlockPSDTop.FACING);
            if (current != EnumPersistent.ROUTE) {
               world.setBlockAndUpdate(pos, (BlockState)state.setValue(BlockPSDTop.PERSISTENT, EnumPersistent.ROUTE));
               this.propagatePersistent(world, pos, facing.getClockWise(), EnumPersistent.ROUTE);
               this.propagatePersistent(world, pos, facing.getCounterClockWise(), EnumPersistent.ROUTE);
            } else {
               int arrow = IBlock.getStatePropertySafe(state, BlockPSDTop.ARROW_DIRECTION);
               int nextArrow = arrow == 2 ? 0 : 2;
               world.setBlockAndUpdate(pos, (BlockState)state.setValue(BlockPSDTop.ARROW_DIRECTION, nextArrow));
               this.propagateArrow(world, pos, facing.getClockWise(), nextArrow);
               this.propagateArrow(world, pos, facing.getCounterClockWise(), nextArrow);
            }
         }

      }, (Runnable)null, new Item[]{(Item)mtr.Items.BRUSH.get(), net.minecraft.world.item.Items.SHEARS});
   }

   private void propagatePersistent(Level world, BlockPos pos, Direction dir, BlockPSDTop.EnumPersistent value) {
      int i = 1;

      while(true) {
         BlockPos nextPos = pos.relative(dir, i);
         BlockState neighbor = world.getBlockState(nextPos);
         Block neighborBlock = neighbor.getBlock();
         if (!(neighborBlock instanceof MyPSDTopLcd12)) {
            return;
         }

         world.setBlockAndUpdate(nextPos, (BlockState)neighbor.setValue(BlockPSDTop.PERSISTENT, value));
         ++i;
      }
   }

   private void propagateArrow(Level world, BlockPos pos, Direction dir, int value) {
      int i = 1;

      while(true) {
         BlockPos nextPos = pos.relative(dir, i);
         BlockState neighbor = world.getBlockState(nextPos);
         if (!(neighbor.getBlock() instanceof MyPSDTopLcd12)) {
            return;
         }

         world.setBlockAndUpdate(nextPos, (BlockState)neighbor.setValue(BlockPSDTop.ARROW_DIRECTION, value));
         ++i;
      }
   }
}
