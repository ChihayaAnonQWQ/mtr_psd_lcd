package com.mtrpsdlcd.block;

import com.mtrpsdlcd.block.entity.MyPSDTopLcd11BE;
import com.mtrpsdlcd.registry.Items;
import javax.annotation.Nonnull;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;

public class MyPSDTopLcd11 extends MyPSDTopLcd7 implements IStandaloneTopModule {
   public MyPSDTopLcd11() {
   }

   @Nonnull
   public Item asItem() {
      return (Item)Items.PSD_TOP_LCD11.get();
   }

   @Nonnull
   public BlockEntityMapper createBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new MyPSDTopLcd11BE(blockPos, blockState);
   }
}
