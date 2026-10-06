package com.mtrpsdlcd.block;

import com.mtrpsdlcd.Diag;
import com.mtrpsdlcd.registry.Items;
import javax.annotation.Nonnull;
import mtr.block.BlockPSDGlass;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class MyPSDGlass extends BlockPSDGlass {
   private final int style;

   public MyPSDGlass(int style) {
      super(style);
      this.style = style;
   }

   public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
      Diag.breakLog(state, pos, player);
      super.playerWillDestroy(world, pos, state, player);
   }

   @Nonnull
   public Item asItem() {
      return this.style == 0 ? (Item)Items.PSD_GLASS.get() : (Item)Items.PSD_GLASS_2.get();
   }
}
