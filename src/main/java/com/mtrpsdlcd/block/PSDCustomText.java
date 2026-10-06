package com.mtrpsdlcd.block;

import com.mtrpsdlcd.block.entity.MyPSDTopBE;
import com.mtrpsdlcd.server.PSDCustomTextStore;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import mtr.block.BlockPSDGlass;
import mtr.block.BlockPSDTop;
import mtr.block.IBlock;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public final class PSDCustomText {
   public static final String DEFAULT_TEXT = "\u5730\u94c1\u8f68\u4ea4";
   public static final String LIBRARY_SUB_DIR = "1";
   public static final String SELECTED_NONE = "@none";
   private static long libraryScanTime = 0L;
   private static String selectedImage = "";
   private static String libraryNewestImage = "";
   private static long libraryNewestTime = 0L;
   private static boolean selectedCleared = false;
   private static final Map<String, String> IMAGE_VERSION = new HashMap();
   private static final Map<String, Long> IMAGE_VERSION_TIME = new HashMap();
   private static volatile boolean serverStoreValid = false;
   private static volatile String serverStoreText = "";
   private static volatile String serverStoreImageName = "";
   private static volatile String serverStoreImagePath = "";

   public static File getLibraryGroupDir() {
      File dir = new File(new File(FMLPaths.GAMEDIR.get().toFile(), "psd_lcd_images"), "1");
      if (!dir.isDirectory()) {
         dir.mkdirs();
      }

      return dir;
   }

   private static synchronized void scanLibrary() {
      long now = System.currentTimeMillis();
      if (now - libraryScanTime >= 3000L) {
         libraryScanTime = now;
         selectedImage = "";
         libraryNewestImage = "";
         libraryNewestTime = 0L;
         selectedCleared = false;
         long markerTime = 0L;
         boolean markerIsNone = false;

         try {
            File marker = new File(getLibraryGroupDir(), "selected.txt");
            if (marker.isFile()) {
               markerTime = marker.lastModified();
               String name = (new String(Files.readAllBytes(marker.toPath()), StandardCharsets.UTF_8)).trim();
               if (!"@none".equalsIgnoreCase(name) && !name.isEmpty()) {
                  File image = new File(name);
                  File resolved = image.isAbsolute() ? image : new File(getLibraryGroupDir(), name);
                  if (resolved.isFile()) {
                     selectedImage = resolved.getAbsolutePath();
                  }
               } else {
                  markerIsNone = true;
               }
            }

            File[] files = getLibraryGroupDir().listFiles();
            if (files != null) {
               for(File file : files) {
                  String name = file.getName().toLowerCase(Locale.ROOT);
                  if (file.isFile() && (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".bmp") || name.endsWith(".gif")) && file.lastModified() >= libraryNewestTime) {
                     libraryNewestTime = file.lastModified();
                     libraryNewestImage = file.getAbsolutePath();
                  }
               }
            }
         } catch (Throwable var12) {
         }

         selectedCleared = markerIsNone && (libraryNewestImage.isEmpty() || libraryNewestTime <= markerTime);
         if (markerIsNone && !selectedCleared) {
            selectedImage = libraryNewestImage;
         }

      }
   }

   public static String getSelectedLibraryImage() {
      scanLibrary();
      return selectedImage;
   }

   public static boolean isLibrarySelectionCleared() {
      scanLibrary();
      return selectedCleared;
   }

   public static void setSelectedLibraryImage(String path) {
      try {
         String name = path != null && !path.isEmpty() ? (new File(path)).getName() : "";
         Files.write((new File(getLibraryGroupDir(), "selected.txt")).toPath(), name.getBytes(StandardCharsets.UTF_8), new OpenOption[0]);
         libraryScanTime = 0L;
      } catch (Throwable var2) {
      }

   }

   public static void clearSelectedLibraryImage() {
      try {
         Files.write((new File(getLibraryGroupDir(), "selected.txt")).toPath(), "@none".getBytes(StandardCharsets.UTF_8), new OpenOption[0]);
         libraryScanTime = 0L;
      } catch (Throwable var1) {
      }

   }

   public static String findFirstLibraryImage() {
      scanLibrary();
      return libraryNewestImage;
   }

   public static String imageVersion(String path) {
      if (path != null && !path.isEmpty()) {
         long now = System.currentTimeMillis();
         Long last = (Long)IMAGE_VERSION_TIME.get(path);
         if (last != null && now - last < 3000L) {
            String cached = (String)IMAGE_VERSION.get(path);
            if (cached != null) {
               return cached;
            }
         }

         String version = "";

         try {
            File file = new File(path);
            if (file.isFile()) {
               version = file.lastModified() + ":" + file.length();
            }
         } catch (Throwable var6) {
         }

         if (IMAGE_VERSION.size() > 64) {
            IMAGE_VERSION.clear();
            IMAGE_VERSION_TIME.clear();
         }

         IMAGE_VERSION.put(path, version);
         IMAGE_VERSION_TIME.put(path, now);
         return version;
      } else {
         return "";
      }
   }

   public static boolean readDirectionFlip(Level world, BlockPos pos) {
      BlockPos topPos = resolveTopPos(world, pos);
      if (topPos == null) {
         return false;
      } else {
         BlockEntity blockEntity = world.getBlockEntity(topPos);
         return blockEntity instanceof MyPSDTopBE && ((MyPSDTopBE)blockEntity).isDirectionFlip();
      }
   }

   public static void applyDirectionFlip(Level world, BlockPos topPos, boolean flip) {
      writeFlip(world, topPos, flip);
      BlockState state = world.getBlockState(topPos);
      Direction facing = (Direction)IBlock.getStatePropertySafe(state, BlockPSDTop.FACING);

      for(int dir = 0; dir < 2; ++dir) {
         Direction direction = dir == 0 ? facing.getClockWise() : facing.getCounterClockWise();

         for(BlockPos pos = topPos.relative(direction); isFamily(world.getBlockState(pos).getBlock()); pos = pos.relative(direction)) {
            writeFlip(world, pos, flip);
         }
      }

   }

   private static void writeFlip(Level world, BlockPos pos, boolean flip) {
      BlockEntity blockEntity = world.getBlockEntity(pos);
      if (blockEntity instanceof MyPSDTopBE) {
         ((MyPSDTopBE)blockEntity).setDirectionFlip(flip);
      }

   }

   private PSDCustomText() {
   }

   public static boolean isFamily(Block block) {
      return block instanceof MyPSDTopLcd14;
   }

   public static boolean hasServerStore() {
      return serverStoreValid;
   }

   public static String serverStoreTextValue() {
      String value = serverStoreText;
      return value != null && !value.isEmpty() ? value : "\u5730\u94c1\u8f68\u4ea4";
   }

   public static String serverStoreImagePathValue() {
      String value = serverStoreImagePath;
      return value == null ? "" : value;
   }

   public static String serverStoreImageNameValue() {
      String value = serverStoreImageName;
      return value == null ? "" : value;
   }

   public static void setServerStore(String text, String imageName, String localImagePath) {
      serverStoreText = text == null ? "" : text;
      serverStoreImageName = imageName == null ? "" : imageName;
      serverStoreImagePath = localImagePath == null ? "" : localImagePath;
      serverStoreValid = true;
   }

   public static File serverStoreDir() {
      return new File(FMLPaths.GAMEDIR.get().toFile(), "psd_lcd_server");
   }

   public static void ensureServerStoreLoaded() {
      PSDCustomTextStore.ensureInit(serverStoreDir());
   }

   public static File receivedImageFile(String name) {
      String safe = PSDCustomTextStore.sanitiseName(name);
      if (safe.isEmpty()) {
         return null;
      } else {
         File dir = new File(FMLPaths.GAMEDIR.get().toFile(), "psd_lcd_images");
         if (!dir.isDirectory()) {
            dir.mkdirs();
         }

         return new File(dir, safe);
      }
   }

   public static BlockPos resolveTopPos(Level world, BlockPos pos) {
      BlockState state = world.getBlockState(pos);
      if (isFamily(state.getBlock())) {
         return pos;
      } else {
         if (state.getBlock() instanceof BlockPSDGlass) {
            DoubleBlockHalf half = (DoubleBlockHalf)IBlock.getStatePropertySafe(state, IBlock.HALF);
            BlockPos topPos = half == DoubleBlockHalf.LOWER ? pos.above(2) : pos.above(1);
            if (isFamily(world.getBlockState(topPos).getBlock())) {
               return topPos;
            }
         }

         return null;
      }
   }

   public static String read(Level world, BlockPos pos) {
      if (serverStoreValid) {
         return serverStoreTextValue();
      } else {
         BlockPos topPos = resolveTopPos(world, pos);
         if (topPos == null) {
            return "\u5730\u94c1\u8f68\u4ea4";
         } else {
            BlockEntity blockEntity = world.getBlockEntity(topPos);
            return blockEntity instanceof MyPSDTopBE ? ((MyPSDTopBE)blockEntity).getCustomText() : "\u5730\u94c1\u8f68\u4ea4";
         }
      }
   }

   public static String readImage(Level world, BlockPos pos) {
      if (serverStoreValid) {
         return serverStoreImagePathValue();
      } else {
         BlockPos topPos = resolveTopPos(world, pos);
         if (topPos == null) {
            return "";
         } else {
            BlockEntity blockEntity = world.getBlockEntity(topPos);
            return blockEntity instanceof MyPSDTopBE ? ((MyPSDTopBE)blockEntity).getCustomImagePath() : "";
         }
      }
   }

   public static void apply(Level world, BlockPos topPos, String text, String imagePath) {
      writeOne(world, topPos, text, imagePath);
      BlockState state = world.getBlockState(topPos);
      Direction facing = (Direction)IBlock.getStatePropertySafe(state, BlockPSDTop.FACING);

      for(int dir = 0; dir < 2; ++dir) {
         Direction direction = dir == 0 ? facing.getClockWise() : facing.getCounterClockWise();

         for(BlockPos pos = topPos.relative(direction); isFamily(world.getBlockState(pos).getBlock()); pos = pos.relative(direction)) {
            writeOne(world, pos, text, imagePath);
         }
      }

   }

   private static void writeOne(Level world, BlockPos pos, String text, String imagePath) {
      BlockEntity blockEntity = world.getBlockEntity(pos);
      if (blockEntity instanceof MyPSDTopBE) {
         ((MyPSDTopBE)blockEntity).setCustomText(text);
         ((MyPSDTopBE)blockEntity).setCustomImagePath(imagePath);
      }

   }

   public static void apply(Level world, BlockPos topPos, String text) {
      writeOne(world, topPos, text);
      BlockState state = world.getBlockState(topPos);
      Direction facing = (Direction)IBlock.getStatePropertySafe(state, BlockPSDTop.FACING);

      for(int dir = 0; dir < 2; ++dir) {
         Direction direction = dir == 0 ? facing.getClockWise() : facing.getCounterClockWise();

         for(BlockPos pos = topPos.relative(direction); isFamily(world.getBlockState(pos).getBlock()); pos = pos.relative(direction)) {
            writeOne(world, pos, text);
         }
      }

   }

   private static void writeOne(Level world, BlockPos pos, String text) {
      BlockEntity blockEntity = world.getBlockEntity(pos);
      if (blockEntity instanceof MyPSDTopBE) {
         ((MyPSDTopBE)blockEntity).setCustomText(text);
      }

   }
}
