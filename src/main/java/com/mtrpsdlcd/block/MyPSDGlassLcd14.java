package com.mtrpsdlcd.block;

import com.mtrpsdlcd.Diag;
import com.mtrpsdlcd.registry.Items;
import javax.annotation.Nonnull;
import mtr.block.BlockPSDGlass;
import mtr.block.BlockPSDTop;
import mtr.block.IBlock;
import mtr.block.BlockPSDTop.EnumPersistent;
import mtr.block.IBlock.EnumSide;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;

public class MyPSDGlassLcd14 extends BlockPSDGlass {
   public MyPSDGlassLcd14() {
      super(0);
   }

   @Nonnull
   public Item asItem() {
      return (Item)Items.PSD_GLASS_LCD14.get();
   }

   @Nonnull
   public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockHitResult) {
      return IBlock.checkHoldingBrush(world, player, () -> {
         for(int dy = -1; dy <= 1; ++dy) {
            BlockPos upPos = pos.above(dy);
            BlockState upState = world.getBlockState(upPos);
            if (upState.getBlock() instanceof MyPSDGlassLcd14) {
               this.connectGlass(world, upPos, upState);
            }
         }

         DoubleBlockHalf half = (DoubleBlockHalf)IBlock.getStatePropertySafe(state, IBlock.HALF);
         BlockPos topPos = half == DoubleBlockHalf.LOWER ? pos.above(2) : pos.above(1);
         BlockState topState = world.getBlockState(topPos);
         if (topState.getBlock() instanceof MyPSDTopLcd14) {
            BlockPSDTop.EnumPersistent current = (BlockPSDTop.EnumPersistent)IBlock.getStatePropertySafe(topState, BlockPSDTop.PERSISTENT);
            BlockPSDTop.EnumPersistent next = current == EnumPersistent.ROUTE ? EnumPersistent.NONE : EnumPersistent.ROUTE;
            Direction facing = (Direction)IBlock.getStatePropertySafe(state, BlockPSDTop.FACING);
            this.toggleTopPersistent(world, topPos, next);
            this.propagateGlassChain(world, pos, facing.getClockWise(), next);
            this.propagateGlassChain(world, pos, facing.getCounterClockWise(), next);
         }

      });
   }

   private void toggleTopPersistent(Level world, BlockPos topPos, BlockPSDTop.EnumPersistent value) {
      BlockState top = world.getBlockState(topPos);
      if (top.getBlock() instanceof MyPSDTopLcd14) {
         world.setBlockAndUpdate(topPos, (BlockState)top.setValue(BlockPSDTop.PERSISTENT, value));
      }

   }

   private void propagateGlassChain(Level world, BlockPos glassPos, Direction dir, BlockPSDTop.EnumPersistent value) {
      int i = 1;

      while(true) {
         BlockPos nextGlass = glassPos.relative(dir, i);
         BlockState nextState = world.getBlockState(nextGlass);
         if (!(nextState.getBlock() instanceof MyPSDGlassLcd14)) {
            return;
         }

         DoubleBlockHalf half = (DoubleBlockHalf)IBlock.getStatePropertySafe(nextState, IBlock.HALF);
         this.toggleTopPersistent(world, half == DoubleBlockHalf.LOWER ? nextGlass.above(2) : nextGlass.above(1), value);
         ++i;
      }
   }

   private void connectGlass(Level world, BlockPos pos, BlockState state) {
      Direction facing = (Direction)IBlock.getStatePropertySafe(state, BlockPSDTop.FACING);
      BlockPos leftPos = pos.relative(facing.getCounterClockWise());
      BlockState leftState = world.getBlockState(leftPos);
      boolean hasLeft = leftState.getBlock() instanceof MyPSDGlassLcd14;
      if (hasLeft) {
         IBlock.EnumSide leftSide = (IBlock.EnumSide)IBlock.getStatePropertySafe(leftState, IBlock.SIDE_EXTENDED);
         IBlock.EnumSide newLeft = leftSide == EnumSide.RIGHT ? EnumSide.MIDDLE : (leftSide == EnumSide.SINGLE ? EnumSide.LEFT : leftSide);
         world.setBlockAndUpdate(leftPos, (BlockState)leftState.setValue(IBlock.SIDE_EXTENDED, newLeft));
      }

      BlockPos rightPos = pos.relative(facing.getClockWise());
      BlockState rightState = world.getBlockState(rightPos);
      boolean hasRight = rightState.getBlock() instanceof MyPSDGlassLcd14;
      if (hasRight) {
         IBlock.EnumSide rightSide = (IBlock.EnumSide)IBlock.getStatePropertySafe(rightState, IBlock.SIDE_EXTENDED);
         IBlock.EnumSide newRight = rightSide == EnumSide.LEFT ? EnumSide.MIDDLE : (rightSide == EnumSide.SINGLE ? EnumSide.RIGHT : rightSide);
         world.setBlockAndUpdate(rightPos, (BlockState)rightState.setValue(IBlock.SIDE_EXTENDED, newRight));
      }

      IBlock.EnumSide ownSide = hasLeft && hasRight ? EnumSide.MIDDLE : (hasLeft ? EnumSide.RIGHT : (hasRight ? EnumSide.LEFT : EnumSide.SINGLE));
      world.setBlockAndUpdate(pos, (BlockState)state.setValue(IBlock.SIDE_EXTENDED, ownSide));
   }

   public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
      Diag.breakLog(state, pos, player);
      DoubleBlockHalf half = (DoubleBlockHalf)IBlock.getStatePropertySafe(state, IBlock.HALF);
      BlockPos topPos = half == DoubleBlockHalf.LOWER ? pos.above(2) : pos.above(1);
      BlockState topState = world.getBlockState(topPos);
      if (topState.getBlock() instanceof MyPSDTopLcd14) {
         world.setBlock(topPos, Blocks.AIR.defaultBlockState(), 34);
      }

      super.playerWillDestroy(world, pos, state, player);
      Direction facing = (Direction)IBlock.getStatePropertySafe(state, BlockPSDTop.FACING);
      this.recalcGlassSide(world, pos, facing, facing.getClockWise());
      this.recalcGlassSide(world, pos, facing, facing.getCounterClockWise());
   }

   private void recalcGlassSide(Level world, BlockPos pos, Direction facing, Direction dir) {
      int i = 1;

      while(true) {
         BlockPos nextPos = pos.relative(dir, i);
         BlockState neighbor = world.getBlockState(nextPos);
         DoubleBlockHalf half = (DoubleBlockHalf)IBlock.getStatePropertySafe(neighbor, IBlock.HALF);
         boolean isCol = neighbor.getBlock() instanceof MyPSDGlassLcd14;
         if (!isCol) {
            return;
         }

         BlockPos other = half == DoubleBlockHalf.LOWER ? nextPos.above(1) : nextPos.below(1);
         this.recalcOneGlassTop(world, nextPos, facing);
         if (world.getBlockState(other).getBlock() instanceof MyPSDGlassLcd14) {
            this.recalcOneGlassTop(world, other, facing);
         }

         ++i;
      }
   }

   private void recalcOneGlassTop(Level world, BlockPos pos, Direction facing) {
      BlockState state = world.getBlockState(pos);
      boolean hasLeft = world.getBlockState(pos.relative(facing.getCounterClockWise())).getBlock() instanceof MyPSDGlassLcd14;
      boolean hasRight = world.getBlockState(pos.relative(facing.getClockWise())).getBlock() instanceof MyPSDGlassLcd14;
      IBlock.EnumSide ownSide = hasLeft && hasRight ? EnumSide.MIDDLE : (hasLeft ? EnumSide.RIGHT : (hasRight ? EnumSide.LEFT : EnumSide.SINGLE));
      world.setBlockAndUpdate(pos, (BlockState)state.setValue(IBlock.SIDE_EXTENDED, ownSide));
   }
}
