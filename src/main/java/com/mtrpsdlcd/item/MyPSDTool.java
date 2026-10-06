package com.mtrpsdlcd.item;

import com.mtrpsdlcd.block.PSDCustomText;
import com.mtrpsdlcd.client.CustomTextScreenOpener;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public class MyPSDTool extends Item {
   public MyPSDTool(Item.Properties settings) {
      super(settings);
   }

   public InteractionResult useOn(UseOnContext context) {
      BlockPos topPos = PSDCustomText.resolveTopPos(context.getLevel(), context.getClickedPos());
      if (topPos == null) {
         return InteractionResult.PASS;
      } else {
         if (context.getLevel().isClientSide()) {
            CustomTextScreenOpener.open(topPos, PSDCustomText.read(context.getLevel(), topPos), PSDCustomText.readImage(context.getLevel(), topPos));
         }

         return InteractionResult.SUCCESS;
      }
   }
}
