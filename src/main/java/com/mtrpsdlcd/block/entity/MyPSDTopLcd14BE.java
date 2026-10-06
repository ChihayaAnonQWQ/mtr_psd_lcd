package com.mtrpsdlcd.block.entity;

import com.mtrpsdlcd.registry.BlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MyPSDTopLcd14BE extends MyPSDTopBE {
   private static final String TAG_ROUTE_INDEX = "routeIndex";
   private int routeIndex = 0;

   public MyPSDTopLcd14BE(BlockPos pos, BlockState state) {
      this((BlockEntityType)BlockEntities.PSD_TOP_LCD14.get(), pos, state);
   }

   public MyPSDTopLcd14BE(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
      super(type, pos, state);
   }

   public int getRouteIndex() {
      return this.routeIndex;
   }

   public void setRouteIndex(int index) {
      this.routeIndex = index;
      this.setChanged();
      this.syncToClient();
   }

   public void writeCompoundTag(CompoundTag compoundTag) {
      super.writeCompoundTag(compoundTag);
      compoundTag.putInt("routeIndex", this.routeIndex);
   }

   public void readCompoundTag(CompoundTag compoundTag) {
      super.readCompoundTag(compoundTag);
      this.routeIndex = compoundTag.contains("routeIndex") ? compoundTag.getInt("routeIndex") : 0;
   }
}
