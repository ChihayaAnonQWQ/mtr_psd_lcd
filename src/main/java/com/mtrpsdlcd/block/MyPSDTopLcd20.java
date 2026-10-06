package com.mtrpsdlcd.block;

import com.mtrpsdlcd.block.entity.MyPSDTopLcd14BE;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd20BE;
import com.mtrpsdlcd.registry.Items;
import javax.annotation.Nonnull;
import mtr.block.BlockPSDTop;
import mtr.block.IBlock;
import mtr.block.BlockPSDTop.EnumPersistent;
import mtr.block.IBlock.EnumSide;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class MyPSDTopLcd20 extends MyPSDTopLcd14 {
   public MyPSDTopLcd20() {
   }

   @Nonnull
   public Item asItem() {
      return (Item)Items.PSD_TOP_LCD20.get();
   }

   @Nonnull
   public BlockEntityMapper createBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new MyPSDTopLcd20BE(blockPos, blockState);
   }

   public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
      super.playerWillDestroy(world, pos, state, player);
      Direction facing = (Direction)IBlock.getStatePropertySafe(state, BlockPSDTop.FACING);
      recomputeSideAfterBreak(world, pos, facing);
   }

   @Nonnull
   public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockHitResult) {
      return IBlock.checkHoldingBrush(world, player, () -> {
         BlockEntity be = world.getBlockEntity(pos);
         if (be instanceof MyPSDTopLcd20BE lcd18be) {
            BlockPSDTop.EnumPersistent current = (BlockPSDTop.EnumPersistent)IBlock.getStatePropertySafe(state, BlockPSDTop.PERSISTENT);
            int newIndex;
            if (current == EnumPersistent.NONE) {
               world.setBlockAndUpdate(pos, (BlockState)state.setValue(BlockPSDTop.PERSISTENT, EnumPersistent.ROUTE));
               newIndex = 0;
            } else {
               newIndex = lcd18be.getRouteIndex();
               boolean flipped = !PSDCustomText.readDirectionFlip(world, pos);
               PSDCustomText.applyDirectionFlip(world, pos, flipped);
            }

            lcd18be.setRouteIndex(newIndex);
            Direction facing = (Direction)IBlock.getStatePropertySafe(state, BlockPSDTop.FACING);
            this.propagateRouteIndex(world, pos, facing.getClockWise(), newIndex);
            this.propagateRouteIndex(world, pos, facing.getCounterClockWise(), newIndex);
            this.propagatePersistent(world, pos, facing.getClockWise(), EnumPersistent.ROUTE);
            this.propagatePersistent(world, pos, facing.getCounterClockWise(), EnumPersistent.ROUTE);
         }

      });
   }

   private void propagateRouteIndex(Level world, BlockPos pos, Direction dir, int index) {
      int i = 1;

      while(true) {
         BlockPos nextPos = pos.relative(dir, i);
         BlockState neighbor = world.getBlockState(nextPos);
         if (!(neighbor.getBlock() instanceof MyPSDTopLcd20)) {
            return;
         }

         BlockEntity be = world.getBlockEntity(nextPos);
         if (be instanceof MyPSDTopLcd14BE) {
            ((MyPSDTopLcd14BE)be).setRouteIndex(index);
         }

         ++i;
      }
   }

   private void propagatePersistent(Level world, BlockPos pos, Direction dir, BlockPSDTop.EnumPersistent value) {
      int i = 1;

      while(true) {
         BlockPos nextPos = pos.relative(dir, i);
         BlockState neighbor = world.getBlockState(nextPos);
         if (!(neighbor.getBlock() instanceof MyPSDTopLcd20)) {
            return;
         }

         world.setBlockAndUpdate(nextPos, (BlockState)neighbor.setValue(BlockPSDTop.PERSISTENT, value));
         ++i;
      }
   }

   public static void recomputeSide(Level world, BlockPos pos, Direction facing) {
      recomputeSideRun(world, pos, facing, (BlockPos)null);
   }

   public static void recomputeSideAfterBreak(Level world, BlockPos brokenPos, Direction facing) {
      recomputeSideRun(world, brokenPos, facing, brokenPos);
   }

   private static void recomputeSideRun(Level world, BlockPos anchor, Direction facing, BlockPos excluded) {
      for(BlockPos p = anchor.relative(facing.getCounterClockWise()); world.getBlockState(p).getBlock() instanceof MyPSDTopLcd20; p = p.relative(facing.getCounterClockWise())) {
         recalcGlassSide(world, p, facing, excluded);
      }

      for(BlockPos p = anchor.relative(facing.getClockWise()); world.getBlockState(p).getBlock() instanceof MyPSDTopLcd20; p = p.relative(facing.getClockWise())) {
         recalcGlassSide(world, p, facing, excluded);
      }

      if (!anchor.equals(excluded)) {
         recalcGlassSide(world, anchor, facing, excluded);
      }

   }

   private static void recalcGlassSide(Level world, BlockPos pos, Direction facing, BlockPos excluded) {
      BlockState state = world.getBlockState(pos);
      if (state.getBlock() instanceof MyPSDTopLcd20) {
         BlockState leftState = world.getBlockState(pos.relative(facing.getCounterClockWise()));
         boolean hasLeft = !pos.relative(facing.getCounterClockWise()).equals(excluded) && leftState.getBlock() instanceof MyPSDTopLcd20;
         BlockState rightState = world.getBlockState(pos.relative(facing.getClockWise()));
         boolean hasRight = !pos.relative(facing.getClockWise()).equals(excluded) && rightState.getBlock() instanceof MyPSDTopLcd20;
         IBlock.EnumSide ownSide = hasLeft && hasRight ? EnumSide.MIDDLE : (hasLeft ? EnumSide.RIGHT : (hasRight ? EnumSide.LEFT : EnumSide.SINGLE));
         world.setBlockAndUpdate(pos, (BlockState)state.setValue(IBlock.SIDE_EXTENDED, ownSide));
      }
   }
}
