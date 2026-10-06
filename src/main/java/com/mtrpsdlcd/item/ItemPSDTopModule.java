package com.mtrpsdlcd.item;

import com.mtrpsdlcd.block.MyPSDTopLcd14;
import com.mtrpsdlcd.block.MyPSDTopLcd18;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import mtr.block.BlockPSDTop;
import mtr.block.IBlock;
import mtr.block.IBlock.EnumSide;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ItemPSDTopModule extends Item implements IBlock {
   private final Supplier<net.minecraft.world.level.block.Block> moduleBlock;
   private final boolean doubleWidth;

   public ItemPSDTopModule(Supplier<net.minecraft.world.level.block.Block> moduleBlock, boolean doubleWidth, net.minecraft.world.item.Item.Properties itemSettings) {
      super(itemSettings);
      this.moduleBlock = moduleBlock;
      this.doubleWidth = doubleWidth;
   }

   @Nonnull
   public InteractionResult useOn(UseOnContext context) {
      int horizontal = this.doubleWidth ? 2 : 1;
      Level world = context.getLevel();
      Direction facing = context.getHorizontalDirection();
      BlockPos basePos = context.getClickedPos().relative(context.getClickedFace());

      for(int i = 0; i < horizontal; ++i) {
         BlockPos checkPos = basePos.relative(facing.getClockWise(), i);
         if (!world.getBlockState(checkPos).getBlock().equals(Blocks.AIR)) {
            return InteractionResult.FAIL;
         }
      }

      for(int i = 0; i < horizontal; ++i) {
         BlockPos newPos = basePos.relative(facing.getClockWise(), i);
         IBlock.EnumSide side = this.doubleWidth ? (i == 0 ? EnumSide.LEFT : EnumSide.RIGHT) : EnumSide.SINGLE;
         BlockState state = (BlockState)((BlockState)((Block)this.moduleBlock.get()).defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, facing)).setValue(IBlock.SIDE_EXTENDED, side);
         world.setBlockAndUpdate(newPos, state);
         world.setBlockAndUpdate(newPos, BlockPSDTop.getActualState(world, newPos));
      }

      Block placedBlock = world.getBlockState(basePos).getBlock();
      if (placedBlock instanceof MyPSDTopLcd14) {
         MyPSDTopLcd18.recomputeSide(world, basePos, facing);
      }

      context.getItemInHand().shrink(1);
      return InteractionResult.SUCCESS;
   }
}
