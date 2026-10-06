package com.mtrpsdlcd.block;

import com.mtrpsdlcd.block.entity.MyPSDTopLcd8BE;
import com.mtrpsdlcd.registry.Items;
import javax.annotation.Nonnull;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;

public class MyPSDTopLcd8 extends MyPSDTop implements IStandaloneTopModule {
   public MyPSDTopLcd8() {
   }

   @Nonnull
   public Item asItem() {
      return (Item)Items.PSD_TOP_LCD8.get();
   }

   @Nonnull
   public BlockEntityMapper createBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new MyPSDTopLcd8BE(blockPos, blockState);
   }
}
