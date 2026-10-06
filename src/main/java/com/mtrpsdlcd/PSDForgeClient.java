package com.mtrpsdlcd;

import com.mtrpsdlcd.registry.ModRegistryClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = PSDForge.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class PSDForgeClient {
   private PSDForgeClient() {
   }

   /** Runs while the mod is being constructed on the physical client. */
   public static void init() {
      try {
         System.setProperty("java.awt.headless", "false");
      } catch (Throwable ignored) {
      }

      ModRegistryClient.register();
   }

   /** Render layers and block entity renderers are registered during client setup. */
   @SubscribeEvent
   public static void onClientSetup(FMLClientSetupEvent event) {
      event.enqueueWork(ModRegistryClient::registerClientSetup);
   }
}
