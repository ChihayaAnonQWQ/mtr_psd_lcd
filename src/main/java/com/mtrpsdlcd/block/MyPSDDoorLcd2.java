package com.mtrpsdlcd.block;

import com.mtrpsdlcd.block.entity.MyPSDDoorLcd2BE;
import com.mtrpsdlcd.registry.Items;
import javax.annotation.Nonnull;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;

public class MyPSDDoorLcd2 extends MyPSDDoor {
   public MyPSDDoorLcd2() {
      super(1);
   }

   @Nonnull
   public Item asItem() {
      return (Item)Items.PSD_DOOR_LCD2.get();
   }

   @Nonnull
   public BlockEntityMapper createBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new MyPSDDoorLcd2BE(blockPos, blockState);
   }
}
