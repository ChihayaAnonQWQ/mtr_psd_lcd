package com.mtrpsdlcd.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MyPSDPillar extends Block {
   public static final net.minecraft.world.level.block.state.properties.Property<net.minecraft.core.Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

   public MyPSDPillar() {
      super(Properties.of().mapColor(MapColor.COLOR_GRAY).requiresCorrectToolForDrops().strength(2.0F).noOcclusion());
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(FACING, Direction.NORTH));
   }

   protected void createBlockStateDefinition(net.minecraft.world.level.block.state.StateDefinition.Builder<net.minecraft.world.level.block.Block, net.minecraft.world.level.block.state.BlockState> builder) {
      builder.add(new Property[]{FACING});
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      Direction horizontal = context.getPlayer() != null ? context.getPlayer().getDirection() : context.getNearestLookingDirection().getOpposite();
      Direction safe = horizontal != Direction.UP && horizontal != Direction.DOWN ? horizontal : Direction.NORTH;
      return (BlockState)this.defaultBlockState().setValue(FACING, safe);
   }

   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return shapeFor(state);
   }

   public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return shapeFor(state);
   }

   private static VoxelShape shapeFor(BlockState state) {
      switch ((Direction)state.getValue(FACING)) {
         case SOUTH:
            return Block.box(5.0D, 0.0D, 10.0D, 11.0D, 16.0D, 16.0D);
         case WEST:
            return Block.box(0.0D, 0.0D, 5.0D, 6.0D, 16.0D, 11.0D);
         case EAST:
            return Block.box(10.0D, 0.0D, 5.0D, 16.0D, 16.0D, 11.0D);
         default:
            return Block.box(5.0D, 0.0D, 0.0D, 11.0D, 16.0D, 6.0D);
      }
   }
}
