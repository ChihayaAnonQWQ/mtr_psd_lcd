package com.mtrpsdlcd.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

public final class CustomTextScreenOpener {
   private CustomTextScreenOpener() {
   }

   public static void open(BlockPos topPos, String currentText, String currentImagePath) {
      Minecraft.getInstance().setScreen(new CustomTextScreen(topPos, currentText, currentImagePath));
   }
}
