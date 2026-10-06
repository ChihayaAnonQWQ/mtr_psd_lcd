package com.mtrpsdlcd.block.entity;

import com.mtrpsdlcd.registry.BlockEntities;
import mtr.block.BlockPSDAPGDoorBase;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MyPSDDoorLcd3BE extends BlockPSDAPGDoorBase.TileEntityPSDAPGDoorBase {
   public MyPSDDoorLcd3BE(BlockPos pos, BlockState state) {
      this((BlockEntityType)BlockEntities.PSD_DOOR_LCD3.get(), pos, state);
   }

   public MyPSDDoorLcd3BE(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
      super(type, pos, state);
   }
}
