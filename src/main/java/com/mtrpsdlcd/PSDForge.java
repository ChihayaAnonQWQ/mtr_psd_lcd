package com.mtrpsdlcd;

import com.mtrpsdlcd.registry.ModRegistry;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(PSDForge.MOD_ID)
@Mod.EventBusSubscriber(modid = PSDForge.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PSDForge {
   public static final String MOD_ID = "mtr_psd_lcd";

   public PSDForge() {
      try {
         System.setProperty("java.awt.headless", "false");
      } catch (Throwable ignored) {
      }

      // MTR 3's DeferredRegisterHolder is backed by Architectury, which resolves
      // the mod event bus by mod id; MTR's own Forge entrypoint hands its bus to
      // Architectury the same way. Must run before any register() call.
      mtr.forge.mappings.ForgeUtilities.registerModEventBus(MOD_ID, net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext.get().getModEventBus());
      this.enforceSingleVersion();
      ModRegistry.register();
      DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> PSDForgeClient::init);
   }

   /** Registry events have run by now, so this reports what actually landed. */
   @net.minecraftforge.eventbus.api.SubscribeEvent
   public static void onCommonSetup(net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent event) {
      ModRegistry.reportRegistered();
   }

   private void enforceSingleVersion() {
      try {
         Path modsDir = FMLPaths.MODSDIR.get();
         List<String> sameIdJars = new ArrayList<>();
         if (Files.isDirectory(modsDir, new LinkOption[0])) {
            for (Path jar : Files.list(modsDir).filter((p) -> p.toString().toLowerCase().endsWith(".jar")).collect(Collectors.toList())) {
               if (this.modHasOurId(jar)) {
                  sameIdJars.add(jar.getFileName().toString());
               }
            }
         }

         if (sameIdJars.size() > 1) {
            throw new RuntimeException("Detected multiple Mtr_psd_LCD (mtr_psd_lcd) versions in the mods folder. Keep only one! Found: " + String.join(", ", sameIdJars));
         }
      } catch (RuntimeException e) {
         throw e;
      } catch (Exception e) {
         System.err.println("[mtr_psd_lcd] version check skipped (cannot scan mods folder): " + e.getMessage());
      }
   }

   private boolean modHasOurId(Path jar) {
      try (ZipFile zip = new ZipFile(jar.toFile())) {
         ZipEntry entry = zip.getEntry("META-INF/mods.toml");
         if (entry == null) {
            return false;
         }

         String toml = new String(zip.getInputStream(entry).readAllBytes(), StandardCharsets.UTF_8);
         return toml.matches("(?s).*modId\\s*=\\s*\"mtr_psd_lcd\".*");
      } catch (Exception e) {
         return false;
      }
   }
}
