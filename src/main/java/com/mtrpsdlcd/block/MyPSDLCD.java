package com.mtrpsdlcd.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;

public class MyPSDLCD extends Block {
   public MyPSDLCD() {
      super(Properties.of().mapColor(MapColor.COLOR_GRAY).requiresCorrectToolForDrops().strength(2.0F).noOcclusion());
   }
}
