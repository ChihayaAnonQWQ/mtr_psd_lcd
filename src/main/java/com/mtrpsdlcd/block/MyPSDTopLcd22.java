package com.mtrpsdlcd.block;

import com.mtrpsdlcd.block.entity.MyPSDTopLcd22BE;
import com.mtrpsdlcd.registry.Items;
import javax.annotation.Nonnull;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;

public class MyPSDTopLcd22 extends MyPSDTopLcd14 {
   public MyPSDTopLcd22() {
   }

   @Nonnull
   public Item asItem() {
      return (Item)Items.PSD_GLASS_LCD22.get();
   }

   @Nonnull
   public BlockEntityMapper createBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new MyPSDTopLcd22BE(blockPos, blockState);
   }
}
