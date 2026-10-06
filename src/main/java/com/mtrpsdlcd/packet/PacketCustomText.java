package com.mtrpsdlcd.packet;

import com.mtrpsdlcd.Constants;
import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public final class PacketCustomText {
   public static final ResourceLocation CHANNEL = Constants.id("custom_text");
   private final String text;
   private final String imageName;
   private final int totalBytes;
   private final int chunkIndex;
   private final int chunkCount;
   private final byte[] payload;

   public PacketCustomText(String text, String imageName, int totalBytes, int chunkIndex, int chunkCount, byte[] payload) {
      this.text = text == null ? "" : text;
      this.imageName = imageName == null ? "" : imageName;
      this.totalBytes = Math.max(0, totalBytes);
      this.chunkIndex = Math.max(0, chunkIndex);
      this.chunkCount = Math.max(1, chunkCount);
      this.payload = payload == null ? new byte[0] : payload;
   }

   public PacketCustomText(FriendlyByteBuf buf) {
      this(buf.readUtf(), buf.readUtf(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readByteArray());
   }

   public void write(FriendlyByteBuf buf) {
      buf.writeUtf(this.text);
      buf.writeUtf(this.imageName);
      buf.writeInt(this.totalBytes);
      buf.writeInt(this.chunkIndex);
      buf.writeInt(this.chunkCount);
      buf.writeByteArray(this.payload);
   }

   public FriendlyByteBuf toBuf() {
      FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
      this.write(buf);
      return buf;
   }

   public String getText() {
      return this.text;
   }

   public String getImageName() {
      return this.imageName;
   }

   public int getTotalBytes() {
      return this.totalBytes;
   }

   public int getChunkIndex() {
      return this.chunkIndex;
   }

   public int getChunkCount() {
      return this.chunkCount;
   }

   public byte[] getPayload() {
      return this.payload;
   }

   public static List<PacketCustomText> split(String text, String imageName, byte[] bytes) {
      byte[] data = bytes == null ? new byte[0] : bytes;
      int chunkBytes = 24576;
      int count = Math.max(1, (data.length + 24576 - 1) / 24576);
      List<PacketCustomText> packets = new ArrayList(count);

      for(int index = 0; index < count; ++index) {
         int from = index * 24576;
         int to = Math.min(data.length, from + 24576);
         byte[] slice = new byte[Math.max(0, to - from)];
         if (slice.length > 0) {
            System.arraycopy(data, from, slice, 0, slice.length);
         }

         packets.add(new PacketCustomText(text, imageName, data.length, index, count, slice));
      }

      return packets;
   }
}
