package com.mtrpsdlcd.registry;

import com.mtrpsdlcd.render.RenderPSDTop;
import mtr.mappings.RegistryUtilitiesClient;
import mtr.render.RenderPSDAPGDoor;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class BlockEntityRenderers {
   private BlockEntityRenderers() {
   }

   public static void registerClient() {
      RegistryUtilitiesClient.registerTileEntityRenderer((BlockEntityType)BlockEntities.PSD_DOOR.get(), (context) -> new RenderPSDAPGDoor(context, 0));
      RegistryUtilitiesClient.registerTileEntityRenderer((BlockEntityType)BlockEntities.PSD_DOOR_2.get(), (context) -> new RenderPSDAPGDoor(context, 1));
      RegistryUtilitiesClient.registerTileEntityRenderer((BlockEntityType)BlockEntities.PSD_DOOR_LCD2.get(), (context) -> new RenderPSDAPGDoor(context, 1));
      RegistryUtilitiesClient.registerTileEntityRenderer((BlockEntityType)BlockEntities.PSD_DOOR_LCD15.get(), (context) -> new RenderPSDAPGDoor(context, 1));
      RegistryUtilitiesClient.registerTileEntityRenderer((BlockEntityType)BlockEntities.PSD_DOOR_LCD3.get(), (context) -> new RenderPSDAPGDoor(context, 0));
      RegistryUtilitiesClient.registerTileEntityRenderer((BlockEntityType)BlockEntities.PSD_DOOR_LCD4.get(), (context) -> new RenderPSDAPGDoor(context, 0));
      RegistryUtilitiesClient.registerTileEntityRenderer((BlockEntityType)BlockEntities.PSD_DOOR_LCD5.get(), (context) -> new RenderPSDAPGDoor(context, 1));
      RegistryUtilitiesClient.registerTileEntityRenderer((BlockEntityType)BlockEntities.PSD_DOOR_LCD6.get(), (context) -> new RenderPSDAPGDoor(context, 0));
      RegistryUtilitiesClient.registerTileEntityRenderer((BlockEntityType)BlockEntities.PSD_DOOR_LCD7.get(), (context) -> new RenderPSDAPGDoor(context, 1));
      RegistryUtilitiesClient.registerTileEntityRenderer((BlockEntityType)BlockEntities.PSD_DOOR_LCD19.get(), (context) -> new RenderPSDAPGDoor(context, 0));
      RegistryUtilitiesClient.registerTileEntityRenderer((BlockEntityType)BlockEntities.PSD_TOP.get(), (context) -> new RenderPSDTop(context));
      RegistryUtilitiesClient.registerTileEntityRenderer((BlockEntityType)BlockEntities.PSD_TOP_LCD2.get(), (context) -> new RenderPSDTop(context));
      RegistryUtilitiesClient.registerTileEntityRenderer((BlockEntityType)BlockEntities.PSD_TOP_LCD3.get(), (context) -> new RenderPSDTop(context));
      RegistryUtilitiesClient.registerTileEntityRenderer((BlockEntityType)BlockEntities.PSD_TOP_LCD4.get(), (context) -> new RenderPSDTop(context));
      RegistryUtilitiesClient.registerTileEntityRenderer((BlockEntityType)BlockEntities.PSD_TOP_LCD5.get(), (context) -> new RenderPSDTop(context));
      RegistryUtilitiesClient.registerTileEntityRenderer((BlockEntityType)BlockEntities.PSD_TOP_LCD6.get(), (context) -> new RenderPSDTop(context));
      RegistryUtilitiesClient.registerTileEntityRenderer((BlockEntityType)BlockEntities.PSD_TOP_LCD7.get(), (context) -> new RenderPSDTop(context));
      RegistryUtilitiesClient.registerTileEntityRenderer((BlockEntityType)BlockEntities.PSD_TOP_LCD8.get(), (context) -> new RenderPSDTop(context));
      RegistryUtilitiesClient.registerTileEntityRenderer((BlockEntityType)BlockEntities.PSD_TOP_LCD9.get(), (context) -> new RenderPSDTop(context));
      RegistryUtilitiesClient.registerTileEntityRenderer((BlockEntityType)BlockEntities.PSD_TOP_LCD10.get(), (context) -> new RenderPSDTop(context));
      RegistryUtilitiesClient.registerTileEntityRenderer((BlockEntityType)BlockEntities.PSD_TOP_LCD11.get(), (context) -> new RenderPSDTop(context));
      RegistryUtilitiesClient.registerTileEntityRenderer((BlockEntityType)BlockEntities.PSD_TOP_LCD12.get(), (context) -> new RenderPSDTop(context));
      RegistryUtilitiesClient.registerTileEntityRenderer((BlockEntityType)BlockEntities.PSD_TOP_LCD13.get(), (context) -> new RenderPSDTop(context));
      RegistryUtilitiesClient.registerTileEntityRenderer((BlockEntityType)BlockEntities.PSD_TOP_LCD14.get(), (context) -> new RenderPSDTop(context));
      RegistryUtilitiesClient.registerTileEntityRenderer((BlockEntityType)BlockEntities.PSD_TOP_LCD15.get(), (context) -> new RenderPSDTop(context));
      RegistryUtilitiesClient.registerTileEntityRenderer((BlockEntityType)BlockEntities.PSD_TOP_LCD16.get(), (context) -> new RenderPSDTop(context));
      RegistryUtilitiesClient.registerTileEntityRenderer((BlockEntityType)BlockEntities.PSD_TOP_LCD17.get(), (context) -> new RenderPSDTop(context));
      RegistryUtilitiesClient.registerTileEntityRenderer((BlockEntityType)BlockEntities.PSD_TOP_LCD18.get(), (context) -> new RenderPSDTop(context));
      RegistryUtilitiesClient.registerTileEntityRenderer((BlockEntityType)BlockEntities.PSD_TOP_LCD19.get(), (context) -> new RenderPSDTop(context));
      RegistryUtilitiesClient.registerTileEntityRenderer((BlockEntityType)BlockEntities.PSD_TOP_LCD20.get(), (context) -> new RenderPSDTop(context));
      RegistryUtilitiesClient.registerTileEntityRenderer((BlockEntityType)BlockEntities.PSD_TOP_LCD21.get(), (context) -> new RenderPSDTop(context));
      RegistryUtilitiesClient.registerTileEntityRenderer((BlockEntityType)BlockEntities.PSD_TOP_LCD22.get(), (context) -> new RenderPSDTop(context));
   }
}
