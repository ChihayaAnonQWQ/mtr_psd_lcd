package com.mtrpsdlcd.block.entity;

import com.mtrpsdlcd.registry.BlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MyPSDTopLcd6BE extends MyPSDTopBE {
   public MyPSDTopLcd6BE(BlockPos pos, BlockState state) {
      this((BlockEntityType)BlockEntities.PSD_TOP_LCD6.get(), pos, state);
   }

   public MyPSDTopLcd6BE(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
      super(type, pos, state);
   }
}
