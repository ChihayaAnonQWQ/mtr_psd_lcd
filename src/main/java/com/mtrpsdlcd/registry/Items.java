package com.mtrpsdlcd.registry;

import com.mtrpsdlcd.Constants;
import com.mtrpsdlcd.PSDForge;
import com.mtrpsdlcd.item.ItemPSDBase;
import com.mtrpsdlcd.item.ItemPSDTopModule;
import com.mtrpsdlcd.item.MyPSDTool;
import mtr.RegistryObject;
import mtr.mappings.DeferredRegisterHolder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class Items {
   public static final DeferredRegisterHolder<Item> ITEMS = new DeferredRegisterHolder<>(PSDForge.MOD_ID, Registries.ITEM);

   public static final RegistryObject<net.minecraft.world.item.Item> PSD_LCD = new RegistryObject(() -> new BlockItem((Block)Blocks.PSD_LCD.get(), new Item.Properties()));
   public static final RegistryObject<net.minecraft.world.item.Item> PSD_PILLAR = new RegistryObject(() -> new BlockItem((Block)Blocks.PSD_PILLAR.get(), new Item.Properties()));
   public static final RegistryObject<net.minecraft.world.item.Item> PSD_GLASS_LCD14 = new RegistryObject(() -> new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_GLASS, ItemPSDBase.EnumPSDAPGType.PSD_GLASS_LCD14, new Item.Properties()));
   public static final RegistryObject<net.minecraft.world.item.Item> PSD_GLASS_LCD17 = new RegistryObject(() -> new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_GLASS, ItemPSDBase.EnumPSDAPGType.PSD_GLASS_LCD17, new Item.Properties()));
   public static final RegistryObject<net.minecraft.world.item.Item> PSD_DOOR = new RegistryObject(() -> new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_DOOR, ItemPSDBase.EnumPSDAPGType.PSD_1, new Item.Properties()));
   public static final RegistryObject<net.minecraft.world.item.Item> PSD_DOOR_2 = new RegistryObject(() -> new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_DOOR, ItemPSDBase.EnumPSDAPGType.PSD_2, new Item.Properties()));
   public static final RegistryObject<net.minecraft.world.item.Item> PSD_DOOR_LCD3 = new RegistryObject(() -> new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_DOOR, ItemPSDBase.EnumPSDAPGType.PSD_2_LCD3, new Item.Properties()));
   public static final RegistryObject<net.minecraft.world.item.Item> PSD_DOOR_LCD2 = new RegistryObject(() -> new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_DOOR, ItemPSDBase.EnumPSDAPGType.PSD_2_LCD2, new Item.Properties()));
   public static final RegistryObject<net.minecraft.world.item.Item> PSD_DOOR_LCD4 = new RegistryObject(() -> new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_DOOR, ItemPSDBase.EnumPSDAPGType.PSD_2_LCD4, new Item.Properties()));
   public static final RegistryObject<net.minecraft.world.item.Item> PSD_DOOR_LCD5 = new RegistryObject(() -> new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_DOOR, ItemPSDBase.EnumPSDAPGType.PSD_2_LCD5, new Item.Properties()));
   public static final RegistryObject<net.minecraft.world.item.Item> PSD_DOOR_LCD6 = new RegistryObject(() -> new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_DOOR, ItemPSDBase.EnumPSDAPGType.PSD_2_LCD6, new Item.Properties()));
   public static final RegistryObject<net.minecraft.world.item.Item> PSD_DOOR_LCD7 = new RegistryObject(() -> new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_DOOR, ItemPSDBase.EnumPSDAPGType.PSD_2_LCD7, new Item.Properties()));
   public static final RegistryObject<net.minecraft.world.item.Item> PSD_DOOR_LCD19 = new RegistryObject(() -> new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_DOOR, ItemPSDBase.EnumPSDAPGType.PSD_2_LCD19, new Item.Properties()));
   public static final RegistryObject<net.minecraft.world.item.Item> PSD_DOOR_LCD15 = new RegistryObject(() -> new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_DOOR, ItemPSDBase.EnumPSDAPGType.PSD_2_LCD15, new Item.Properties()));
   public static final RegistryObject<net.minecraft.world.item.Item> PSD_TOP_LCD8 = new RegistryObject(() -> new ItemPSDTopModule(Blocks.PSD_TOP_LCD8::get, true, new Item.Properties()));
   public static final RegistryObject<net.minecraft.world.item.Item> PSD_TOP_LCD9 = new RegistryObject(() -> new ItemPSDTopModule(Blocks.PSD_TOP_LCD9::get, true, new Item.Properties()));
   public static final RegistryObject<net.minecraft.world.item.Item> PSD_TOP_LCD10 = new RegistryObject(() -> new ItemPSDTopModule(Blocks.PSD_TOP_LCD10::get, true, new Item.Properties()));
   public static final RegistryObject<net.minecraft.world.item.Item> PSD_TOP_LCD11 = new RegistryObject(() -> new ItemPSDTopModule(Blocks.PSD_TOP_LCD11::get, true, new Item.Properties()));
   public static final RegistryObject<net.minecraft.world.item.Item> PSD_TOP_LCD16 = new RegistryObject(() -> new ItemPSDTopModule(Blocks.PSD_TOP_LCD16::get, true, new Item.Properties()));
   public static final RegistryObject<net.minecraft.world.item.Item> PSD_TOP_LCD12 = new RegistryObject(() -> new ItemPSDTopModule(Blocks.PSD_TOP_LCD12::get, false, new Item.Properties()));
   public static final RegistryObject<net.minecraft.world.item.Item> PSD_TOP_LCD18 = new RegistryObject(() -> new ItemPSDTopModule(Blocks.PSD_TOP_LCD18::get, false, new Item.Properties()));
   public static final RegistryObject<net.minecraft.world.item.Item> PSD_GLASS = new RegistryObject(() -> new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_GLASS, ItemPSDBase.EnumPSDAPGType.PSD_1, new Item.Properties()));
   public static final RegistryObject<net.minecraft.world.item.Item> PSD_GLASS_2 = new RegistryObject(() -> new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_GLASS, ItemPSDBase.EnumPSDAPGType.PSD_2, new Item.Properties()));
   public static final RegistryObject<net.minecraft.world.item.Item> PSD_GLASS_LCD21 = new RegistryObject(() -> new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_GLASS, ItemPSDBase.EnumPSDAPGType.PSD_GLASS_LCD21, new Item.Properties()));
   public static final RegistryObject<net.minecraft.world.item.Item> PSD_GLASS_LCD22 = new RegistryObject(() -> new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_GLASS, ItemPSDBase.EnumPSDAPGType.PSD_GLASS_LCD22, new Item.Properties()));
   public static final RegistryObject<net.minecraft.world.item.Item> PSD_TOP_LCD20 = new RegistryObject(() -> new ItemPSDTopModule(Blocks.PSD_TOP_LCD20::get, false, new Item.Properties()));
   public static final RegistryObject<net.minecraft.world.item.Item> PSD_GLASS_LCD13 = new RegistryObject(() -> new ItemPSDBase(ItemPSDBase.EnumPSDAPGItem.PSD_APG_GLASS, ItemPSDBase.EnumPSDAPGType.PSD_GLASS_LCD13, new Item.Properties()));
   public static final RegistryObject<net.minecraft.world.item.Item> PSD_TOOL = new RegistryObject(() -> new MyPSDTool(new Item.Properties()));

   private Items() {
   }

   public static void register() {
      ITEMS.register();
      register("psd_lcd", PSD_LCD);
      register("psd_pillar", PSD_PILLAR);
      register("psd_glass_lcd14", PSD_GLASS_LCD14);
      register("psd_glass_lcd_3", PSD_GLASS_LCD17);
      register("psd_door", PSD_DOOR);
      register("psd_door_2", PSD_DOOR_2);
      register("psd_door_lcd3", PSD_DOOR_LCD3);
      register("psd_door_lcd2", PSD_DOOR_LCD2);
      register("psd_door_lcd4", PSD_DOOR_LCD4);
      register("psd_door_lcd5", PSD_DOOR_LCD5);
      register("psd_door_lcd6", PSD_DOOR_LCD6);
      register("psd_door_lcd7", PSD_DOOR_LCD7);
      register("psd_door_lcd19", PSD_DOOR_LCD19);
      register("psd_door_lcd15", PSD_DOOR_LCD15);
      register("psd_top_lcd8", PSD_TOP_LCD8);
      register("psd_top_lcd9", PSD_TOP_LCD9);
      register("psd_top_lcd10", PSD_TOP_LCD10);
      register("psd_top_lcd11", PSD_TOP_LCD11);
      register("psd_top_lcd16", PSD_TOP_LCD16);
      register("psd_top_lcd12", PSD_TOP_LCD12);
      register("psd_top_lcd18", PSD_TOP_LCD18);
      register("psd_glass", PSD_GLASS);
      register("psd_glass_2", PSD_GLASS_2);
      register("psd_glass_lcd21", PSD_GLASS_LCD21);
      register("psd_glass_lcd22", PSD_GLASS_LCD22);
      register("psd_top_lcd20", PSD_TOP_LCD20);
      register("psd_glass_lcd13", PSD_GLASS_LCD13);
      register("psd_tool", PSD_TOOL);
   }

   private static void register(String id, RegistryObject<? extends net.minecraft.world.item.Item> holder) {
      ITEMS.register(id, holder::get);
   }
}
