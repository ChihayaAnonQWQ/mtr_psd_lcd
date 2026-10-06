package com.mtrpsdlcd.registry;

import com.mtrpsdlcd.Constants;
import com.mtrpsdlcd.block.entity.MyPSDDoorBE;
import com.mtrpsdlcd.block.entity.MyPSDDoorLcd15BE;
import com.mtrpsdlcd.block.entity.MyPSDDoorLcd19BE;
import com.mtrpsdlcd.block.entity.MyPSDDoorLcd2BE;
import com.mtrpsdlcd.block.entity.MyPSDDoorLcd3BE;
import com.mtrpsdlcd.block.entity.MyPSDDoorLcd4BE;
import com.mtrpsdlcd.block.entity.MyPSDDoorLcd5BE;
import com.mtrpsdlcd.block.entity.MyPSDDoorLcd6BE;
import com.mtrpsdlcd.block.entity.MyPSDDoorLcd7BE;
import com.mtrpsdlcd.block.entity.MyPSDTopBE;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd10BE;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd11BE;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd12BE;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd13BE;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd14BE;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd15BE;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd16BE;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd17BE;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd18BE;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd19BE;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd20BE;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd21BE;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd22BE;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd2BE;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd3BE;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd4BE;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd5BE;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd6BE;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd7BE;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd8BE;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd9BE;
import mtr.RegistryObject;
import com.mtrpsdlcd.PSDForge;
import mtr.mappings.DeferredRegisterHolder;
import mtr.mappings.RegistryUtilities;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class BlockEntities {
   public static final DeferredRegisterHolder<BlockEntityType<?>> BLOCK_ENTITY_TYPES = new DeferredRegisterHolder<>(PSDForge.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

   public static final RegistryObject<net.minecraft.world.level.block.entity.BlockEntityType<MyPSDDoorBE>> PSD_DOOR = new RegistryObject(() -> RegistryUtilities.getBlockEntityType((pos, state) -> new MyPSDDoorBE((BlockEntityType)BlockEntities.PSD_DOOR.get(), pos, state), (Block)Blocks.PSD_DOOR.get()));
   public static final RegistryObject<net.minecraft.world.level.block.entity.BlockEntityType<MyPSDDoorBE>> PSD_DOOR_2 = new RegistryObject(() -> RegistryUtilities.getBlockEntityType((pos, state) -> new MyPSDDoorBE((BlockEntityType)BlockEntities.PSD_DOOR_2.get(), pos, state), (Block)Blocks.PSD_DOOR_2.get()));
   public static final RegistryObject<net.minecraft.world.level.block.entity.BlockEntityType<MyPSDDoorLcd2BE>> PSD_DOOR_LCD2 = new RegistryObject(() -> RegistryUtilities.getBlockEntityType((pos, state) -> new MyPSDDoorLcd2BE((BlockEntityType)BlockEntities.PSD_DOOR_LCD2.get(), pos, state), (Block)Blocks.PSD_DOOR_LCD2.get()));
   public static final RegistryObject<net.minecraft.world.level.block.entity.BlockEntityType<MyPSDDoorLcd3BE>> PSD_DOOR_LCD3 = new RegistryObject(() -> RegistryUtilities.getBlockEntityType((pos, state) -> new MyPSDDoorLcd3BE((BlockEntityType)BlockEntities.PSD_DOOR_LCD3.get(), pos, state), (Block)Blocks.PSD_DOOR_LCD3.get()));
   public static final RegistryObject<net.minecraft.world.level.block.entity.BlockEntityType<MyPSDDoorLcd4BE>> PSD_DOOR_LCD4 = new RegistryObject(() -> RegistryUtilities.getBlockEntityType((pos, state) -> new MyPSDDoorLcd4BE((BlockEntityType)BlockEntities.PSD_DOOR_LCD4.get(), pos, state), (Block)Blocks.PSD_DOOR_LCD4.get()));
   public static final RegistryObject<net.minecraft.world.level.block.entity.BlockEntityType<MyPSDDoorLcd5BE>> PSD_DOOR_LCD5 = new RegistryObject(() -> RegistryUtilities.getBlockEntityType((pos, state) -> new MyPSDDoorLcd5BE((BlockEntityType)BlockEntities.PSD_DOOR_LCD5.get(), pos, state), (Block)Blocks.PSD_DOOR_LCD5.get()));
   public static final RegistryObject<net.minecraft.world.level.block.entity.BlockEntityType<MyPSDDoorLcd6BE>> PSD_DOOR_LCD6 = new RegistryObject(() -> RegistryUtilities.getBlockEntityType((pos, state) -> new MyPSDDoorLcd6BE((BlockEntityType)BlockEntities.PSD_DOOR_LCD6.get(), pos, state), (Block)Blocks.PSD_DOOR_LCD6.get()));
   public static final RegistryObject<net.minecraft.world.level.block.entity.BlockEntityType<MyPSDDoorLcd7BE>> PSD_DOOR_LCD7 = new RegistryObject(() -> RegistryUtilities.getBlockEntityType((pos, state) -> new MyPSDDoorLcd7BE((BlockEntityType)BlockEntities.PSD_DOOR_LCD7.get(), pos, state), (Block)Blocks.PSD_DOOR_LCD7.get()));
   public static final RegistryObject<net.minecraft.world.level.block.entity.BlockEntityType<MyPSDTopBE>> PSD_TOP = new RegistryObject(() -> RegistryUtilities.getBlockEntityType(MyPSDTopBE::new, (Block)Blocks.PSD_TOP.get()));
   public static final RegistryObject<net.minecraft.world.level.block.entity.BlockEntityType<MyPSDTopLcd2BE>> PSD_TOP_LCD2 = new RegistryObject(() -> RegistryUtilities.getBlockEntityType(MyPSDTopLcd2BE::new, (Block)Blocks.PSD_TOP_LCD2.get()));
   public static final RegistryObject<net.minecraft.world.level.block.entity.BlockEntityType<MyPSDTopLcd3BE>> PSD_TOP_LCD3 = new RegistryObject(() -> RegistryUtilities.getBlockEntityType(MyPSDTopLcd3BE::new, (Block)Blocks.PSD_TOP_LCD3.get()));
   public static final RegistryObject<net.minecraft.world.level.block.entity.BlockEntityType<MyPSDTopLcd4BE>> PSD_TOP_LCD4 = new RegistryObject(() -> RegistryUtilities.getBlockEntityType(MyPSDTopLcd4BE::new, (Block)Blocks.PSD_TOP_LCD4.get()));
   public static final RegistryObject<net.minecraft.world.level.block.entity.BlockEntityType<MyPSDTopLcd5BE>> PSD_TOP_LCD5 = new RegistryObject(() -> RegistryUtilities.getBlockEntityType(MyPSDTopLcd5BE::new, (Block)Blocks.PSD_TOP_LCD5.get()));
   public static final RegistryObject<net.minecraft.world.level.block.entity.BlockEntityType<MyPSDTopLcd6BE>> PSD_TOP_LCD6 = new RegistryObject(() -> RegistryUtilities.getBlockEntityType(MyPSDTopLcd6BE::new, (Block)Blocks.PSD_TOP_LCD6.get()));
   public static final RegistryObject<net.minecraft.world.level.block.entity.BlockEntityType<MyPSDTopLcd7BE>> PSD_TOP_LCD7 = new RegistryObject(() -> RegistryUtilities.getBlockEntityType(MyPSDTopLcd7BE::new, (Block)Blocks.PSD_TOP_LCD7.get()));
   public static final RegistryObject<net.minecraft.world.level.block.entity.BlockEntityType<MyPSDTopLcd8BE>> PSD_TOP_LCD8 = new RegistryObject(() -> RegistryUtilities.getBlockEntityType(MyPSDTopLcd8BE::new, (Block)Blocks.PSD_TOP_LCD8.get()));
   public static final RegistryObject<net.minecraft.world.level.block.entity.BlockEntityType<MyPSDTopLcd9BE>> PSD_TOP_LCD9 = new RegistryObject(() -> RegistryUtilities.getBlockEntityType(MyPSDTopLcd9BE::new, (Block)Blocks.PSD_TOP_LCD9.get()));
   public static final RegistryObject<net.minecraft.world.level.block.entity.BlockEntityType<MyPSDTopLcd10BE>> PSD_TOP_LCD10 = new RegistryObject(() -> RegistryUtilities.getBlockEntityType(MyPSDTopLcd10BE::new, (Block)Blocks.PSD_TOP_LCD10.get()));
   public static final RegistryObject<net.minecraft.world.level.block.entity.BlockEntityType<MyPSDTopLcd11BE>> PSD_TOP_LCD11 = new RegistryObject(() -> RegistryUtilities.getBlockEntityType(MyPSDTopLcd11BE::new, (Block)Blocks.PSD_TOP_LCD11.get()));
   public static final RegistryObject<net.minecraft.world.level.block.entity.BlockEntityType<MyPSDTopLcd12BE>> PSD_TOP_LCD12 = new RegistryObject(() -> RegistryUtilities.getBlockEntityType(MyPSDTopLcd12BE::new, (Block)Blocks.PSD_TOP_LCD12.get()));
   public static final RegistryObject<net.minecraft.world.level.block.entity.BlockEntityType<MyPSDTopLcd13BE>> PSD_TOP_LCD13 = new RegistryObject(() -> RegistryUtilities.getBlockEntityType(MyPSDTopLcd13BE::new, (Block)Blocks.PSD_TOP_LCD13.get()));
   public static final RegistryObject<net.minecraft.world.level.block.entity.BlockEntityType<MyPSDTopLcd14BE>> PSD_TOP_LCD14 = new RegistryObject(() -> RegistryUtilities.getBlockEntityType(MyPSDTopLcd14BE::new, (Block)Blocks.PSD_TOP_LCD14.get()));
   public static final RegistryObject<net.minecraft.world.level.block.entity.BlockEntityType<MyPSDDoorLcd15BE>> PSD_DOOR_LCD15 = new RegistryObject(() -> RegistryUtilities.getBlockEntityType((pos, state) -> new MyPSDDoorLcd15BE((BlockEntityType)BlockEntities.PSD_DOOR_LCD15.get(), pos, state), (Block)Blocks.PSD_DOOR_LCD15.get()));
   public static final RegistryObject<net.minecraft.world.level.block.entity.BlockEntityType<MyPSDTopLcd15BE>> PSD_TOP_LCD15 = new RegistryObject(() -> RegistryUtilities.getBlockEntityType(MyPSDTopLcd15BE::new, (Block)Blocks.PSD_TOP_LCD15.get()));
   public static final RegistryObject<net.minecraft.world.level.block.entity.BlockEntityType<MyPSDTopLcd16BE>> PSD_TOP_LCD16 = new RegistryObject(() -> RegistryUtilities.getBlockEntityType(MyPSDTopLcd16BE::new, (Block)Blocks.PSD_TOP_LCD16.get()));
   public static final RegistryObject<net.minecraft.world.level.block.entity.BlockEntityType<MyPSDTopLcd17BE>> PSD_TOP_LCD17 = new RegistryObject(() -> RegistryUtilities.getBlockEntityType(MyPSDTopLcd17BE::new, (Block)Blocks.PSD_TOP_LCD17.get()));
   public static final RegistryObject<net.minecraft.world.level.block.entity.BlockEntityType<MyPSDTopLcd18BE>> PSD_TOP_LCD18 = new RegistryObject(() -> RegistryUtilities.getBlockEntityType(MyPSDTopLcd18BE::new, (Block)Blocks.PSD_TOP_LCD18.get()));
   public static final RegistryObject<net.minecraft.world.level.block.entity.BlockEntityType<MyPSDTopLcd20BE>> PSD_TOP_LCD20 = new RegistryObject(() -> RegistryUtilities.getBlockEntityType(MyPSDTopLcd20BE::new, (Block)Blocks.PSD_TOP_LCD20.get()));
   public static final RegistryObject<net.minecraft.world.level.block.entity.BlockEntityType<MyPSDTopLcd21BE>> PSD_TOP_LCD21 = new RegistryObject(() -> RegistryUtilities.getBlockEntityType(MyPSDTopLcd21BE::new, (Block)Blocks.PSD_TOP_LCD21.get()));
   public static final RegistryObject<net.minecraft.world.level.block.entity.BlockEntityType<MyPSDTopLcd22BE>> PSD_TOP_LCD22 = new RegistryObject(() -> RegistryUtilities.getBlockEntityType(MyPSDTopLcd22BE::new, (Block)Blocks.PSD_TOP_LCD22.get()));
   public static final RegistryObject<net.minecraft.world.level.block.entity.BlockEntityType<MyPSDDoorLcd19BE>> PSD_DOOR_LCD19 = new RegistryObject(() -> RegistryUtilities.getBlockEntityType((pos, state) -> new MyPSDDoorLcd19BE((BlockEntityType)BlockEntities.PSD_DOOR_LCD19.get(), pos, state), (Block)Blocks.PSD_DOOR_LCD19.get()));
   public static final RegistryObject<net.minecraft.world.level.block.entity.BlockEntityType<MyPSDTopLcd19BE>> PSD_TOP_LCD19 = new RegistryObject(() -> RegistryUtilities.getBlockEntityType(MyPSDTopLcd19BE::new, (Block)Blocks.PSD_TOP_LCD19.get()));

   private BlockEntities() {
   }

   public static void register() {
      BLOCK_ENTITY_TYPES.register();
      register("psd_door", PSD_DOOR);
      register("psd_door_2", PSD_DOOR_2);
      register("psd_door_lcd2", PSD_DOOR_LCD2);
      register("psd_door_lcd3", PSD_DOOR_LCD3);
      register("psd_door_lcd4", PSD_DOOR_LCD4);
      register("psd_door_lcd5", PSD_DOOR_LCD5);
      register("psd_door_lcd6", PSD_DOOR_LCD6);
      register("psd_door_lcd7", PSD_DOOR_LCD7);
      register("psd_top", PSD_TOP);
      register("psd_top_lcd2", PSD_TOP_LCD2);
      register("psd_top_lcd3", PSD_TOP_LCD3);
      register("psd_top_lcd4", PSD_TOP_LCD4);
      register("psd_top_lcd5", PSD_TOP_LCD5);
      register("psd_top_lcd6", PSD_TOP_LCD6);
      register("psd_top_lcd7", PSD_TOP_LCD7);
      register("psd_top_lcd8", PSD_TOP_LCD8);
      register("psd_top_lcd9", PSD_TOP_LCD9);
      register("psd_top_lcd10", PSD_TOP_LCD10);
      register("psd_top_lcd11", PSD_TOP_LCD11);
      register("psd_top_lcd12", PSD_TOP_LCD12);
      register("psd_top_lcd13", PSD_TOP_LCD13);
      register("psd_top_lcd14", PSD_TOP_LCD14);
      register("psd_door_lcd15", PSD_DOOR_LCD15);
      register("psd_top_lcd15", PSD_TOP_LCD15);
      register("psd_top_lcd16", PSD_TOP_LCD16);
      register("psd_top_lcd_3", PSD_TOP_LCD17);
      register("psd_top_lcd18", PSD_TOP_LCD18);
      register("psd_top_lcd20", PSD_TOP_LCD20);
      register("psd_top_lcd21", PSD_TOP_LCD21);
      register("psd_top_lcd22", PSD_TOP_LCD22);
      register("psd_door_lcd19", PSD_DOOR_LCD19);
      register("psd_top_lcd19", PSD_TOP_LCD19);
   }

   private static void register(String id, RegistryObject<? extends net.minecraft.world.level.block.entity.BlockEntityType<?>> holder) {
      BLOCK_ENTITY_TYPES.register(id, holder::get);
   }
}
