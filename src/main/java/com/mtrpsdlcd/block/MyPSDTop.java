package com.mtrpsdlcd.block;

import com.mtrpsdlcd.Diag;
import com.mtrpsdlcd.block.entity.MyPSDTopBE;
import com.mtrpsdlcd.registry.Items;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import mtr.block.BlockPSDDoor;
import mtr.block.BlockPSDGlass;
import mtr.block.BlockPSDTop;
import mtr.block.IBlock;
import mtr.block.BlockPSDTop.EnumPersistent;
import mtr.block.IBlock.EnumSide;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class MyPSDTop extends BlockPSDTop {
   public MyPSDTop() {
   }

   @Nonnull
   public Item asItem() {
      return (Item)Items.PSD_GLASS.get();
   }

   @Nonnull
   public BlockEntityMapper createBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new MyPSDTopBE(blockPos, blockState);
   }

   @Nonnull
   public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockHitResult) {
      return IBlock.checkHoldingItem(world, player, (item) -> {
         if (item == mtr.Items.BRUSH.get()) {
            if (!this.alignDirectionIfNeeded(world, pos, state)) {
               return;
            }

            BlockState cycled = (BlockState)state.cycle(BlockPSDTop.ARROW_DIRECTION);
            world.setBlockAndUpdate(pos, cycled);
            int doorArrow = IBlock.getStatePropertySafe(cycled, BlockPSDTop.ARROW_DIRECTION);
            Direction facing = (Direction)IBlock.getStatePropertySafe(state, BlockPSDTop.FACING);
            this.propagateTop(world, pos, facing.getClockWise(), (offsetPos) -> {
               BlockState neighbor = world.getBlockState(offsetPos);
               world.setBlockAndUpdate(offsetPos, this.applyDirectionToNeighbor(world, neighbor, offsetPos, doorArrow));
            });
            this.propagateTop(world, pos, facing.getCounterClockWise(), (offsetPos) -> {
               BlockState neighbor = world.getBlockState(offsetPos);
               world.setBlockAndUpdate(offsetPos, this.applyDirectionToNeighbor(world, neighbor, offsetPos, doorArrow));
            });
         } else {
            boolean setPersistent = IBlock.getStatePropertySafe(state, BlockPSDTop.PERSISTENT) == EnumPersistent.NONE;
            this.setState(world, pos, setPersistent);
            this.propagateTop(world, pos, ((Direction)IBlock.getStatePropertySafe(state, BlockPSDTop.FACING)).getClockWise(), (offsetPos) -> this.setState(world, offsetPos, setPersistent));
            this.propagateTop(world, pos, ((Direction)IBlock.getStatePropertySafe(state, BlockPSDTop.FACING)).getCounterClockWise(), (offsetPos) -> this.setState(world, offsetPos, setPersistent));
         }

      }, (Runnable)null, new Item[]{(Item)mtr.Items.BRUSH.get(), net.minecraft.world.item.Items.SHEARS});
   }

   private void setState(Level world, BlockPos pos, boolean persistent) {
      Block blockBelow = world.getBlockState(pos.below()).getBlock();
      BlockState current = world.getBlockState(pos);
      BlockState toggled;
      if (persistent) {
         BlockPSDTop.EnumPersistent type;
         if (blockBelow instanceof BlockPSDDoor) {
            type = EnumPersistent.ARROW;
         } else if (blockBelow instanceof BlockPSDGlass) {
            type = EnumPersistent.ROUTE;
         } else {
            type = EnumPersistent.BLANK;
         }

         toggled = (BlockState)current.setValue(BlockPSDTop.PERSISTENT, type);
      } else {
         toggled = (BlockState)current.setValue(BlockPSDTop.PERSISTENT, EnumPersistent.NONE);
      }

      world.setBlockAndUpdate(pos, toggled);
   }

   private void propagateTop(net.minecraft.world.level.Level world, net.minecraft.core.BlockPos pos, net.minecraft.core.Direction direction, Consumer<net.minecraft.core.BlockPos> consumer) {
      for(int i = 1; i <= 1; ++i) {
         BlockPos offsetPos = pos.relative(direction, i);
         if (this.isTop(world.getBlockState(offsetPos).getBlock())) {
            consumer.accept(offsetPos);
            this.propagateTop(world, offsetPos, direction, consumer);
            return;
         }
      }

   }

   private boolean isTop(Block block) {
      return block instanceof BlockPSDTop;
   }

   private BlockState applyDirectionToNeighbor(Level world, BlockState neighbor, BlockPos neighborPos, int doorArrow) {
      Block below = world.getBlockState(neighborPos.below()).getBlock();
      if (below instanceof BlockPSDGlass) {
         int glassVal = doorArrow == 2 ? 2 : 0;
         return (BlockState)neighbor.setValue(BlockPSDTop.ARROW_DIRECTION, glassVal);
      } else {
         return (BlockState)neighbor.setValue(BlockPSDTop.ARROW_DIRECTION, doorArrow);
      }
   }

   private boolean alignDirectionIfNeeded(Level world, BlockPos pos, BlockState state) {
      int myDir = IBlock.getStatePropertySafe(state, BlockPSDTop.ARROW_DIRECTION);
      if (myDir <= 0) {
         return true;
      } else {
         Direction facing = (Direction)IBlock.getStatePropertySafe(state, BlockPSDTop.FACING);
         Map<Integer, Integer> counts = new HashMap();
         counts.put(myDir, 0);
         this.collectDirectionSide(world, pos, facing.getClockWise(), counts);
         this.collectDirectionSide(world, pos, facing.getCounterClockWise(), counts);
         int majority = this.pickMajority(counts);
         if (majority > 0 && majority != myDir) {
            world.setBlockAndUpdate(pos, (BlockState)state.setValue(BlockPSDTop.ARROW_DIRECTION, majority));
            return false;
         } else {
            return true;
         }
      }
   }

   private void collectDirectionSide(net.minecraft.world.level.Level world, net.minecraft.core.BlockPos pos, net.minecraft.core.Direction dir, Map<Integer, Integer> counts) {
      BlockPos next = pos.relative(dir);

      while(true) {
         BlockState s = world.getBlockState(next);
         if (!(s.getBlock() instanceof BlockPSDTop)) {
            return;
         }

         Block below = world.getBlockState(next.below()).getBlock();
         if (below instanceof BlockPSDGlass) {
            return;
         }

         int d = IBlock.getStatePropertySafe(s, BlockPSDTop.ARROW_DIRECTION);
         if (d > 0) {
            counts.merge(d, 1, Integer::sum);
         }

         next = next.relative(dir);
      }
   }

   private int pickMajority(Map<Integer, Integer> counts) {
      int bestDir = 0;
      int bestCount = 0;

      for(Map.Entry<Integer, Integer> e : counts.entrySet()) {
         int c = e.getValue();
         if (c > bestCount) {
            bestCount = c;
            bestDir = e.getKey();
         }
      }

      return bestDir;
   }

   private boolean isStandalone() {
      return this instanceof IStandaloneTopModule;
   }

   @Nonnull
   public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
      return this.isStandalone() ? BlockPSDTop.getActualState(world, pos) : super.updateShape(state, direction, neighborState, world, pos, neighborPos);
   }

   @Nonnull
   public BlockState getStateForPlacement(BlockPlaceContext ctx) {
      return this.isStandalone() ? (BlockState)this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, ctx.getHorizontalDirection()) : super.getStateForPlacement(ctx);
   }

   private boolean isDoubleModule() {
      return this instanceof MyPSDTopLcd8 || this instanceof MyPSDTopLcd9 || this instanceof MyPSDTopLcd10 || this instanceof MyPSDTopLcd11;
   }

   private static boolean isDoubleModule(Block block) {
      return block instanceof MyPSDTopLcd8 || block instanceof MyPSDTopLcd9 || block instanceof MyPSDTopLcd10 || block instanceof MyPSDTopLcd11;
   }

   public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
      Diag.breakLog(state, pos, player);
      if (this.isDoubleModule()) {
         Direction facing = (Direction)IBlock.getStatePropertySafe(state, HorizontalDirectionalBlock.FACING);
         IBlock.EnumSide side = (IBlock.EnumSide)IBlock.getStatePropertySafe(state, IBlock.SIDE_EXTENDED);
         if (side == EnumSide.LEFT || side == EnumSide.RIGHT) {
            BlockPos partnerPos = side == EnumSide.LEFT ? pos.relative(facing.getClockWise()) : pos.relative(facing.getCounterClockWise());
            if (isDoubleModule(world.getBlockState(partnerPos).getBlock())) {
               world.setBlock(partnerPos, Blocks.AIR.defaultBlockState(), 35);
            }
         }
      }

      super.playerWillDestroy(world, pos, state, player);
   }
}
