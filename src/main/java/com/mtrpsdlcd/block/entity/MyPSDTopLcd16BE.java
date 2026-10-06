package com.mtrpsdlcd.block.entity;

import com.mtrpsdlcd.registry.BlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MyPSDTopLcd16BE extends MyPSDTopBE {
   public MyPSDTopLcd16BE(BlockPos pos, BlockState state) {
      this((BlockEntityType)BlockEntities.PSD_TOP_LCD16.get(), pos, state);
   }

   public MyPSDTopLcd16BE(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
      super(type, pos, state);
   }
}
