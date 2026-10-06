package com.mtrpsdlcd.registry;

import com.mtrpsdlcd.block.PSDCustomText;
import com.mtrpsdlcd.client.PacketCustomTextClient;
import mtr.mappings.RegistryUtilitiesClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;

public class ModRegistryClient {
   private ModRegistryClient() {
   }

   public static void saveCustomText(BlockPos topPos, String text, int imageState, String imageName, byte[] imageBytes, String localImagePath) {
      Minecraft client = Minecraft.getInstance();
      if (client != null && client.level != null) {
         PSDCustomText.apply(client.level, topPos, text, imageState == 1 && localImagePath != null ? localImagePath : "");
         PacketCustomTextClient.sendUpload(topPos, text, imageState, imageName, imageBytes);
      }
   }

   /** Called while the mod is constructed on the physical client. */
   public static void register() {
      PacketCustomTextClient.registerReceiver();
   }

   /** Called on FMLClientSetupEvent, on the client thread. */
   public static void registerClientSetup() {
      registerRenderTypes();
      BlockEntityRenderers.registerClient();
   }

   private static void registerRenderTypes() {
      RegistryUtilitiesClient.registerRenderType(RenderType.cutout(), (Block)Blocks.PSD_DOOR.get());
      RegistryUtilitiesClient.registerRenderType(RenderType.cutout(), (Block)Blocks.PSD_GLASS.get());
      RegistryUtilitiesClient.registerRenderType(RenderType.cutout(), (Block)Blocks.PSD_DOOR_2.get());
      RegistryUtilitiesClient.registerRenderType(RenderType.cutout(), (Block)Blocks.PSD_GLASS_2.get());
      RegistryUtilitiesClient.registerRenderType(RenderType.cutout(), (Block)Blocks.PSD_DOOR_LCD2.get());
      RegistryUtilitiesClient.registerRenderType(RenderType.cutout(), (Block)Blocks.PSD_DOOR_LCD3.get());
      RegistryUtilitiesClient.registerRenderType(RenderType.cutout(), (Block)Blocks.PSD_DOOR_LCD4.get());
      RegistryUtilitiesClient.registerRenderType(RenderType.cutout(), (Block)Blocks.PSD_DOOR_LCD5.get());
      RegistryUtilitiesClient.registerRenderType(RenderType.cutout(), (Block)Blocks.PSD_DOOR_LCD6.get());
      RegistryUtilitiesClient.registerRenderType(RenderType.cutout(), (Block)Blocks.PSD_DOOR_LCD7.get());
      RegistryUtilitiesClient.registerRenderType(RenderType.cutout(), (Block)Blocks.PSD_GLASS_LCD13.get());
      RegistryUtilitiesClient.registerRenderType(RenderType.cutout(), (Block)Blocks.PSD_GLASS_LCD14.get());
      RegistryUtilitiesClient.registerRenderType(RenderType.cutout(), (Block)Blocks.PSD_DOOR_LCD15.get());
      RegistryUtilitiesClient.registerRenderType(RenderType.cutout(), (Block)Blocks.PSD_GLASS_LCD17.get());
      RegistryUtilitiesClient.registerRenderType(RenderType.cutout(), (Block)Blocks.PSD_GLASS_LCD21.get());
      RegistryUtilitiesClient.registerRenderType(RenderType.cutout(), (Block)Blocks.PSD_GLASS_LCD22.get());
      RegistryUtilitiesClient.registerRenderType(RenderType.cutout(), (Block)Blocks.PSD_DOOR_LCD19.get());
      RegistryUtilitiesClient.registerRenderType(RenderType.solid(), (Block)Blocks.PSD_PILLAR.get());
   }
}
