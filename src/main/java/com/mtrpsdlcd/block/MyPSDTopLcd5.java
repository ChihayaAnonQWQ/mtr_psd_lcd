package com.mtrpsdlcd.block;

import com.mtrpsdlcd.block.entity.MyPSDTopLcd5BE;
import com.mtrpsdlcd.registry.Items;
import javax.annotation.Nonnull;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;

public class MyPSDTopLcd5 extends MyPSDTop {
   public MyPSDTopLcd5() {
   }

   @Nonnull
   public Item asItem() {
      return (Item)Items.PSD_DOOR_LCD5.get();
   }

   @Nonnull
   public BlockEntityMapper createBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new MyPSDTopLcd5BE(blockPos, blockState);
   }
}
