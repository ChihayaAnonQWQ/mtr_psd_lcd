package com.mtrpsdlcd.block.entity;

import com.mtrpsdlcd.registry.BlockEntities;
import mtr.block.BlockPSDTop;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MyPSDTopBE extends BlockPSDTop.TileEntityRouteBase {
   private static final String TAG_CUSTOM_TEXT = "customText";
   private static final String TAG_CUSTOM_IMAGE = "customImage";
   private static final String TAG_DIRECTION_FLIP = "directionFlip";
   private String customText = "\u5730\u94c1\u8f68\u4ea4";
   private String customImagePath = "";
   private boolean directionFlip = false;

   public boolean isDirectionFlip() {
      return this.directionFlip;
   }

   public void setDirectionFlip(boolean flip) {
      this.directionFlip = flip;
      this.setChanged();
      this.syncToClient();
   }

   public String getCustomText() {
      return this.customText != null && !this.customText.isEmpty() ? this.customText : "\u5730\u94c1\u8f68\u4ea4";
   }

   public void setCustomText(String text) {
      this.customText = text == null ? "" : text;
      this.setChanged();
      this.syncToClient();
   }

   public String getCustomImagePath() {
      return this.customImagePath == null ? "" : this.customImagePath;
   }

   public void setCustomImagePath(String path) {
      this.customImagePath = path == null ? "" : path;
      this.setChanged();
      this.syncToClient();
   }

   public net.minecraft.network.protocol.Packet<net.minecraft.network.protocol.game.ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag() {
      CompoundTag nbt = super.getUpdateTag();
      this.writeCompoundTag(nbt);
      return nbt;
   }

   protected void syncToClient() {
      if (this.level instanceof ServerLevel) {
         ((ServerLevel)this.level).getChunkSource().blockChanged(this.worldPosition);
      }

   }

   public void writeCompoundTag(CompoundTag compoundTag) {
      super.writeCompoundTag(compoundTag);
      compoundTag.putString("customText", this.customText == null ? "" : this.customText);
      compoundTag.putString("customImage", this.customImagePath == null ? "" : this.customImagePath);
      compoundTag.putBoolean("directionFlip", this.directionFlip);
   }

   public void readCompoundTag(CompoundTag compoundTag) {
      super.readCompoundTag(compoundTag);
      this.customText = compoundTag.contains("customText") ? compoundTag.getString("customText") : "\u5730\u94c1\u8f68\u4ea4";
      this.customImagePath = compoundTag.contains("customImage") ? compoundTag.getString("customImage") : "";
      this.directionFlip = compoundTag.contains("directionFlip") && compoundTag.getBoolean("directionFlip");
   }

   public MyPSDTopBE(BlockPos pos, BlockState state) {
      this((BlockEntityType)BlockEntities.PSD_TOP.get(), pos, state);
   }

   protected MyPSDTopBE(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
      super(type, pos, state);
   }
}
