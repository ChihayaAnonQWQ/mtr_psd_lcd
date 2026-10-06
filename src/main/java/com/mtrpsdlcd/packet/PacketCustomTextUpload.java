package com.mtrpsdlcd.packet;

import com.mtrpsdlcd.block.PSDCustomText;
import com.mtrpsdlcd.server.PSDCustomTextStore;
import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public final class PacketCustomTextUpload {
   public static final ResourceLocation CHANNEL = PacketCustomText.CHANNEL;
   private static final int MAX_CHUNKS = 1024;
   private static final Map<UUID, PacketCustomTextUpload.PendingUpload> PENDING = new HashMap();
   private final int x;
   private final int y;
   private final int z;
   private final String text;
   private final int imageState;
   private final String imageName;
   private final int totalBytes;
   private final int chunkIndex;
   private final int chunkCount;
   private final byte[] payload;

   public PacketCustomTextUpload(BlockPos pos, String text, int imageState, String imageName, int totalBytes, int chunkIndex, int chunkCount, byte[] payload) {
      this(pos.getX(), pos.getY(), pos.getZ(), text, imageState, imageName, totalBytes, chunkIndex, chunkCount, payload);
   }

   public PacketCustomTextUpload(int x, int y, int z, String text, int imageState, String imageName, int totalBytes, int chunkIndex, int chunkCount, byte[] payload) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.text = text == null ? "" : text;
      this.imageState = imageState;
      this.imageName = imageName == null ? "" : imageName;
      this.totalBytes = Math.max(0, totalBytes);
      this.chunkIndex = Math.max(0, chunkIndex);
      this.chunkCount = Math.max(1, chunkCount);
      this.payload = payload == null ? new byte[0] : payload;
   }

   public PacketCustomTextUpload(FriendlyByteBuf buf) {
      this(buf.readInt(), buf.readInt(), buf.readInt(), buf.readUtf(), buf.readInt(), buf.readUtf(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readByteArray());
   }

   public void write(FriendlyByteBuf buf) {
      buf.writeInt(this.x);
      buf.writeInt(this.y);
      buf.writeInt(this.z);
      buf.writeUtf(this.text);
      buf.writeInt(this.imageState);
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

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public int getZ() {
      return this.z;
   }

   public String getText() {
      return this.text;
   }

   public int getImageState() {
      return this.imageState;
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

   public static List<PacketCustomTextUpload> split(net.minecraft.core.BlockPos pos, String text, int imageState, String imageName, byte[] bytes) {
      byte[] data = bytes == null ? new byte[0] : bytes;
      int chunkBytes = 24576;
      int count = Math.max(1, (data.length + 24576 - 1) / 24576);
      List<PacketCustomTextUpload> packets = new ArrayList(count);

      for(int index = 0; index < count; ++index) {
         int from = index * 24576;
         int to = Math.min(data.length, from + 24576);
         byte[] slice = new byte[Math.max(0, to - from)];
         if (slice.length > 0) {
            System.arraycopy(data, from, slice, 0, slice.length);
         }

         packets.add(new PacketCustomTextUpload(pos, text, imageState, imageName, data.length, index, count, slice));
      }

      return packets;
   }

   public static void registerServerReceiver() {
      mtr.Registry.registerNetworkReceiver(CHANNEL, (server, player, buf) -> {
         PacketCustomTextUpload packet = new PacketCustomTextUpload(buf);
         server.execute(() -> handleUpload(server, player, packet));
      });
   }

   private static void handleUpload(MinecraftServer server, ServerPlayer player, PacketCustomTextUpload packet) {
      if (player != null) {
         PSDCustomText.ensureServerStoreLoaded();
         if (packet.chunkCount > 1024) {
            System.out.println("[mtr_psd_lcd] PSDCustomTextUpload rejected: chunkCount=" + packet.chunkCount + " > 1024");
         } else {
            UUID id = player.getUUID();
            if (packet.chunkIndex == 0) {
               PENDING.put(id, new PacketCustomTextUpload.PendingUpload(packet.chunkCount, packet.totalBytes));
            }

            PacketCustomTextUpload.PendingUpload pending = (PacketCustomTextUpload.PendingUpload)PENDING.get(id);
            if (pending != null) {
               if (pending.accept(packet.chunkIndex, packet.payload, packet.chunkIndex * 24576)) {
                  if (pending.complete()) {
                     PENDING.remove(id);
                     commit(server, player, packet, pending.data);
                  }
               }
            }
         }
      }
   }

   private static void commit(MinecraftServer server, ServerPlayer player, PacketCustomTextUpload packet, byte[] received) {
      int imageState = packet.imageState;
      String imageName = packet.imageName;
      if (imageState == 1) {
         if (packet.totalBytes > 1048576) {
            System.out.println("[mtr_psd_lcd] PSDCustomTextUpload image rejected (" + packet.totalBytes + " bytes > 1048576); text applied, image kept");
            imageState = 2;
         } else if (received.length != packet.totalBytes) {
            System.out.println("[mtr_psd_lcd] PSDCustomTextUpload image incomplete (" + received.length + " != " + packet.totalBytes + "); text applied, image kept");
            imageState = 2;
         }
      }

      String storedName = PSDCustomTextStore.apply(packet.text, imageState, imageName, received);
      Level world = player.level();
      if (world != null) {
         PSDCustomText.apply(world, new BlockPos(packet.x, packet.y, packet.z), PSDCustomTextStore.getText(), storedName);
      }

      pushStoreToAll(server);
   }

   public static void sendStoreTo(ServerPlayer player) {
      if (player != null) {
         PSDCustomText.ensureServerStoreLoaded();
         if (PSDCustomTextStore.isPushable()) {
            sendStoreTo(player, PSDCustomTextStore.getText(), PSDCustomTextStore.getImageName(), PSDCustomTextStore.getImageBytes());
         }
      }
   }

   public static void pushStoreToAll(MinecraftServer server) {
      if (server != null && PSDCustomTextStore.isPushable()) {
         String text = PSDCustomTextStore.getText();
         String imageName = PSDCustomTextStore.getImageName();
         byte[] imageBytes = PSDCustomTextStore.getImageBytes();

         for(ServerPlayer target : server.getPlayerList().getPlayers()) {
            sendStoreTo(target, text, imageName, imageBytes);
         }

      }
   }

   private static void sendStoreTo(ServerPlayer player, String text, String imageName, byte[] imageBytes) {
      try {
         for(PacketCustomText chunk : PacketCustomText.split(text, imageName, imageBytes)) {
            mtr.Registry.sendToPlayer(player, PacketCustomText.CHANNEL, chunk.toBuf());
         }
      } catch (Throwable var6) {
         System.out.println("[mtr_psd_lcd] PSDCustomTextUpload push failed for " + (player == null ? "-" : player.getName().getString()) + ": " + var6);
      }

   }

   private static final class PendingUpload {
      private final int chunkCount;
      private final byte[] data;
      private final boolean[] seen;
      private int seenCount;

      private PendingUpload(int chunkCount, int totalBytes) {
         this.chunkCount = chunkCount;
         this.data = new byte[Math.max(0, totalBytes)];
         this.seen = new boolean[chunkCount];
      }

      private boolean accept(int index, byte[] slice, int offset) {
         if (index >= 0 && index < this.chunkCount && !this.seen[index]) {
            this.seen[index] = true;
            ++this.seenCount;
            if (slice.length > 0) {
               if (offset < 0 || offset + slice.length > this.data.length) {
                  return false;
               }

               System.arraycopy(slice, 0, this.data, offset, slice.length);
            }

            return true;
         } else {
            return false;
         }
      }

      private boolean complete() {
         return this.seenCount >= this.chunkCount;
      }
   }
}
