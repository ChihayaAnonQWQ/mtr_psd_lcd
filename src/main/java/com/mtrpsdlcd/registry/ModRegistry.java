package com.mtrpsdlcd.registry;

import com.mtrpsdlcd.Diag;
import com.mtrpsdlcd.block.PSDCustomText;
import com.mtrpsdlcd.packet.PacketCustomTextUpload;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class ModRegistry {
   private ModRegistry() {
   }

   public static void register() {
      Blocks.register();
      Diag.log("ModRegistry: blocks registered");
      BlockEntities.register();
      Diag.log("ModRegistry: block entity types registered");
      Items.register();
      Diag.log("ModRegistry: items registered");
      ItemGroups.register();
      PacketCustomTextUpload.registerServerReceiver();
      Diag.log("ModRegistry: server receiver for the custom text/image upload packet registered, channel=mtr_psd_lcd:custom_text");
      if (FMLEnvironment.dist.isDedicatedServer()) {
         PSDCustomText.ensureServerStoreLoaded();
      }

      mtr.Registry.registerPlayerJoinEvent(PacketCustomTextUpload::sendStoreTo);
      Diag.log("ModRegistry: join push source for the custom text/image store registered, channel=mtr_psd_lcd:custom_text");
   }

   public static void reportRegistered() {
      Diag.log("ModRegistry: entries owned by this mod: blocks=" + countIds(BuiltInRegistries.BLOCK) + " block_entity_types=" + countIds(BuiltInRegistries.BLOCK_ENTITY_TYPE) + " items=" + countIds(BuiltInRegistries.ITEM));
   }

   private static int countIds(Registry<?> registry) {
      int count = 0;

      for (ResourceLocation id : registry.keySet()) {
         if ("mtr_psd_lcd".equals(id.getNamespace())) {
            ++count;
         }
      }

      return count;
   }
}
