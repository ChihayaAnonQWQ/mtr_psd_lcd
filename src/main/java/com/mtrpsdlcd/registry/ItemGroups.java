package com.mtrpsdlcd.registry;

import com.mtrpsdlcd.Diag;
import com.mtrpsdlcd.PSDForge;
import mtr.mappings.DeferredRegisterHolder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public final class ItemGroups {
   public static final DeferredRegisterHolder<CreativeModeTab> TABS = new DeferredRegisterHolder<>(PSDForge.MOD_ID, Registries.CREATIVE_MODE_TAB);
   private static final int VISIBLE_COUNT = 22;
   private static final int HIDDEN_COUNT = 6;

   private ItemGroups() {
   }

   public static Item[] hidden() {
      return new Item[]{(Item)Items.PSD_GLASS.get(), (Item)Items.PSD_GLASS_2.get(), (Item)Items.PSD_GLASS_LCD13.get(), (Item)Items.PSD_GLASS_LCD21.get(), (Item)Items.PSD_GLASS_LCD22.get(), (Item)Items.PSD_TOP_LCD20.get()};
   }

   private static Item[] contents() {
      return new Item[]{(Item)Items.PSD_LCD.get(), (Item)Items.PSD_DOOR.get(), (Item)Items.PSD_DOOR_2.get(), (Item)Items.PSD_GLASS_LCD14.get(), (Item)Items.PSD_DOOR_LCD2.get(), (Item)Items.PSD_DOOR_LCD3.get(), (Item)Items.PSD_DOOR_LCD4.get(), (Item)Items.PSD_DOOR_LCD5.get(), (Item)Items.PSD_DOOR_LCD6.get(), (Item)Items.PSD_DOOR_LCD7.get(), (Item)Items.PSD_DOOR_LCD15.get(), (Item)Items.PSD_GLASS_LCD17.get(), (Item)Items.PSD_DOOR_LCD19.get(), (Item)Items.PSD_TOP_LCD8.get(), (Item)Items.PSD_TOP_LCD9.get(), (Item)Items.PSD_TOP_LCD10.get(), (Item)Items.PSD_TOP_LCD11.get(), (Item)Items.PSD_TOP_LCD12.get(), (Item)Items.PSD_TOP_LCD16.get(), (Item)Items.PSD_TOP_LCD18.get(), (Item)Items.PSD_PILLAR.get(), (Item)Items.PSD_TOOL.get()};
   }

   public static void register() {
      TABS.register();
      TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.mtr_psd_lcd.main"))
            .icon(() -> new ItemStack((ItemLike)Blocks.PSD_LCD.get()))
            .displayItems((parameters, output) -> {
               for (Item item : contents()) {
                  output.accept(new ItemStack(item));
               }
            })
            .build());
      // Do not touch the block/item suppliers here: on Forge 1.20.1 a Block may
      // only be constructed while its registry accepts writes, so every supplier
      // must stay lazy until the registry events run.
      Diag.log("ItemGroups: creative tab registered (visible items=" + VISIBLE_COUNT + ", hidden=" + HIDDEN_COUNT + ")");
   }
}
