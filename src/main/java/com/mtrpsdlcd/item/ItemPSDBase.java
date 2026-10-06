package com.mtrpsdlcd.item;

import com.mtrpsdlcd.registry.Blocks;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mtr.block.BlockPSDTop;
import mtr.block.IBlock;
import mtr.block.IBlock.EnumSide;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class ItemPSDBase extends Item implements IBlock {
   private final ItemPSDBase.EnumPSDAPGItem item;
   private final ItemPSDBase.EnumPSDAPGType type;

   public ItemPSDBase(ItemPSDBase.EnumPSDAPGItem item, ItemPSDBase.EnumPSDAPGType type, Item.Properties itemSettings) {
      super(itemSettings);
      this.item = item;
      this.type = type;
   }

   @Nonnull
   public InteractionResult useOn(UseOnContext context) {
      int horizontalBlocks = this.item.isDoor ? 2 : 1;
      if (blocksNotReplaceable(context, horizontalBlocks, 3, this.getBlockStateFromItem().getBlock())) {
         return InteractionResult.FAIL;
      } else {
         Level world = context.getLevel();
         Direction playerFacing = context.getHorizontalDirection();
         BlockPos pos = context.getClickedPos().relative(context.getClickedFace());

         for(int x = 0; x < horizontalBlocks; ++x) {
            BlockPos newPos = pos.relative(playerFacing.getClockWise(), x);

            for(int y = 0; y < 2; ++y) {
               BlockState state = (BlockState)((BlockState)this.getBlockStateFromItem().setValue(HorizontalDirectionalBlock.FACING, playerFacing)).setValue(IBlock.HALF, y == 1 ? DoubleBlockHalf.UPPER : DoubleBlockHalf.LOWER);
               if (this.item.isDoor) {
                  BlockState neighborState = (BlockState)state.setValue(IBlock.SIDE, x == 0 ? EnumSide.LEFT : EnumSide.RIGHT);
                  world.setBlockAndUpdate(newPos.above(y), neighborState);
               } else {
                  world.setBlockAndUpdate(newPos.above(y), (BlockState)state.setValue(IBlock.SIDE_EXTENDED, EnumSide.SINGLE));
               }
            }

            if (this.type.isPSD) {
               BlockPos topPos = newPos.above(2);
               if (this.item.isDoor) {
                  Block topBlock;
                  switch (this.type) {
                     case PSD_2_LCD2:
                        topBlock = (Block)Blocks.PSD_TOP_LCD2.get();
                        break;
                     case PSD_2_LCD3:
                        topBlock = (Block)Blocks.PSD_TOP_LCD3.get();
                        break;
                     case PSD_2_LCD4:
                        topBlock = (Block)Blocks.PSD_TOP_LCD4.get();
                        break;
                     case PSD_2_LCD5:
                        topBlock = (Block)Blocks.PSD_TOP_LCD5.get();
                        break;
                     case PSD_2_LCD6:
                        topBlock = (Block)Blocks.PSD_TOP_LCD6.get();
                        break;
                     case PSD_2_LCD7:
                        topBlock = (Block)Blocks.PSD_TOP_LCD7.get();
                        break;
                     case PSD_2_LCD15:
                        topBlock = (Block)Blocks.PSD_TOP_LCD15.get();
                        break;
                     case PSD_2_LCD19:
                        topBlock = (Block)Blocks.PSD_TOP_LCD19.get();
                        break;
                     default:
                        topBlock = (Block)Blocks.PSD_TOP.get();
                  }

                  world.setBlockAndUpdate(topPos, topBlock.defaultBlockState());
               } else {
                  world.setBlockAndUpdate(topPos, this.type == ItemPSDBase.EnumPSDAPGType.PSD_GLASS_LCD13 ? ((Block)Blocks.PSD_TOP_LCD13.get()).defaultBlockState() : (this.type == ItemPSDBase.EnumPSDAPGType.PSD_GLASS_LCD14 ? ((Block)Blocks.PSD_TOP_LCD14.get()).defaultBlockState() : (this.type == ItemPSDBase.EnumPSDAPGType.PSD_GLASS_LCD17 ? ((Block)Blocks.PSD_TOP_LCD17.get()).defaultBlockState() : (this.type == ItemPSDBase.EnumPSDAPGType.PSD_GLASS_LCD21 ? ((Block)Blocks.PSD_TOP_LCD21.get()).defaultBlockState() : (this.type == ItemPSDBase.EnumPSDAPGType.PSD_GLASS_LCD22 ? ((Block)Blocks.PSD_TOP_LCD22.get()).defaultBlockState() : ((Block)Blocks.PSD_TOP.get()).defaultBlockState())))));
               }

               world.setBlockAndUpdate(topPos, BlockPSDTop.getActualState(world, topPos));
            }
         }

         context.getItemInHand().shrink(1);
         return InteractionResult.SUCCESS;
      }
   }

   public void appendHoverText(net.minecraft.world.item.ItemStack stack, @Nullable net.minecraft.world.level.Level world, List<net.minecraft.network.chat.Component> tooltip, net.minecraft.world.item.TooltipFlag options) {
      MutableComponent line = Component.translatable("tooltip.mtr_psd_lcd." + this.item.name).withStyle(ChatFormatting.GRAY);
      tooltip.add(line);
   }

   private BlockState getBlockStateFromItem() {
      switch (this.type) {
         case PSD_2_LCD2:
            switch (this.item) {
               case PSD_APG_DOOR:
                  return ((Block)Blocks.PSD_DOOR_LCD2.get()).defaultBlockState();
               case PSD_APG_GLASS:
                  return ((Block)Blocks.PSD_GLASS_2.get()).defaultBlockState();
               default:
                  return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
            }
         case PSD_2_LCD3:
            switch (this.item) {
               case PSD_APG_DOOR:
                  return ((Block)Blocks.PSD_DOOR_LCD3.get()).defaultBlockState();
               case PSD_APG_GLASS:
                  return ((Block)Blocks.PSD_GLASS_2.get()).defaultBlockState();
               default:
                  return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
            }
         case PSD_2_LCD4:
            switch (this.item) {
               case PSD_APG_DOOR:
                  return ((Block)Blocks.PSD_DOOR_LCD4.get()).defaultBlockState();
               case PSD_APG_GLASS:
                  return ((Block)Blocks.PSD_GLASS_2.get()).defaultBlockState();
               default:
                  return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
            }
         case PSD_2_LCD5:
            switch (this.item) {
               case PSD_APG_DOOR:
                  return ((Block)Blocks.PSD_DOOR_LCD5.get()).defaultBlockState();
               case PSD_APG_GLASS:
                  return ((Block)Blocks.PSD_GLASS_2.get()).defaultBlockState();
               default:
                  return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
            }
         case PSD_2_LCD6:
            switch (this.item) {
               case PSD_APG_DOOR:
                  return ((Block)Blocks.PSD_DOOR_LCD6.get()).defaultBlockState();
               case PSD_APG_GLASS:
                  return ((Block)Blocks.PSD_GLASS_2.get()).defaultBlockState();
               default:
                  return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
            }
         case PSD_2_LCD7:
            switch (this.item) {
               case PSD_APG_DOOR:
                  return ((Block)Blocks.PSD_DOOR_LCD7.get()).defaultBlockState();
               case PSD_APG_GLASS:
                  return ((Block)Blocks.PSD_GLASS_2.get()).defaultBlockState();
               default:
                  return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
            }
         case PSD_2_LCD15:
            switch (this.item) {
               case PSD_APG_DOOR:
                  return ((Block)Blocks.PSD_DOOR_LCD15.get()).defaultBlockState();
               case PSD_APG_GLASS:
                  return ((Block)Blocks.PSD_GLASS_2.get()).defaultBlockState();
               default:
                  return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
            }
         case PSD_2_LCD19:
            switch (this.item) {
               case PSD_APG_DOOR:
                  return ((Block)Blocks.PSD_DOOR_LCD19.get()).defaultBlockState();
               case PSD_APG_GLASS:
                  return ((Block)Blocks.PSD_GLASS_2.get()).defaultBlockState();
               default:
                  return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
            }
         case PSD_1:
            switch (this.item) {
               case PSD_APG_DOOR:
                  return ((Block)Blocks.PSD_DOOR.get()).defaultBlockState();
               case PSD_APG_GLASS:
                  return ((Block)Blocks.PSD_GLASS.get()).defaultBlockState();
               default:
                  return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
            }
         case PSD_2:
            switch (this.item) {
               case PSD_APG_DOOR:
                  return ((Block)Blocks.PSD_DOOR_2.get()).defaultBlockState();
               case PSD_APG_GLASS:
                  return ((Block)Blocks.PSD_GLASS_2.get()).defaultBlockState();
               default:
                  return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
            }
         case PSD_GLASS_LCD13:
            return ((Block)Blocks.PSD_GLASS_LCD13.get()).defaultBlockState();
         case PSD_GLASS_LCD14:
            return ((Block)Blocks.PSD_GLASS_LCD14.get()).defaultBlockState();
         case PSD_GLASS_LCD17:
            return ((Block)Blocks.PSD_GLASS_LCD17.get()).defaultBlockState();
         case PSD_GLASS_LCD21:
            return ((Block)Blocks.PSD_GLASS_LCD21.get()).defaultBlockState();
         case PSD_GLASS_LCD22:
            return ((Block)Blocks.PSD_GLASS_LCD22.get()).defaultBlockState();
         default:
            return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
      }
   }

   public static boolean blocksNotReplaceable(UseOnContext context, int width, int height, @Nullable Block blacklistBlock) {
      Direction facing = context.getHorizontalDirection();
      Level world = context.getLevel();
      BlockPos startingPos = context.getClickedPos().relative(context.getClickedFace());

      for(int x = 0; x < width; ++x) {
         BlockPos offsetPos = startingPos.relative(facing.getClockWise(), x);
         if (blacklistBlock != null) {
            boolean isBlacklistedBelow = world.getBlockState(offsetPos.below()).is(blacklistBlock);
            boolean isBlacklistedAbove = world.getBlockState(offsetPos.above(height)).is(blacklistBlock);
            if (isBlacklistedBelow || isBlacklistedAbove) {
               return true;
            }
         }

         for(int y = 0; y < height; ++y) {
            if (!world.getBlockState(offsetPos.above(y)).getBlock().equals(net.minecraft.world.level.block.Blocks.AIR)) {
               return true;
            }
         }
      }

      return false;
   }

   public static enum EnumPSDAPGItem implements net.minecraft.util.StringRepresentable {
      PSD_APG_DOOR("psd_apg_door", true),
      PSD_APG_GLASS("psd_apg_glass", false);

      private final String name;
      private final boolean isDoor;

      private EnumPSDAPGItem(String name, boolean isDoor) {
         this.name = name;
         this.isDoor = isDoor;
      }

      @Nonnull
      public String getSerializedName() {
         return this.name;
      }
   }

   public static enum EnumPSDAPGType {
      PSD_1(true, false),
      PSD_2(true, false),
      PSD_GLASS_LCD13(true, false),
      PSD_GLASS_LCD14(true, false),
      PSD_2_LCD2(true, true),
      PSD_2_LCD3(true, true),
      PSD_2_LCD4(true, true),
      PSD_2_LCD5(true, true),
      PSD_2_LCD6(true, true),
      PSD_2_LCD7(true, true),
      PSD_2_LCD15(true, true),
      PSD_GLASS_LCD17(true, false),
      PSD_GLASS_LCD21(true, false),
      PSD_GLASS_LCD22(true, false),
      PSD_2_LCD19(true, true);

      private final boolean isPSD;
      private final boolean isLcd;

      private EnumPSDAPGType(boolean isPSD, boolean isLcd) {
         this.isPSD = isPSD;
         this.isLcd = isLcd;
      }
   }
}
