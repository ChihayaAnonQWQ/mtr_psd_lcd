package com.mtrpsdlcd.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public final class PSDCustomTextStore {
   public static final int MAX_IMAGE_BYTES = 1048576;
   public static final int CHUNK_BYTES = 24576;
   public static final int IMAGE_STATE_CLEAR = 0;
   public static final int IMAGE_STATE_SET = 1;
   public static final int IMAGE_STATE_KEEP = 2;
   public static final String DIR_NAME = "psd_lcd_server";
   public static final String FILE_NAME = "global.dat";
   private static final int FORMAT_VERSION = 1;
   private static final byte[] EMPTY = new byte[0];
   private static final int MAX_NAME_LENGTH = 128;
   private static File directory;
   private static boolean initialised = false;
   private static boolean wasSet = false;
   private static String text = "";
   private static String imageName = "";
   private static byte[] imageBytes = EMPTY;

   private PSDCustomTextStore() {
   }

   public static synchronized void ensureInit(File dir) {
      if (!initialised) {
         directory = dir;
         initialised = true;
         load();
      }
   }

   public static synchronized void initForVerification(File dir) {
      directory = dir;
      initialised = true;
      load();
   }

   private static void load() {
      wasSet = false;
      text = "";
      imageName = "";
      imageBytes = EMPTY;

      try {
         if (directory == null) {
            return;
         }

         if (!directory.isDirectory()) {
            directory.mkdirs();
         }

         File file = new File(directory, "global.dat");
         if (!file.isFile()) {
            save();
            logState("created-empty");
            return;
         }

         DataInputStream in = new DataInputStream(new FileInputStream(file));

         label61: {
            try {
               int version = in.readInt();
               if (version == 1) {
                  wasSet = in.readBoolean();
                  text = readString(in);
                  imageName = readString(in);
                  int length = in.readInt();
                  imageBytes = length <= 0 ? EMPTY : readBytes(in, length);
                  break label61;
               }

               logState("unsupported-version-" + version);
            } catch (Throwable var5) {
               try {
                  in.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }

               throw var5;
            }

            in.close();
            return;
         }

         in.close();
         logState("loaded");
      } catch (Throwable var6) {
         wasSet = false;
         text = "";
         imageName = "";
         imageBytes = EMPTY;
         System.out.println("[mtr_psd_lcd] PSDCustomTextStore load failed: " + var6);
      }

   }

   private static void save() {
      try {
         if (directory == null) {
            return;
         }

         if (!directory.isDirectory()) {
            directory.mkdirs();
         }

         File file = new File(directory, "global.dat");
         File temp = new File(directory, "global.dat.tmp");
         DataOutputStream out = new DataOutputStream(new FileOutputStream(temp));

         try {
            out.writeInt(1);
            out.writeBoolean(wasSet);
            writeString(out, text);
            writeString(out, imageName);
            out.writeInt(imageBytes.length);
            out.write(imageBytes);
            out.flush();
         } catch (Throwable var6) {
            try {
               out.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }

            throw var6;
         }

         out.close();
         Files.move(temp.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
      } catch (Throwable var7) {
         System.out.println("[mtr_psd_lcd] PSDCustomTextStore save failed: " + var7);
      }

   }

   private static void logState(String phase) {
      try {
         System.out.println("[mtr_psd_lcd] PSDCustomTextStore " + phase + " dir=" + (directory == null ? "-" : directory.getAbsolutePath()) + " wasSet=" + wasSet + " text=\"" + text + "\" image=" + (imageName.isEmpty() ? "-" : imageName) + " bytes=" + imageBytes.length);
      } catch (Throwable var2) {
      }

   }

   public static synchronized boolean wasSet() {
      return wasSet;
   }

   public static synchronized String getText() {
      return text;
   }

   public static synchronized String getImageName() {
      return imageName;
   }

   public static synchronized byte[] getImageBytes() {
      return imageBytes.length == 0 ? EMPTY : (byte[])imageBytes.clone();
   }

   public static synchronized boolean isPushable() {
      return wasSet;
   }

   public static synchronized String apply(String newText, int imageState, String newImageName, byte[] newImageBytes) {
      wasSet = true;
      text = newText == null ? "" : newText;
      if (imageState == 0) {
         imageName = "";
         imageBytes = EMPTY;
      } else if (imageState == 1) {
         String name = sanitiseName(newImageName);
         byte[] bytes = newImageBytes == null ? EMPTY : newImageBytes;
         if (!name.isEmpty() && bytes.length != 0) {
            if (bytes.length > 1048576) {
               System.out.println("[mtr_psd_lcd] PSDCustomTextStore rejected image (" + bytes.length + " bytes > 1048576); image kept as " + (imageName.isEmpty() ? "-" : imageName));
            } else {
               imageName = name;
               imageBytes = (byte[])bytes.clone();
            }
         } else {
            imageName = "";
            imageBytes = EMPTY;
         }
      }

      save();
      logState("saved");
      return imageName;
   }

   public static String sanitiseName(String raw) {
      if (raw == null) {
         return "";
      } else {
         String name = raw.replace('\\', '/');
         int slash = name.lastIndexOf(47);
         if (slash >= 0) {
            name = name.substring(slash + 1);
         }

         for(name = name.replaceAll("[^A-Za-z0-9._\\u4e00-\\u9fa5-]", "_"); name.startsWith("."); name = name.substring(1)) {
         }

         if (name.length() > 128) {
            name = name.substring(0, 128);
         }

         return name;
      }
   }

   private static void writeString(DataOutputStream out, String value) throws Exception {
      byte[] bytes = (value == null ? "" : value).getBytes(StandardCharsets.UTF_8);
      out.writeInt(bytes.length);
      out.write(bytes);
   }

   private static String readString(DataInputStream in) throws Exception {
      int length = in.readInt();
      return length <= 0 ? "" : new String(readBytes(in, length), StandardCharsets.UTF_8);
   }

   private static byte[] readBytes(DataInputStream in, int length) throws Exception {
      byte[] bytes = new byte[length];
      in.readFully(bytes);
      return bytes;
   }
}
