package com.mtrpsdlcd.block;

import com.mtrpsdlcd.block.entity.MyPSDTopLcd13BE;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MyPSDTopLcd13 extends MyPSDTop implements IStandaloneTopModule {
   public MyPSDTopLcd13() {
   }

   @Nonnull
   public Item asItem() {
      return (Item)Items.PSD_GLASS_LCD13.get();
   }

   @Nonnull
   public BlockEntityMapper createBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new MyPSDTopLcd13BE(blockPos, blockState);
   }

   @Nonnull
   public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockHitResult) {
      return IBlock.checkHoldingBrush(world, player, () -> {
         BlockPSDTop.EnumPersistent current = (BlockPSDTop.EnumPersistent)IBlock.getStatePropertySafe(state, BlockPSDTop.PERSISTENT);
         BlockPSDTop.EnumPersistent next = current == EnumPersistent.ROUTE ? EnumPersistent.NONE : EnumPersistent.ROUTE;
         world.setBlockAndUpdate(pos, (BlockState)state.setValue(BlockPSDTop.PERSISTENT, next));
         Direction facing = (Direction)IBlock.getStatePropertySafe(state, BlockPSDTop.FACING);
         this.propagatePersistent(world, pos, facing.getClockWise(), next);
         this.propagatePersistent(world, pos, facing.getCounterClockWise(), next);
      });
   }

   private void propagatePersistent(Level world, BlockPos pos, Direction dir, BlockPSDTop.EnumPersistent value) {
      int i = 1;

      while(true) {
         BlockPos nextPos = pos.relative(dir, i);
         BlockState neighbor = world.getBlockState(nextPos);
         if (!(neighbor.getBlock() instanceof MyPSDTopLcd13)) {
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
   }
}
