package com.mtrpsdlcd.client;

import com.mtrpsdlcd.Diag;
import com.mtrpsdlcd.block.PSDCustomText;
import com.mtrpsdlcd.registry.ModRegistryClient;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardCopyOption;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

public class CustomTextScreen extends Screen {
   private final BlockPos topPos;
   private final String initialText;
   private String currentImagePath;
   private EditBox textField;
   private volatile String pickedImagePath = null;
   private volatile String pickerError = null;
   private volatile boolean pickingImage = false;
   private volatile String saveError = null;
   private static final int MAX_IMAGE_SIDE = 1920;

   public CustomTextScreen(BlockPos topPos, String initialText, String initialImagePath) {
      super(Component.literal("\u81ea\u5b9a\u4e49\u6587\u5b57 / \u56fe\u7247"));
      this.topPos = topPos;
      this.initialText = initialText;
      this.currentImagePath = initialImagePath == null ? "" : initialImagePath;
   }

   protected void init() {
      int centerX = this.width / 2;
      int centerY = this.height / 2;
      this.textField = new EditBox(this.font, centerX - 130, centerY - 44, 260, 20, Component.literal("\u6587\u5b57"));
      this.textField.setMaxLength(64);
      this.textField.setValue(this.readInitialText());
      this.addRenderableWidget(this.textField);
      this.setFocused(this.textField);
      this.addRenderableWidget(Button.builder(Component.literal("\u9009\u62e9\u56fe\u7247"), (button) -> this.pickImageFile()).bounds(centerX - 130, centerY - 8, 175, 20).build());
      this.addRenderableWidget(Button.builder(Component.literal("\u6e05\u9664\u56fe\u7247"), (button) -> {
         this.currentImagePath = "";
         this.saveError = null;
         PSDCustomText.clearSelectedLibraryImage();
         String text = this.textField.getValue();
         ModRegistryClient.saveCustomText(this.topPos, text, 0, "", (byte[])null, "");
         Diag.log("[LCD14] \u5df2\u6e05\u9664\u81ea\u5b9a\u4e49\u56fe\u7247\uff08\u672c\u5c4f + \u670d\u52a1\u7aef\u771f\u6e90\uff09\uff1a" + this.topPos);
      }).bounds(centerX + 50, centerY - 8, 80, 20).build());
      this.addRenderableWidget(Button.builder(Component.literal("\u4fdd\u5b58"), (button) -> this.saveAndClose()).bounds(centerX - 130, centerY + 40, 125, 20).build());
      this.addRenderableWidget(Button.builder(Component.literal("\u53d6\u6d88"), (button) -> this.onClose()).bounds(centerX + 5, centerY + 40, 125, 20).build());
   }

   private String readInitialText() {
      String fromBlock = this.initialText == null ? "" : this.initialText;
      if (!fromBlock.isEmpty() && !"\u5730\u94c1\u8f68\u4ea4".equals(fromBlock)) {
         return fromBlock;
      } else {
         try {
            File file = new File(PSDCustomText.getLibraryGroupDir(), "text.txt");
            if (file.isFile()) {
               String fromFile = (new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8)).trim();
               if (!fromFile.isEmpty()) {
                  return fromFile;
               }
            }
         } catch (Throwable var4) {
         }

         return fromBlock;
      }
   }

   private void writeTextFile(String text) {
      try {
         Files.write((new File(PSDCustomText.getLibraryGroupDir(), "text.txt")).toPath(), (text == null ? "" : text).getBytes(StandardCharsets.UTF_8), new OpenOption[0]);
      } catch (Throwable var3) {
         Diag.log("[LCD14] \u5199\u5165 text.txt \u5931\u8d25: " + var3);
      }

   }

   private void saveAndClose() {
      String text = this.textField.getValue();
      String imagePath = this.currentImagePath == null ? "" : this.currentImagePath;
      int imageState = 0;
      String imageName = "";
      byte[] imageBytes = null;
      if (!imagePath.isEmpty()) {
         File file = new File(imagePath);
         if (!file.isFile()) {
            imageState = 2;
            Diag.log("[LCD14] \u4fdd\u5b58\uff1a\u672c\u673a\u6ca1\u6709\u8fd9\u4e2a\u56fe\u7247\u6587\u4ef6\uff0c\u670d\u52a1\u7aef\u56fe\u7247\u4fdd\u6301\u539f\u6837 path=" + imagePath);
         } else {
            if (file.length() > 1048576L) {
               this.saveError = "\u56fe\u7247\u592a\u5927\uff0c\u672a\u4e0a\u4f20\uff1a" + file.length() / 1024L + " KB > \u4e0a\u9650 1024 KB\uff08" + file.getName() + "\uff09\u2014\u2014 \u6362\u4e00\u5f20\u5c0f\u4e00\u70b9\u7684\u56fe\u518d\u4fdd\u5b58";
               Diag.log("[LCD14] \u4fdd\u5b58\u88ab\u62d2\uff08\u56fe\u7247\u8d85\u4e0a\u9650\uff09\uff1a" + file.length() + " > 1048576");
               return;
            }

            try {
               imageBytes = Files.readAllBytes(file.toPath());
               imageName = file.getName();
               imageState = 1;
            } catch (Throwable var8) {
               this.saveError = "\u56fe\u7247\u8bfb\u4e0d\u51fa\u6765\uff0c\u672a\u4e0a\u4f20\uff1a" + describeThrowable(var8) + "\uff08\u670d\u52a1\u7aef\u539f\u6709\u7684\u56fe\u4fdd\u6301\u4e0d\u52a8\uff09";
               Diag.log("[LCD14] \u4fdd\u5b58\u88ab\u62d2\uff08\u56fe\u7247\u8bfb\u53d6\u5931\u8d25\uff09\uff1a" + var8);
               return;
            }
         }
      }

      this.saveError = null;
      ModRegistryClient.saveCustomText(this.topPos, text, imageState, imageName, imageBytes, imagePath);
      this.writeTextFile(text);
      if (imagePath.isEmpty()) {
         PSDCustomText.clearSelectedLibraryImage();
      } else {
         PSDCustomText.setSelectedLibraryImage(imagePath);
      }

      this.onClose();
   }

   private void pickImageFile() {
      if (!this.pickingImage) {
         this.pickingImage = true;
         this.pickerError = null;
         Thread thread = new Thread(() -> {
            String result = null;
            String error = null;

            try {
               result = openAwtFileDialog();
            } catch (Throwable var6) {
               error = "\u7cfb\u7edf\u5bf9\u8bdd\u6846\u5931\u8d25\uff1a" + describeThrowable(var6) + " [headless=" + GraphicsEnvironment.isHeadless() + "]";

               try {
                  result = openSwingFileChooser();
                  error = null;
               } catch (Throwable var5) {
                  error = error + "\uff1bSwing \u515c\u5e95\u4e5f\u5931\u8d25\uff1a" + describeThrowable(var5);
               }
            }

            if (result != null && !result.isEmpty()) {
               String imported = this.importToLibrary(result);
               if (imported != null && !imported.isEmpty()) {
                  this.pickedImagePath = imported;
               }

               Diag.log("[LCD14] \u9009\u62e9\u56fe\u7247 file=" + result + " \u2192 \u5165\u5e93=" + imported);
            } else if (error != null) {
               this.pickerError = error;
               Diag.log("[LCD14] \u56fe\u7247\u9009\u62e9\u5668\u5931\u8d25\uff1a" + error);
            }

            this.pickingImage = false;
         }, "psd-lcd-image-picker");
         thread.setDaemon(true);
         thread.start();
      }
   }

   private static int[] readImageSize(File file) {
      try {
         BufferedImage image = ImageIO.read(file);
         return image == null ? null : new int[]{image.getWidth(), image.getHeight()};
      } catch (Throwable var2) {
         return null;
      }
   }

   private String importToLibrary(String sourcePath) {
      try {
         File source = new File(sourcePath);
         if (!source.isFile()) {
            return sourcePath;
         } else {
            int[] size = readImageSize(source);
            if (size == null) {
               this.pickerError = "\u56fe\u7247\u8bfb\u4e0d\u51fa\u6765\uff08\u4e0d\u662f\u652f\u6301\u7684\u56fe\u7247\u683c\u5f0f\uff1fpng/jpg/bmp/gif\uff09";
               return null;
            } else if (size[0] == size[1] && size[0] <= 1920) {
               File dir = PSDCustomText.getLibraryGroupDir();
               String safeName = source.getName().replaceAll("[^A-Za-z0-9._\\u4e00-\\u9fa5-]", "_");
               File target = new File(dir, safeName);
               if (source.getCanonicalFile().equals(target.getCanonicalFile())) {
                  return target.getAbsolutePath();
               } else {
                  Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                  Diag.log("[LCD14] \u56fe\u7247\u5df2\u5165\u5e93\uff08\u540c\u540d\u8986\u76d6\uff09\uff1a" + target.getAbsolutePath());
                  return target.getAbsolutePath();
               }
            } else {
               this.pickerError = "\u56fe\u7247\u4e0d\u7b26\u5408\u8981\u6c42\uff1a\u9700 1:1 \u6b63\u65b9\u5f62\u3001\u8fb9\u957f \u2264 1920\uff08\u8fd9\u5f20\u662f " + size[0] + "\u00d7" + size[1] + "\uff09";
               return null;
            }
         }
      } catch (Throwable var7) {
         Diag.log("[LCD14] \u56fe\u7247\u5165\u5e93\u5931\u8d25\uff08\u6539\u7528\u539f\u8def\u5f84\uff09: " + var7);
         return sourcePath;
      }
   }

   private static String describeThrowable(Throwable t) {
      StringBuilder sb = new StringBuilder();
      Throwable current = t;
      int depth = 0;

      while(current != null && depth++ < 5) {
         sb.append(current.getClass().getSimpleName());
         String message = current.getMessage();
         if (message != null && !message.isEmpty()) {
            sb.append('(').append(message).append(')');
         }

         current = current.getCause();
         if (current != null) {
            sb.append(" <- ");
         }
      }

      return sb.toString();
   }

   private static String openAwtFileDialog() throws Exception {
      String[] result = new String[1];
      EventQueue.invokeAndWait(() -> {
         final FileDialog dialog = new FileDialog((Frame)null, "\u9009\u62e9\u56fe\u7247");
         dialog.setMode(0);
         dialog.setMultipleMode(false);

         try {
            dialog.setAlwaysOnTop(true);
         } catch (Throwable var5) {
         }

         try {
            dialog.setLocationRelativeTo((java.awt.Component)null);
         } catch (Throwable var4) {
         }

         dialog.addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent event) {
               try {
                  dialog.toFront();
                  dialog.requestFocus();
                  dialog.setAlwaysOnTop(true);
               } catch (Throwable var3) {
               }

            }
         });
         dialog.setVisible(true);
         String fileName = dialog.getFile();
         if (fileName != null && !fileName.isEmpty()) {
            String directory = dialog.getDirectory();
            result[0] = (directory == null ? new File(fileName) : new File(directory, fileName)).getAbsolutePath();
         }

         dialog.dispose();
      });
      return result[0];
   }

   private static String openSwingFileChooser() throws Exception {
      String[] result = new String[1];
      SwingUtilities.invokeAndWait(() -> {
         JFileChooser chooser = new JFileChooser();
         chooser.setDialogTitle("\u9009\u62e9\u56fe\u7247");

         try {
            chooser.setFileFilter(new FileNameExtensionFilter("\u56fe\u7247\u6587\u4ef6 (png/jpg/bmp/gif)", new String[]{"png", "jpg", "jpeg", "bmp", "gif"}));
         } catch (Throwable var3) {
         }

         if (chooser.showOpenDialog((java.awt.Component)null) == 0 && chooser.getSelectedFile() != null) {
            result[0] = chooser.getSelectedFile().getAbsolutePath();
         }

      });
      return result[0];
   }

   public void tick() {
      super.tick();
      String picked = this.pickedImagePath;
      if (picked != null) {
         this.pickedImagePath = null;
         this.currentImagePath = picked;
         this.pickerError = null;
      }

   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      return this.textField != null && this.textField.keyPressed(keyCode, scanCode, modifiers) ? true : super.keyPressed(keyCode, scanCode, modifiers);
   }

   public boolean charTyped(char chr, int modifiers) {
      return this.textField != null && this.textField.charTyped(chr, modifiers) ? true : super.charTyped(chr, modifiers);
   }

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
      this.renderBackground(guiGraphics);
      guiGraphics.drawCenteredString(this.font, this.getTitle(), this.width / 2, this.height / 2 - 84, 16777215);
      String imageLabel;
      if (this.currentImagePath != null && !this.currentImagePath.isEmpty()) {
         imageLabel = "\u56fe\u7247\uff1a" + (new File(this.currentImagePath)).getName();
      } else {
         imageLabel = "\u56fe\u7247\uff1a\u65e0\uff1b\u4e5f\u53ef\u76f4\u63a5\u628a\u56fe\u4e22\u8fdb\u4e0b\u9762\u8fd9\u4e2a\u6587\u4ef6\u5939\uff08\u5219\u65e0\u9700\u9009\u56fe\uff09";
      }

      guiGraphics.drawCenteredString(this.font, imageLabel, this.width / 2, this.height / 2 - 70, 10526880);
      guiGraphics.drawCenteredString(this.font, "\u56fe\u7247\u8981\u6c42\uff1a1:1 \u6b63\u65b9\u5f62\uff08\u5bbd=\u9ad8\uff09\u3001\u8fb9\u957f \u2264 1920 \u50cf\u7d20\u3001png/jpg/bmp/gif\uff1b\u4e0a\u4f20\u4e0a\u9650 1024 KB", this.width / 2, this.height / 2 - 58, 6332671);
      guiGraphics.drawCenteredString(this.font, "\u56fe\u7247\u5e93\uff1a" + PSDCustomText.getLibraryGroupDir().getAbsolutePath(), this.width / 2, this.height / 2 + 22, 8421504);
      if (this.pickerError != null) {
         guiGraphics.drawCenteredString(this.font, "\u56fe\u7247\u9009\u62e9\u5668\uff1a" + this.pickerError, this.width / 2, this.height / 2 + 34, 16733525);
      }

      if (this.saveError != null) {
         guiGraphics.drawCenteredString(this.font, "\u4fdd\u5b58\u672a\u5b8c\u6210\uff1a" + this.saveError, this.width / 2, this.height / 2 + 64, 16733525);
      }

      super.render(guiGraphics, mouseX, mouseY, delta);
   }

   public boolean isPauseScreen() {
      return false;
   }
}
