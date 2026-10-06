package com.mtrpsdlcd.block;

import com.mtrpsdlcd.Diag;
import com.mtrpsdlcd.block.entity.MyPSDDoorBE;
import com.mtrpsdlcd.registry.BlockEntities;
import com.mtrpsdlcd.registry.Items;
import javax.annotation.Nonnull;
import mtr.block.BlockPSDAPGDoorBase;
import mtr.block.BlockPSDDoor;
import mtr.block.IBlock;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class MyPSDDoor extends BlockPSDDoor {
   private final int style;

   public MyPSDDoor(int style) {
      super(style);
      this.style = style;
   }

   @Nonnull
   public BlockEntityMapper createBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new MyPSDDoorBE(this.style == 0 ? (BlockEntityType)BlockEntities.PSD_DOOR.get() : (BlockEntityType)BlockEntities.PSD_DOOR_2.get(), blockPos, blockState);
   }

   public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
      Diag.breakLog(state, pos, player);
      super.playerWillDestroy(world, pos, state, player);
   }

   @Nonnull
   public Item asItem() {
      return this.style == 0 ? (Item)Items.PSD_DOOR.get() : (Item)Items.PSD_DOOR_2.get();
   }

   public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockHitResult) {
      return IBlock.checkHoldingBrush(world, player, () -> {
         boolean unlocked = IBlock.getStatePropertySafe(state, BlockPSDAPGDoorBase.UNLOCKED);

         for(int y = -1; y <= 1; ++y) {
            BlockState scanState = world.getBlockState(pos.above(y));
            if (state.is(scanState.getBlock())) {
               this.lockDoor(world, pos.above(y), scanState, !unlocked);
            }
         }

         player.displayClientMessage(Component.translatable(unlocked ? "gui.mtr.psd_apg_door_locked" : "gui.mtr.psd_apg_door_unlocked"), true);
      });
   }

   private void lockDoor(Level world, BlockPos pos, BlockState state, boolean unlocked) {
      Direction facing = (Direction)IBlock.getStatePropertySafe(state, BlockPSDAPGDoorBase.FACING);
      BlockPos leftPos = pos.relative(facing.getCounterClockWise());
      BlockPos rightPos = pos.relative(facing.getClockWise());
      BlockState leftState = world.getBlockState(leftPos);
      BlockState rightState = world.getBlockState(rightPos);
      if (leftState.is(state.getBlock())) {
         BlockState toggled = (BlockState)leftState.setValue(BlockPSDAPGDoorBase.UNLOCKED, unlocked);
         world.setBlockAndUpdate(leftPos, toggled);
      }

      if (rightState.is(state.getBlock())) {
         BlockState toggled = (BlockState)rightState.setValue(BlockPSDAPGDoorBase.UNLOCKED, unlocked);
         world.setBlockAndUpdate(rightPos, toggled);
      }

      world.setBlockAndUpdate(pos, (BlockState)state.setValue(BlockPSDAPGDoorBase.UNLOCKED, unlocked));
   }
}
