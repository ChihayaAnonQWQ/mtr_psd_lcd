package com.mtrpsdlcd.block;

import com.mtrpsdlcd.block.entity.MyPSDTopLcd9BE;
import com.mtrpsdlcd.registry.Items;
import javax.annotation.Nonnull;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;

public class MyPSDTopLcd9 extends MyPSDTopLcd3 implements IStandaloneTopModule {
   public MyPSDTopLcd9() {
   }

   @Nonnull
   public Item asItem() {
      return (Item)Items.PSD_TOP_LCD9.get();
   }

   @Nonnull
   public BlockEntityMapper createBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new MyPSDTopLcd9BE(blockPos, blockState);
   }
}
