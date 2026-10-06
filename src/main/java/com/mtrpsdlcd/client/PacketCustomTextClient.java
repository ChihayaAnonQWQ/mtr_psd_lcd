package com.mtrpsdlcd.client;

import com.mtrpsdlcd.block.PSDCustomText;
import com.mtrpsdlcd.packet.PacketCustomText;
import com.mtrpsdlcd.packet.PacketCustomTextUpload;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import mtr.mappings.NetworkUtilities;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

public final class PacketCustomTextClient {
   private static boolean pendingActive = false;
   private static String pendingText = "";
   private static String pendingImageName = "";
   private static byte[] pendingBytes = new byte[0];
   private static boolean[] pendingSeen = new boolean[0];
   private static int pendingSeenCount = 0;
   private static final int MAX_CHUNKS = 1024;

   private PacketCustomTextClient() {
   }

   public static void sendUpload(BlockPos topPos, String text, int imageState, String imageName, byte[] imageBytes) {
      Minecraft client = Minecraft.getInstance();
      if (client != null && client.level != null && topPos != null) {
         try {
            for(PacketCustomTextUpload packet : PacketCustomTextUpload.split(topPos, text, imageState, imageName, imageBytes)) {
               NetworkUtilities.sendToServer(PacketCustomTextUpload.CHANNEL, packet.toBuf());
            }
         } catch (Throwable var8) {
            System.out.println("[mtr_psd_lcd] custom text upload failed: " + var8);
         }

      }
   }

   public static void registerReceiver() {
      NetworkUtilities.registerReceiverS2C(PacketCustomText.CHANNEL, (buf, context) -> {
         PacketCustomText packet = new PacketCustomText(buf);
         Minecraft.getInstance().execute(() -> handleStoreChunk(packet));
      });
   }

   private static void handleStoreChunk(PacketCustomText packet) {
      int chunkCount = packet.getChunkCount();
      if (chunkCount > 0 && chunkCount <= 1024) {
         int index = packet.getChunkIndex();
         if (index == 0) {
            pendingActive = true;
            pendingText = packet.getText();
            pendingImageName = packet.getImageName();
            pendingBytes = new byte[Math.max(0, packet.getTotalBytes())];
            pendingSeen = new boolean[chunkCount];
            pendingSeenCount = 0;
         }

         if (pendingActive && index >= 0 && index < pendingSeen.length && !pendingSeen[index]) {
            pendingSeen[index] = true;
            ++pendingSeenCount;
            byte[] slice = packet.getPayload();
            int offset = index * 24576;
            if (slice.length > 0 && offset >= 0 && offset + slice.length <= pendingBytes.length) {
               System.arraycopy(slice, 0, pendingBytes, offset, slice.length);
            }

            if (pendingSeenCount >= pendingSeen.length) {
               String text = pendingText;
               String imageName = pendingImageName;
               byte[] bytes = pendingBytes;
               pendingActive = false;
               pendingSeen = new boolean[0];
               pendingBytes = new byte[0];
               applyStore(text, imageName, bytes);
            }
         }
      }
   }

   private static void applyStore(String text, String imageName, byte[] bytes) {
      String localPath = "";
      if (imageName != null && !imageName.isEmpty() && bytes != null && bytes.length > 0) {
         File file = PSDCustomText.receivedImageFile(imageName);
         if (file != null) {
            try {
               Files.write(file.toPath(), bytes, new OpenOption[0]);
               localPath = file.getAbsolutePath();
            } catch (Throwable var6) {
               System.out.println("[mtr_psd_lcd] custom image write failed (" + file.getAbsolutePath() + "): " + var6);
               localPath = "";
            }
         }
      }

      PSDCustomText.setServerStore(text, imageName, localPath);
   }
}
