package com.mtrpsdlcd.block;

import com.mtrpsdlcd.block.entity.MyPSDTopLcd14BE;
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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MyPSDTopLcd14 extends MyPSDTop implements IStandaloneTopModule {
   public MyPSDTopLcd14() {
   }

   @Nonnull
   public Item asItem() {
      return (Item)Items.PSD_GLASS_LCD14.get();
   }

   @Nonnull
   public BlockEntityMapper createBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new MyPSDTopLcd14BE(blockPos, blockState);
   }

   @Nonnull
   public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockHitResult) {
      return IBlock.checkHoldingBrush(world, player, () -> {
         BlockEntity be = world.getBlockEntity(pos);
         if (be instanceof MyPSDTopLcd14BE lcd14be) {
            BlockPSDTop.EnumPersistent current = (BlockPSDTop.EnumPersistent)IBlock.getStatePropertySafe(state, BlockPSDTop.PERSISTENT);
            int newIndex;
            if (current == EnumPersistent.NONE) {
               world.setBlockAndUpdate(pos, (BlockState)state.setValue(BlockPSDTop.PERSISTENT, EnumPersistent.ROUTE));
               newIndex = 0;
            } else {
               newIndex = lcd14be.getRouteIndex();
               boolean flipped = !PSDCustomText.readDirectionFlip(world, pos);
               PSDCustomText.applyDirectionFlip(world, pos, flipped);
            }

            lcd14be.setRouteIndex(newIndex);
            Direction facing = (Direction)IBlock.getStatePropertySafe(state, BlockPSDTop.FACING);
            this.propagateRouteIndex(world, pos, facing.getClockWise(), newIndex);
            this.propagateRouteIndex(world, pos, facing.getCounterClockWise(), newIndex);
            this.propagatePersistent(world, pos, facing.getClockWise(), EnumPersistent.ROUTE);
            this.propagatePersistent(world, pos, facing.getCounterClockWise(), EnumPersistent.ROUTE);
            MyPSDTopLcd18.recomputeSide(world, pos, facing);
            this.mergeGlassRun(world, pos, facing);
         }

      });
   }

   private void mergeGlassRun(Level world, BlockPos anchor, Direction facing) {
      this.recalcGlassColumn(world, anchor, facing);

      for(int dir = 0; dir < 2; ++dir) {
         Direction d = dir == 0 ? facing.getClockWise() : facing.getCounterClockWise();

         for(BlockPos p = anchor.relative(d); world.getBlockState(p).getBlock() instanceof MyPSDTopLcd14; p = p.relative(d)) {
            this.recalcGlassColumn(world, p, facing);
         }
      }

   }

   private void recalcGlassColumn(Level world, BlockPos topPos, Direction facing) {
      for(int dy = 1; dy <= 2; ++dy) {
         BlockPos gPos = topPos.below(dy);
         BlockState gState = world.getBlockState(gPos);
         Block gBlock = gState.getBlock();
         if (isOurPsdGlass(gBlock)) {
            boolean hasLeft = world.getBlockState(gPos.relative(facing.getCounterClockWise())).getBlock().getClass() == gBlock.getClass();
            boolean hasRight = world.getBlockState(gPos.relative(facing.getClockWise())).getBlock().getClass() == gBlock.getClass();
            IBlock.EnumSide ownSide = hasLeft && hasRight ? EnumSide.MIDDLE : (hasLeft ? EnumSide.RIGHT : (hasRight ? EnumSide.LEFT : EnumSide.SINGLE));
            world.setBlockAndUpdate(gPos, (BlockState)gState.setValue(IBlock.SIDE_EXTENDED, ownSide));
         }
      }

   }

   private static boolean isOurPsdGlass(Block block) {
      return block instanceof MyPSDGlassLcd14 || block instanceof MyPSDGlassLcd17 || block instanceof MyPSDGlassLcd22;
   }

   private void propagateRouteIndex(Level world, BlockPos pos, Direction dir, int index) {
      int i = 1;

      while(true) {
         BlockPos nextPos = pos.relative(dir, i);
         BlockState neighbor = world.getBlockState(nextPos);
         if (!(neighbor.getBlock() instanceof MyPSDTopLcd14)) {
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
         if (!(neighbor.getBlock() instanceof MyPSDTopLcd14)) {
            return;
         }

         world.setBlockAndUpdate(nextPos, (BlockState)neighbor.setValue(BlockPSDTop.PERSISTENT, value));
         ++i;
      }
   }

   @Nonnull
   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return IBlock.getVoxelShapeByDirection(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 6.0D, (Direction)IBlock.getStatePropertySafe(state, BlockPSDTop.FACING));
   }

   public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
      super.playerWillDestroy(world, pos, state, player);
      Direction facing = (Direction)IBlock.getStatePropertySafe(state, BlockPSDTop.FACING);
      this.refreshTopSide(world, pos, facing);
   }

   private void refreshTopSide(Level world, BlockPos pos, Direction facing) {
      int i = 1;

      while(true) {
         BlockPos nextPos = pos.relative(facing.getClockWise(), i);
         if (!(world.getBlockState(nextPos).getBlock() instanceof MyPSDTopLcd14)) {
            i = 1;

            while(true) {
               nextPos = pos.relative(facing.getCounterClockWise(), i);
               if (!(world.getBlockState(nextPos).getBlock() instanceof MyPSDTopLcd14)) {
                  return;
               }

               this.recalcTopSide(world, nextPos, facing);
               ++i;
            }
         }

         this.recalcTopSide(world, nextPos, facing);
         ++i;
      }
   }

   private void recalcTopSide(Level world, BlockPos pos, Direction facing) {
      BlockState state = world.getBlockState(pos);
      boolean hasLeft = world.getBlockState(pos.relative(facing.getCounterClockWise())).getBlock() instanceof MyPSDTopLcd14;
      boolean hasRight = world.getBlockState(pos.relative(facing.getClockWise())).getBlock() instanceof MyPSDTopLcd14;
      IBlock.EnumSide ownSide = hasLeft && hasRight ? EnumSide.MIDDLE : (hasLeft ? EnumSide.RIGHT : (hasRight ? EnumSide.LEFT : EnumSide.SINGLE));
      world.setBlockAndUpdate(pos, (BlockState)state.setValue(IBlock.SIDE_EXTENDED, ownSide));
   }
}
