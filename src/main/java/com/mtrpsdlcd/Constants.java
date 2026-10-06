package com.mtrpsdlcd;

import net.minecraft.resources.ResourceLocation;

public class Constants {
   public static final String MOD_ID = "mtr_psd_lcd";
   public static final String MOD_NAME = "\u5c4f\u853d\u95e8";
   public static final boolean DEBUG_LOG = false;
   public static final boolean DIAG_ENABLED = false;

   public Constants() {
   }

   public static ResourceLocation id(String id) {
      return new ResourceLocation("mtr_psd_lcd", id);
   }
}
