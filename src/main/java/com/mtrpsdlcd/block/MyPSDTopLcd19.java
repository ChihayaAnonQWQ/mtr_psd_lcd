package com.mtrpsdlcd.block;

import com.mtrpsdlcd.block.entity.MyPSDTopLcd19BE;
import com.mtrpsdlcd.registry.Items;
import javax.annotation.Nonnull;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;

public class MyPSDTopLcd19 extends MyPSDTopLcd15 {
   public MyPSDTopLcd19() {
   }

   @Nonnull
   public Item asItem() {
      return (Item)Items.PSD_DOOR_LCD19.get();
   }

   @Nonnull
   public BlockEntityMapper createBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new MyPSDTopLcd19BE(blockPos, blockState);
   }
}
