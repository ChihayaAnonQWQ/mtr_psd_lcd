package com.mtrpsdlcd.block;

import com.mtrpsdlcd.block.entity.MyPSDDoorLcd3BE;
import com.mtrpsdlcd.registry.Items;
import javax.annotation.Nonnull;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;

public class MyPSDDoorLcd3 extends MyPSDDoor {
   public MyPSDDoorLcd3() {
      super(0);
   }

   @Nonnull
   public Item asItem() {
      return (Item)Items.PSD_DOOR_LCD3.get();
   }

   @Nonnull
   public BlockEntityMapper createBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new MyPSDDoorLcd3BE(blockPos, blockState);
   }
}
