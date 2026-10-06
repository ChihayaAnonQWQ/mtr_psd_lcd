package com.mtrpsdlcd;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public final class Diag {
   /** On in dev runs and with -Dmtrpsdlcd.debug=true; silent in production. */
   private static final boolean VERBOSE = Boolean.getBoolean("mtrpsdlcd.debug") || !net.minecraftforge.fml.loading.FMLLoader.isProduction();

   private Diag() {
   }

   public static void log(String message) {
      if (VERBOSE) {
         System.out.println("[mtr_psd_lcd] " + message);
      }
   }

   public static void breakLog(BlockState state, BlockPos pos, Player player) {
      try {
         String id = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
         String caller = player == null ? "null(no-player)" : (player.isCreative() ? "creative" : "survival");
         log("[BREAK] block=" + id + " pos=" + pos.getX() + "," + pos.getY() + "," + pos.getZ() + " half=" + property(state, "half") + " side=" + property(state, "side") + " persistent=" + property(state, "persistent") + " caller=" + caller);
      } catch (Throwable var5) {
      }

   }

   private static String property(BlockState state, String name) {
      try {
         for(Property p : state.getProperties()) {
            if (name.equals(p.getName())) {
               Object value = state.getValue(p);
               return String.valueOf(value);
            }
         }
      } catch (Throwable var5) {
      }

      return "-";
   }
}
