package com.mtrpsdlcd.registry;

import com.mtrpsdlcd.Constants;
import com.mtrpsdlcd.block.MyPSDDoor;
import com.mtrpsdlcd.block.MyPSDDoorLcd15;
import com.mtrpsdlcd.block.MyPSDDoorLcd19;
import com.mtrpsdlcd.block.MyPSDDoorLcd2;
import com.mtrpsdlcd.block.MyPSDDoorLcd3;
import com.mtrpsdlcd.block.MyPSDDoorLcd4;
import com.mtrpsdlcd.block.MyPSDDoorLcd5;
import com.mtrpsdlcd.block.MyPSDDoorLcd6;
import com.mtrpsdlcd.block.MyPSDDoorLcd7;
import com.mtrpsdlcd.block.MyPSDGlass;
import com.mtrpsdlcd.block.MyPSDGlassLcd13;
import com.mtrpsdlcd.block.MyPSDGlassLcd14;
import com.mtrpsdlcd.block.MyPSDGlassLcd17;
import com.mtrpsdlcd.block.MyPSDGlassLcd21;
import com.mtrpsdlcd.block.MyPSDGlassLcd22;
import com.mtrpsdlcd.block.MyPSDLCD;
import com.mtrpsdlcd.block.MyPSDPillar;
import com.mtrpsdlcd.block.MyPSDTop;
import com.mtrpsdlcd.block.MyPSDTopLcd10;
import com.mtrpsdlcd.block.MyPSDTopLcd11;
import com.mtrpsdlcd.block.MyPSDTopLcd12;
import com.mtrpsdlcd.block.MyPSDTopLcd13;
import com.mtrpsdlcd.block.MyPSDTopLcd14;
import com.mtrpsdlcd.block.MyPSDTopLcd15;
import com.mtrpsdlcd.block.MyPSDTopLcd16;
import com.mtrpsdlcd.block.MyPSDTopLcd17;
import com.mtrpsdlcd.block.MyPSDTopLcd18;
import com.mtrpsdlcd.block.MyPSDTopLcd19;
import com.mtrpsdlcd.block.MyPSDTopLcd2;
import com.mtrpsdlcd.block.MyPSDTopLcd20;
import com.mtrpsdlcd.block.MyPSDTopLcd21;
import com.mtrpsdlcd.block.MyPSDTopLcd22;
import com.mtrpsdlcd.block.MyPSDTopLcd3;
import com.mtrpsdlcd.block.MyPSDTopLcd4;
import com.mtrpsdlcd.block.MyPSDTopLcd5;
import com.mtrpsdlcd.block.MyPSDTopLcd6;
import com.mtrpsdlcd.block.MyPSDTopLcd7;
import com.mtrpsdlcd.block.MyPSDTopLcd8;
import com.mtrpsdlcd.block.MyPSDTopLcd9;
import com.mtrpsdlcd.PSDForge;
import mtr.RegistryObject;
import mtr.mappings.DeferredRegisterHolder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;

public final class Blocks {
   public static final DeferredRegisterHolder<Block> BLOCKS = new DeferredRegisterHolder<>(PSDForge.MOD_ID, Registries.BLOCK);

   public static final RegistryObject<net.minecraft.world.level.block.Block> PSD_LCD = new RegistryObject(MyPSDLCD::new);
   public static final RegistryObject<net.minecraft.world.level.block.Block> PSD_DOOR = new RegistryObject(() -> new MyPSDDoor(0));
   public static final RegistryObject<net.minecraft.world.level.block.Block> PSD_GLASS = new RegistryObject(() -> new MyPSDGlass(0));
   public static final RegistryObject<net.minecraft.world.level.block.Block> PSD_DOOR_2 = new RegistryObject(() -> new MyPSDDoor(1));
   public static final RegistryObject<net.minecraft.world.level.block.Block> PSD_GLASS_2 = new RegistryObject(() -> new MyPSDGlass(1));
   public static final RegistryObject<net.minecraft.world.level.block.Block> PSD_GLASS_LCD13 = new RegistryObject(MyPSDGlassLcd13::new);
   public static final RegistryObject<net.minecraft.world.level.block.Block> PSD_GLASS_LCD14 = new RegistryObject(MyPSDGlassLcd14::new);
   public static final RegistryObject<net.minecraft.world.level.block.Block> PSD_DOOR_LCD2 = new RegistryObject(MyPSDDoorLcd2::new);
   public static final RegistryObject<net.minecraft.world.level.block.Block> PSD_TOP_LCD2 = new RegistryObject(MyPSDTopLcd2::new);
   public static final RegistryObject<net.minecraft.world.level.block.Block> PSD_DOOR_LCD3 = new RegistryObject(MyPSDDoorLcd3::new);
   public static final RegistryObject<net.minecraft.world.level.block.Block> PSD_TOP_LCD3 = new RegistryObject(MyPSDTopLcd3::new);
   public static final RegistryObject<net.minecraft.world.level.block.Block> PSD_DOOR_LCD4 = new RegistryObject(MyPSDDoorLcd4::new);
   public static final RegistryObject<net.minecraft.world.level.block.Block> PSD_TOP_LCD4 = new RegistryObject(MyPSDTopLcd4::new);
   public static final RegistryObject<net.minecraft.world.level.block.Block> PSD_DOOR_LCD5 = new RegistryObject(MyPSDDoorLcd5::new);
   public static final RegistryObject<net.minecraft.world.level.block.Block> PSD_TOP_LCD5 = new RegistryObject(MyPSDTopLcd5::new);
   public static final RegistryObject<net.minecraft.world.level.block.Block> PSD_DOOR_LCD6 = new RegistryObject(MyPSDDoorLcd6::new);
   public static final RegistryObject<net.minecraft.world.level.block.Block> PSD_TOP_LCD6 = new RegistryObject(MyPSDTopLcd6::new);
   public static final RegistryObject<net.minecraft.world.level.block.Block> PSD_DOOR_LCD7 = new RegistryObject(MyPSDDoorLcd7::new);
   public static final RegistryObject<net.minecraft.world.level.block.Block> PSD_TOP_LCD7 = new RegistryObject(MyPSDTopLcd7::new);
   public static final RegistryObject<net.minecraft.world.level.block.Block> PSD_TOP_LCD8 = new RegistryObject(MyPSDTopLcd8::new);
   public static final RegistryObject<net.minecraft.world.level.block.Block> PSD_TOP_LCD9 = new RegistryObject(MyPSDTopLcd9::new);
   public static final RegistryObject<net.minecraft.world.level.block.Block> PSD_TOP_LCD10 = new RegistryObject(MyPSDTopLcd10::new);
   public static final RegistryObject<net.minecraft.world.level.block.Block> PSD_TOP_LCD11 = new RegistryObject(MyPSDTopLcd11::new);
   public static final RegistryObject<net.minecraft.world.level.block.Block> PSD_TOP_LCD12 = new RegistryObject(MyPSDTopLcd12::new);
   public static final RegistryObject<net.minecraft.world.level.block.Block> PSD_TOP = new RegistryObject(MyPSDTop::new);
   public static final RegistryObject<net.minecraft.world.level.block.Block> PSD_TOP_LCD13 = new RegistryObject(MyPSDTopLcd13::new);
   public static final RegistryObject<net.minecraft.world.level.block.Block> PSD_TOP_LCD14 = new RegistryObject(MyPSDTopLcd14::new);
   public static final RegistryObject<net.minecraft.world.level.block.Block> PSD_DOOR_LCD15 = new RegistryObject(MyPSDDoorLcd15::new);
   public static final RegistryObject<net.minecraft.world.level.block.Block> PSD_TOP_LCD15 = new RegistryObject(MyPSDTopLcd15::new);
   public static final RegistryObject<net.minecraft.world.level.block.Block> PSD_TOP_LCD16 = new RegistryObject(MyPSDTopLcd16::new);
   public static final RegistryObject<net.minecraft.world.level.block.Block> PSD_GLASS_LCD17 = new RegistryObject(MyPSDGlassLcd17::new);
   public static final RegistryObject<net.minecraft.world.level.block.Block> PSD_TOP_LCD17 = new RegistryObject(MyPSDTopLcd17::new);
   public static final RegistryObject<net.minecraft.world.level.block.Block> PSD_TOP_LCD18 = new RegistryObject(MyPSDTopLcd18::new);
   public static final RegistryObject<net.minecraft.world.level.block.Block> PSD_TOP_LCD20 = new RegistryObject(MyPSDTopLcd20::new);
   public static final RegistryObject<net.minecraft.world.level.block.Block> PSD_GLASS_LCD21 = new RegistryObject(MyPSDGlassLcd21::new);
   public static final RegistryObject<net.minecraft.world.level.block.Block> PSD_TOP_LCD21 = new RegistryObject(MyPSDTopLcd21::new);
   public static final RegistryObject<net.minecraft.world.level.block.Block> PSD_GLASS_LCD22 = new RegistryObject(MyPSDGlassLcd22::new);
   public static final RegistryObject<net.minecraft.world.level.block.Block> PSD_TOP_LCD22 = new RegistryObject(MyPSDTopLcd22::new);
   public static final RegistryObject<net.minecraft.world.level.block.Block> PSD_DOOR_LCD19 = new RegistryObject(MyPSDDoorLcd19::new);
   public static final RegistryObject<net.minecraft.world.level.block.Block> PSD_TOP_LCD19 = new RegistryObject(MyPSDTopLcd19::new);
   public static final RegistryObject<net.minecraft.world.level.block.Block> PSD_PILLAR = new RegistryObject(MyPSDPillar::new);

   private Blocks() {
   }

   public static void register() {
      BLOCKS.register();
      register("psd_lcd", PSD_LCD);
      register("psd_door", PSD_DOOR);
      register("psd_glass", PSD_GLASS);
      register("psd_door_2", PSD_DOOR_2);
      register("psd_glass_2", PSD_GLASS_2);
      register("psd_glass_lcd13", PSD_GLASS_LCD13);
      register("psd_glass_lcd14", PSD_GLASS_LCD14);
      register("psd_door_lcd2", PSD_DOOR_LCD2);
      register("psd_top_lcd2", PSD_TOP_LCD2);
      register("psd_door_lcd3", PSD_DOOR_LCD3);
      register("psd_top_lcd3", PSD_TOP_LCD3);
      register("psd_door_lcd4", PSD_DOOR_LCD4);
      register("psd_top_lcd4", PSD_TOP_LCD4);
      register("psd_door_lcd5", PSD_DOOR_LCD5);
      register("psd_top_lcd5", PSD_TOP_LCD5);
      register("psd_door_lcd6", PSD_DOOR_LCD6);
      register("psd_top_lcd6", PSD_TOP_LCD6);
      register("psd_door_lcd7", PSD_DOOR_LCD7);
      register("psd_top_lcd7", PSD_TOP_LCD7);
      register("psd_top_lcd8", PSD_TOP_LCD8);
      register("psd_top_lcd9", PSD_TOP_LCD9);
      register("psd_top_lcd10", PSD_TOP_LCD10);
      register("psd_top_lcd11", PSD_TOP_LCD11);
      register("psd_top_lcd12", PSD_TOP_LCD12);
      register("psd_top", PSD_TOP);
      register("psd_top_lcd13", PSD_TOP_LCD13);
      register("psd_top_lcd14", PSD_TOP_LCD14);
      register("psd_door_lcd15", PSD_DOOR_LCD15);
      register("psd_top_lcd15", PSD_TOP_LCD15);
      register("psd_top_lcd16", PSD_TOP_LCD16);
      register("psd_glass_lcd_3", PSD_GLASS_LCD17);
      register("psd_top_lcd_3", PSD_TOP_LCD17);
      register("psd_top_lcd18", PSD_TOP_LCD18);
      register("psd_top_lcd20", PSD_TOP_LCD20);
      register("psd_glass_lcd21", PSD_GLASS_LCD21);
      register("psd_top_lcd21", PSD_TOP_LCD21);
      register("psd_glass_lcd22", PSD_GLASS_LCD22);
      register("psd_top_lcd22", PSD_TOP_LCD22);
      register("psd_door_lcd19", PSD_DOOR_LCD19);
      register("psd_top_lcd19", PSD_TOP_LCD19);
      register("psd_pillar", PSD_PILLAR);
   }

   private static void register(String id, RegistryObject<? extends net.minecraft.world.level.block.Block> holder) {
      BLOCKS.register(id, holder::get);
   }
}
