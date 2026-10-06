package com.mtrpsdlcd.render;

import com.mtrpsdlcd.block.IStandaloneTopModule;
import com.mtrpsdlcd.block.MyPSDTop;
import com.mtrpsdlcd.block.MyPSDTopLcd12;
import com.mtrpsdlcd.block.MyPSDTopLcd13;
import com.mtrpsdlcd.block.MyPSDTopLcd14;
import com.mtrpsdlcd.block.MyPSDTopLcd15;
import com.mtrpsdlcd.block.MyPSDTopLcd16;
import com.mtrpsdlcd.block.MyPSDTopLcd2;
import com.mtrpsdlcd.block.MyPSDTopLcd3;
import com.mtrpsdlcd.block.MyPSDTopLcd4;
import com.mtrpsdlcd.block.MyPSDTopLcd5;
import com.mtrpsdlcd.block.MyPSDTopLcd6;
import com.mtrpsdlcd.block.MyPSDTopLcd7;
import com.mtrpsdlcd.block.PSDCustomText;
import com.mtrpsdlcd.block.entity.MyPSDTopLcd14BE;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import mtr.block.BlockPSDAPGDoorBase;
import mtr.block.BlockPSDAPGGlassEndBase;
import mtr.block.BlockPSDTop;
import mtr.block.IBlock;
import mtr.block.BlockPSDTop.EnumPersistent;
import mtr.block.IBlock.EnumSide;
import mtr.client.ClientCache;
import mtr.client.ClientData;
import mtr.client.IDrawing;
import mtr.data.Route;
import mtr.data.IGui.HorizontalAlignment;
import mtr.render.RenderRouteBase;
import mtr.render.RenderTrains;
import mtr.render.StoredMatrixTransformations;
import mtr.render.RenderTrains.QueuedRenderLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class RenderPSDTop<T extends BlockPSDTop.TileEntityRouteBase> extends RenderRouteBase<T> {
   private RenderRouteBase.RenderType lastRenderType = RenderType.NONE;
   private Level lastWorld;
   private BlockPos lastPos;
   private static final float SQRT_OF_TWO = (float)Math.sqrt(2.0D);
   private static final float END_FRONT_OFFSET = 1.0F / (SQRT_OF_TWO * 16.0F);
   private static final float BOTTOM_DIAGONAL_OFFSET = ((float)Math.sqrt(3.0D) - 1.0F) / 32.0F;
   private static final float ROOT_TWO_SCALED = SQRT_OF_TWO / 16.0F;
   private static final float BOTTOM_END_DIAGONAL_OFFSET = END_FRONT_OFFSET - BOTTOM_DIAGONAL_OFFSET / SQRT_OF_TWO;
   private static final float COLOR_STRIP_START = 0.90625F;
   private static final float COLOR_STRIP_END = 0.9375F;
   /** -Dmtrpsdlcd.dumpmaps=true dumps generated panel textures to <gamedir>/psd-debug. */
   private static final boolean DUMP_TEXTURES = Boolean.getBoolean("mtrpsdlcd.dumpmaps");
   private static final float SCALE = 90.0F;
   private static final float LED_Z_OFFSET = 0.06F;
   private static final float LED_PANEL_BACKING_Z = 0.02F;
   private static final float LED_PANEL_EDGE = 0.05F;
   private static final float LED_PANEL_Y1 = 0.68F;
   private static final float LED_PANEL_Y2 = 0.9F;
   private static final float NEXT_LINE_EDGE_GAP = 0.01F;
   private static final float ARROW_ROW_Y2 = 0.625F;
   private static final float GLASS_BORDER = 0.02F;
   private static final float GLASS_BORDER_Z = 0.015F;
   private static final float ROUTE_Y1 = 0.71F;
   private static final float ROUTE_Y2 = 0.87F;
   private static final float ROUTE_PAD = 0.03F;
   private static final float TEXT_GAP = 0.08F;
   private static final String PLATFORM_LABEL = "\u7ad9\u53f0";
   private static final float PLATFORM_TEXT_GAP = 0.08F;
   private static final String PLATFORM_DEST_PREFIX = "\u5f80 ";
   private static final float TEXT_Y = 0.73F;
   private static final float TEXT_SCALE = 1.0F;
   private static final float PANEL_TEXT_BAND_HEIGHT = 0.18F;
   private static final float PANEL_TEXT_TARGET_HEIGHT = 0.08888889F;
   private static final float PANEL_TEXT_SCALE_SAFETY = 0.49382716F;
   private static final float PANEL_TEXT_SCALE_WEATHER = 0.49382716F;
   private static final float PANEL_TEXT_SCALE_DESTINATION = 0.49382716F;
   private static final float PANEL_TEXT_SCALE_BADGE = 0.49382716F;
   private static final float PANEL_TEXT_SCALE_CAR_COUNT = 0.49382716F;
   private static final float PANEL_TEXT_SCALE_ARRIVAL = 0.49382716F;
   private static final float LED_BADGE_BACKGROUND_Z = 0.01F;
   private static final int COLOR_PANEL = -1;
   private static final int COLOR_TEXT = -16777216;
   private static final int COLOR_ARRIVED = -16718218;
   private static final String DOOR_OPEN_MESSAGE = "\u8f66\u95e8\u5f00\u542f\uff0c\u8bf7\u6ce8\u610f\u5b89\u5168";
   private static final String DOOR_CLOSED_MESSAGE = "\u8f66\u95e8\u5173\u95ed\uff0c\u8bf7\u52ff\u501a\u9760\u8f66\u95e8";
   private static final String ARRIVING_MESSAGE = "\u8f66\u8f86\u8fdb\u7ad9\uff0c\u8bf7\u6ce8\u610f\u5b89\u5168";
   private static final int COLOR_DOOR_CLOSED = -65536;
   private static final int COLOR_ARRIVING_MESSAGE = -23296;
   private static final long STOPPED_REMAINING_MILLIS = 5000L;
   private static final long ARRIVING_REMAINING_MILLIS = 60000L;
   private static final int COLOR_ROUTE_TEXT = -1;
   private static final float SONG_Z_OFFSET = -5.0E-4F;
   private static final Map<String, String> STATION_EN_FALLBACK = Map.of("\u5b89\u57ce\u9547", "AnChengZhen", "\u5de1\u6cb3\u574a", "XunHeFang");
   private static final float GROOVE_SCREEN_V1 = 0.056F;
   private static final float GROOVE_SCREEN_V2 = 0.874F;
   private static final int GROOVE_SCREEN_BG_COLOR = -1184275;
   private static final float GROOVE_STRIP_V1 = 0.90625F;
   private static final float GROOVE_STRIP_V2 = 0.9375F;
   private static final float GROOVE_SCREEN_MAP_Z = 0.08F;
   private static final float GROOVE_SCREEN_LED_Z = 0.1F;
   private static final ResourceLocation WHITE_TEXTURE = new ResourceLocation("mtr", "textures/block/white.png");
   private static final ResourceLocation TRANSPARENT_TEXTURE = new ResourceLocation("mtr", "textures/block/transparent.png");
   private static final Set<String> BAND_LOGGED = ConcurrentHashMap.newKeySet();
   private static final Set<String> BRANCH_LOGGED = ConcurrentHashMap.newKeySet();
   private static final Set<String> ARROW_LOGGED = ConcurrentHashMap.newKeySet();
   private static final Map<String, String> PANEL_LAST = new ConcurrentHashMap();
   private static final Map<String, Integer> PANEL_FRAMES = new ConcurrentHashMap();
   private static final Map<String, Integer> PANEL_CHANGES = new ConcurrentHashMap();
   private static final Map<String, String> LCD_PANEL_LOGGED = new ConcurrentHashMap();
   private static final Map<String, String> NEXT_LINE_LOGGED = new ConcurrentHashMap();
   private static final int PANEL_HEARTBEAT_FRAMES = 1200;

   public RenderPSDTop(BlockEntityRenderDispatcher dispatcher) {
      super(dispatcher, 1.95F, 3.0F, 6.0F, 0.125F, true, BlockPSDTop.ARROW_DIRECTION);
   }

   protected RenderRouteBase.RenderType getRenderType(BlockGetter world, BlockPos pos, BlockState state) {
      BlockPSDTop.EnumPersistent persistent = (BlockPSDTop.EnumPersistent)IBlock.getStatePropertySafe(state, BlockPSDTop.PERSISTENT);
      RenderRouteBase.RenderType renderType;
      if (isStandaloneModule(state.getBlock())) {
         renderType = state.getBlock() instanceof MyPSDTopLcd12 ? RenderType.NONE : RenderType.ARROW;
      } else if (persistent == EnumPersistent.NONE) {
         Block blockBelow = world.getBlockState(pos.below()).getBlock();
         if (blockBelow instanceof BlockPSDAPGDoorBase) {
            renderType = RenderType.ARROW;
         } else if (!(blockBelow instanceof BlockPSDAPGGlassEndBase)) {
            renderType = RenderType.ROUTE;
         } else {
            renderType = RenderType.NONE;
         }
      } else {
         renderType = persistent == EnumPersistent.ARROW ? RenderType.ARROW : (persistent == EnumPersistent.ROUTE ? RenderType.ROUTE : RenderType.NONE);
      }

      this.lastRenderType = renderType;
      this.lastWorld = world instanceof Level ? (Level)world : null;
      this.lastPos = pos;
      if (ClientData.DATA_CACHE != null) {
         ClientData.DATA_CACHE.getRouteSquare(0, "1", HorizontalAlignment.CENTER);
      }

      this.branchDiag(pos, state);
      return RenderType.NONE;
   }

   protected void renderAdditionalUnmodified(StoredMatrixTransformations storedMatrixTransformations, BlockState state, Direction facing, int light) {
      boolean airLeft = IBlock.getStatePropertySafe(state, BlockPSDTop.AIR_LEFT);
      boolean airRight = IBlock.getStatePropertySafe(state, BlockPSDTop.AIR_RIGHT);
      boolean persistent = IBlock.getStatePropertySafe(state, BlockPSDTop.PERSISTENT) != EnumPersistent.NONE;
      if ((airLeft || airRight) && !persistent) {
         RenderTrains.scheduleRender(new ResourceLocation("mtr", "textures/block/psd_top.png"), false, QueuedRenderLayer.EXTERIOR, (matrices, vertexConsumer) -> {
            storedMatrixTransformations.transform(matrices);
            if (airLeft) {
               IDrawing.drawTexture(matrices, vertexConsumer, -0.125F, 0.0F, 0.5F, 0.5F, 0.0F, -0.125F, 0.5F, 1.0F, -0.125F, -0.125F, 1.0F, 0.5F, 0.0F, 0.0F, 1.0F, 1.0F, facing, -1, light);
               IDrawing.drawTexture(matrices, vertexConsumer, 0.5F - END_FRONT_OFFSET, 0.0625F, -0.5F - END_FRONT_OFFSET, -0.25F - END_FRONT_OFFSET, 0.0625F, 0.25F - END_FRONT_OFFSET, -0.25F - END_FRONT_OFFSET, 1.0F, 0.25F - END_FRONT_OFFSET, 0.5F - END_FRONT_OFFSET, 1.0F, -0.5F - END_FRONT_OFFSET, 0.0F, 0.0F, 1.0F, 0.9375F, facing.getOpposite(), -1, light);
               IDrawing.drawTexture(matrices, vertexConsumer, 0.5F - BOTTOM_END_DIAGONAL_OFFSET, BOTTOM_DIAGONAL_OFFSET, -0.5F - BOTTOM_END_DIAGONAL_OFFSET, -0.25F - BOTTOM_END_DIAGONAL_OFFSET, BOTTOM_DIAGONAL_OFFSET, 0.25F - BOTTOM_END_DIAGONAL_OFFSET, -0.25F - END_FRONT_OFFSET, 0.0625F, 0.25F - END_FRONT_OFFSET, 0.5F - END_FRONT_OFFSET, 0.0625F, -0.5F - END_FRONT_OFFSET, 0.0F, 0.9375F, 1.0F, 0.96875F, facing.getOpposite(), -1, light);
               IDrawing.drawTexture(matrices, vertexConsumer, 0.5F, 0.0F, -0.5F, -0.25F, 0.0F, 0.25F, -0.25F - BOTTOM_END_DIAGONAL_OFFSET, BOTTOM_DIAGONAL_OFFSET, 0.25F - BOTTOM_END_DIAGONAL_OFFSET, 0.5F - BOTTOM_END_DIAGONAL_OFFSET, BOTTOM_DIAGONAL_OFFSET, -0.5F - BOTTOM_END_DIAGONAL_OFFSET, 0.0F, 0.96875F, 1.0F, 1.0F, facing.getOpposite(), -1, light);
               IDrawing.drawTexture(matrices, vertexConsumer, 0.5F, 0.003125F, -0.125F, -0.125F, 0.003125F, 0.5F, -0.125F, 0.003125F, 0.125F, 0.5F, 0.003125F, -0.5F, 0.125F, 0.125F, 0.1875F, 0.1875F, facing, -1, light);
               IDrawing.drawTexture(matrices, vertexConsumer, 0.5F, 0.996875F, -0.5F, -0.125F, 0.996875F, 0.125F, -0.125F, 0.996875F, 0.5F, 0.5F, 0.996875F, -0.125F, 0.125F, 0.125F, 0.1875F, 0.1875F, Direction.UP, -1, light);
               IDrawing.drawTexture(matrices, vertexConsumer, 0.5F - END_FRONT_OFFSET, 0.996875F, -0.5F - END_FRONT_OFFSET, -0.125F - ROOT_TWO_SCALED, 0.996875F, 0.125F, -0.125F, 0.996875F, 0.125F, 0.5F, 0.996875F, -0.5F, 0.125F, 0.125F, 0.1875F, 0.1875F, Direction.UP, -1, light);
               IDrawing.drawTexture(matrices, vertexConsumer, 0.5F, 0.0625F, -0.5F, 0.5F - END_FRONT_OFFSET, 0.0625F, -0.5F - END_FRONT_OFFSET, 0.5F - END_FRONT_OFFSET, 1.0F, -0.5F - END_FRONT_OFFSET, 0.5F, 1.0F, -0.5F, 0.9375F, 0.0F, 1.0F, 0.9375F, facing, -1, light);
               IDrawing.drawTexture(matrices, vertexConsumer, 0.5F, 0.0F, -0.5F, 0.5F - BOTTOM_END_DIAGONAL_OFFSET, BOTTOM_DIAGONAL_OFFSET, -0.5F - BOTTOM_END_DIAGONAL_OFFSET, 0.5F - END_FRONT_OFFSET, 0.0625F, -0.5F - END_FRONT_OFFSET, 0.5F, 0.0625F, -0.5F, 0.9375F, 0.9375F, 1.0F, 1.0F, facing, -1, light);
            }

            if (airRight) {
               IDrawing.drawTexture(matrices, vertexConsumer, -0.5F, 0.0F, -0.125F, 0.125F, 0.0F, 0.5F, 0.125F, 1.0F, 0.5F, -0.5F, 1.0F, -0.125F, 0.0F, 0.0F, 1.0F, 1.0F, facing, -1, light);
               IDrawing.drawTexture(matrices, vertexConsumer, 0.25F + END_FRONT_OFFSET, 0.0625F, 0.25F - END_FRONT_OFFSET, -0.5F + END_FRONT_OFFSET, 0.0625F, -0.5F - END_FRONT_OFFSET, -0.5F + END_FRONT_OFFSET, 1.0F, -0.5F - END_FRONT_OFFSET, 0.25F + END_FRONT_OFFSET, 1.0F, 0.25F - END_FRONT_OFFSET, 0.0F, 0.0F, 1.0F, 0.9375F, facing.getOpposite(), -1, light);
               IDrawing.drawTexture(matrices, vertexConsumer, 0.25F + BOTTOM_END_DIAGONAL_OFFSET, BOTTOM_DIAGONAL_OFFSET, 0.25F - BOTTOM_END_DIAGONAL_OFFSET, -0.5F + BOTTOM_END_DIAGONAL_OFFSET, BOTTOM_DIAGONAL_OFFSET, -0.5F - BOTTOM_END_DIAGONAL_OFFSET, -0.5F + END_FRONT_OFFSET, 0.0625F, -0.5F - END_FRONT_OFFSET, 0.25F + END_FRONT_OFFSET, 0.0625F, 0.25F - END_FRONT_OFFSET, 0.0F, 0.9375F, 1.0F, 0.96875F, facing.getOpposite(), -1, light);
               IDrawing.drawTexture(matrices, vertexConsumer, 0.25F, 0.0F, 0.25F, -0.5F, 0.0F, -0.5F, -0.5F + BOTTOM_END_DIAGONAL_OFFSET, BOTTOM_DIAGONAL_OFFSET, -0.5F - BOTTOM_END_DIAGONAL_OFFSET, 0.25F + BOTTOM_END_DIAGONAL_OFFSET, BOTTOM_DIAGONAL_OFFSET, 0.25F - BOTTOM_END_DIAGONAL_OFFSET, 0.0F, 0.96875F, 1.0F, 1.0F, facing.getOpposite(), -1, light);
               IDrawing.drawTexture(matrices, vertexConsumer, 0.125F, 0.003125F, 0.5F, -0.5F, 0.003125F, -0.125F, -0.5F, 0.003125F, -0.5F, 0.125F, 0.003125F, 0.125F, 0.125F, 0.125F, 0.1875F, 0.1875F, facing, -1, light);
               IDrawing.drawTexture(matrices, vertexConsumer, 0.125F, 0.996875F, 0.125F, -0.5F, 0.996875F, -0.5F, -0.5F, 0.996875F, -0.125F, 0.125F, 0.996875F, 0.5F, 0.125F, 0.125F, 0.1875F, 0.1875F, Direction.UP, -1, light);
               IDrawing.drawTexture(matrices, vertexConsumer, 0.125F + ROOT_TWO_SCALED, 0.996875F, 0.125F, -0.5F + END_FRONT_OFFSET, 0.996875F, -0.5F - END_FRONT_OFFSET, -0.5F, 0.996875F, -0.5F, 0.125F, 0.996875F, 0.125F, 0.125F, 0.125F, 0.1875F, 0.1875F, Direction.UP, -1, light);
               IDrawing.drawTexture(matrices, vertexConsumer, -0.5F + END_FRONT_OFFSET, 0.0625F, -0.5F - END_FRONT_OFFSET, -0.5F, 0.0625F, -0.5F, -0.5F, 1.0F, -0.5F, -0.5F + END_FRONT_OFFSET, 1.0F, -0.5F - END_FRONT_OFFSET, 0.0F, 0.0F, 0.0625F, 0.9375F, facing, -1, light);
               IDrawing.drawTexture(matrices, vertexConsumer, -0.5F + BOTTOM_END_DIAGONAL_OFFSET, BOTTOM_DIAGONAL_OFFSET, -0.5F - BOTTOM_END_DIAGONAL_OFFSET, -0.5F, 0.0F, -0.5F, -0.5F, 0.0625F, -0.5F, -0.5F + END_FRONT_OFFSET, 0.0625F, -0.5F - END_FRONT_OFFSET, 0.0F, 0.9375F, 0.0625F, 1.0F, facing, -1, light);
            }

            matrices.popPose();
         });
      }
   }

   protected void renderAdditional(StoredMatrixTransformations storedMatrixTransformations, long platformId, BlockState state, int leftBlocks, int rightBlocks, Direction facing, int color, int light) {
      Block block = state.getBlock();
      boolean lcd = block instanceof MyPSDTopLcd2 || block instanceof MyPSDTopLcd3;
      boolean lcd45 = block instanceof MyPSDTopLcd4 || block instanceof MyPSDTopLcd5;
      boolean lcd67 = block instanceof MyPSDTopLcd6 || block instanceof MyPSDTopLcd7;
      if (this.lastWorld != null && this.lastPos != null) {
         Direction rawFacing = facing.getOpposite();
         BlockPos currentPos = this.lastPos.relative(rawFacing.getClockWise(), leftBlocks);
         Block currentBlockBelow = this.lastWorld.getBlockState(currentPos.below()).getBlock();
         boolean standaloneModule = isStandaloneModule(block);
         // 原版信息顶板（mtr_psd_lcd:psd_top，类恰为 MyPSDTop）与 MTR 原版顶板一致：
         // 只要所在位置能取到站台数据就绘制内容，不要求正下方必须是屏蔽门/玻璃。
         boolean vanillaTop = block.getClass() == MyPSDTop.class;
         this.bandDiag(state, currentPos, currentBlockBelow, standaloneModule, lcd45, lcd67);
         if (currentBlockBelow instanceof BlockPSDAPGDoorBase || standaloneModule || vanillaTop) {
            if (block instanceof MyPSDTopLcd12) {
               if (IBlock.getStatePropertySafe(state, BlockPSDTop.PERSISTENT) == EnumPersistent.ROUTE) {
                  this.drawGlassRoute(storedMatrixTransformations, platformId, state, leftBlocks, rightBlocks, facing, color, light);
               }

            } else if (block instanceof MyPSDTopLcd13) {
               if (IBlock.getStatePropertySafe(state, BlockPSDTop.PERSISTENT) == EnumPersistent.ROUTE) {
                  this.drawPerRouteMaps(storedMatrixTransformations, platformId, state, leftBlocks, rightBlocks, facing, color, light);
               }

               this.drawColorStrip(storedMatrixTransformations, platformId, leftBlocks, rightBlocks, facing, light);
            } else if (block instanceof MyPSDTopLcd14) {
               int routeIndex = this.getLcd14RouteIndex(state);
               if (IBlock.getStatePropertySafe(state, BlockPSDTop.PERSISTENT) == EnumPersistent.ROUTE) {
                  this.drawGroovedScreen(storedMatrixTransformations, platformId, state, leftBlocks, rightBlocks, facing, color, light, routeIndex);
               }

               this.drawGroovedColorStrip(storedMatrixTransformations, platformId, leftBlocks, rightBlocks, facing, light);
            } else {
               boolean isNotPersistent = IBlock.getStatePropertySafe(state, BlockPSDTop.PERSISTENT) == EnumPersistent.NONE;
               boolean airLeft = isNotPersistent && IBlock.getStatePropertySafe(state, BlockPSDTop.AIR_LEFT);
               boolean airRight = isNotPersistent && IBlock.getStatePropertySafe(state, BlockPSDTop.AIR_RIGHT);
               float glassX1 = airLeft ? 0.625F : 0.0F;
               float glassX2 = airRight ? 0.375F : 1.0F;
               RenderTrains.scheduleRender(WHITE_TEXTURE, false, QueuedRenderLayer.EXTERIOR, (matrices, vertexConsumer) -> {
                  storedMatrixTransformations.transform(matrices);
                  float borderZ = 0.015F;
                  // The two horizontal "glass frame" strips that upstream drew across
                  // every top module (block heights 0.88-0.9 and 0.68-0.7) are omitted:
                  // they cut straight through the station name / route text and were
                  // reported as covering the content (PORT-REPORT.md §4.6).
                  if (leftBlocks == 0) {
                     IDrawing.drawTexture(matrices, vertexConsumer, glassX1, 0.68F, 0.015F, glassX1 + 0.02F, 0.9F, 0.015F, 0.0F, 0.0F, 1.0F, 1.0F, facing, -1, light);
                  }

                  if (rightBlocks == 0) {
                     IDrawing.drawTexture(matrices, vertexConsumer, glassX2 - 0.02F, 0.68F, 0.015F, glassX2, 0.9F, 0.015F, 0.0F, 0.0F, 1.0F, 1.0F, facing, -1, light);
                  }

                  matrices.popPose();
               });
               RenderTrains.scheduleRender(TRANSPARENT_TEXTURE, false, QueuedRenderLayer.LIGHT_TRANSLUCENT, (matrices, vertexConsumer) -> {
                  storedMatrixTransformations.transform(matrices);
                  IDrawing.drawTexture(matrices, vertexConsumer, glassX1, 0.68F, 0.0F, glassX2, 0.9F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, facing, -1, light);
                  matrices.popPose();
               });
               this.drawColorStrip(storedMatrixTransformations, platformId, leftBlocks, rightBlocks, facing, light);
               if (!lcd && this.lastRenderType == RenderType.ARROW) {
                  int arrowDirection = IBlock.getStatePropertySafe(state, BlockPSDTop.ARROW_DIRECTION);
                  boolean hasLeft = (arrowDirection & 1) > 0;
                  boolean hasRight = (arrowDirection & 2) > 0;
                  float panelWidth = (float)(leftBlocks + 1 + rightBlocks) - 0.015625F;
                  float panelHeight = 0.4375F;
                  boolean showPlatform = !lcd45 && !lcd67;
                  PSDTextureCache.Entry arrowTexture = this.getOwnArrowTexture(platformId, hasLeft, hasRight, panelWidth / 0.4375F, showPlatform);
                  if (arrowTexture.ready) {
                     RenderTrains.scheduleRender(arrowTexture.identifier, false, QueuedRenderLayer.EXTERIOR, (matrices, vertexConsumer) -> {
                        storedMatrixTransformations.transform(matrices);
                        float y1 = 0.1875F - (lcd67 ? 0.091874994F : 0.0F);
                        float y2 = 0.625F - (lcd67 ? 0.091874994F : 0.0F);
                        IDrawing.drawTexture(matrices, vertexConsumer, leftBlocks == 0 ? 0.0078125F : 0.0F, y1, 0.0F, 1.0F - (rightBlocks == 0 ? 0.0078125F : 0.0F), y2, 0.0F, ((float)leftBlocks - (leftBlocks == 0 ? 0.0F : 0.0078125F)) / panelWidth, 0.0F, (panelWidth - (float)rightBlocks + (rightBlocks == 0 ? 0.0F : 0.0078125F)) / panelWidth, 1.0F, facing, color, light);
                        matrices.popPose();
                     });
                  }

                  this.arrowDiag(this.currentPosOf(facing, 0), platformId, hasLeft, hasRight, showPlatform, lcd45, lcd67, arrowTexture);
               }

               if (lcd67 && leftBlocks == 0 && this.lastRenderType == RenderType.ARROW) {
                  this.drawNextLineRows(storedMatrixTransformations, platformId, facing, rightBlocks, light);
               }

               if (lcd && leftBlocks == 0 && this.lastRenderType == RenderType.ARROW) {
                  this.drawLcdPanel(storedMatrixTransformations, platformId, state, facing, rightBlocks, light);
               }

               this.drawLedPanel(storedMatrixTransformations, platformId, state, leftBlocks, rightBlocks, facing, light, currentPos);
            }
         }
      }
   }

   protected float getAdditionalOffset(BlockState state) {
      if (!(state.getBlock() instanceof MyPSDTopLcd12) && !(state.getBlock() instanceof MyPSDTopLcd13) && !(state.getBlock() instanceof MyPSDTopLcd14)) {
         return IBlock.getStatePropertySafe(state, BlockPSDTop.PERSISTENT) == EnumPersistent.NONE ? 0.0F : 0.46875F;
      } else {
         return 0.0F;
      }
   }

   private void drawGlassRoute(StoredMatrixTransformations storedMatrixTransformations, long platformId, BlockState state, int leftBlocks, int rightBlocks, Direction facing, int color, int light) {
      float width = (float)(leftBlocks + rightBlocks + 1) - this.sidePadding * 2.0F;
      float routeHeight = 1.0F - this.topPadding - this.bottomPadding;
      int arrowDirection = IBlock.getStatePropertySafe(state, BlockPSDTop.ARROW_DIRECTION);
      float aspectRatio = width / routeHeight;
      String key = PSDTextureKeys.glassRoute(platformId, aspectRatio, arrowDirection == 2, refreshSignature(platformId));
      PSDTextureCache.Entry route = PSDTextureCache.get(key, () -> {
         RouteMapGenerator.setConstants();
         return RouteMapGenerator.generateRouteMap(platformId, -1L, false, arrowDirection == 2, aspectRatio, true);
      });
      if (route.ready) {
         RenderTrains.scheduleRender(route.identifier, false, QueuedRenderLayer.EXTERIOR, (matrices, vertexConsumer) -> {
            storedMatrixTransformations.transform(matrices);
            IDrawing.drawTexture(matrices, vertexConsumer, leftBlocks == 0 ? this.sidePadding : 0.0F, this.topPadding, 0.0F, 1.0F - (rightBlocks == 0 ? this.sidePadding : 0.0F), 1.0F - this.bottomPadding, 0.0F, ((float)leftBlocks - (leftBlocks == 0 ? 0.0F : this.sidePadding)) / width, 0.0F, (width - (float)rightBlocks + (rightBlocks == 0 ? 0.0F : this.sidePadding)) / width, 1.0F, facing.getOpposite(), color, light);
            matrices.popPose();
         });
      }

      ClientCache.DynamicResource colorStrip = colorStripOf(platformId);
      if (colorStrip != null && colorStrip.width == 1) {
         RenderTrains.scheduleRender(colorStrip.resourceLocation, false, QueuedRenderLayer.EXTERIOR, (matrices, vertexConsumer) -> {
            storedMatrixTransformations.transform(matrices);
            IDrawing.drawTexture(matrices, vertexConsumer, leftBlocks == 0 ? this.sidePadding : 0.0F, 0.90625F, 0.0F, 1.0F - (rightBlocks == 0 ? this.sidePadding : 0.0F), 0.9375F, 0.0F, facing, -1, light);
            matrices.popPose();
         });
      }

   }

   private void drawPerRouteMaps(StoredMatrixTransformations storedMatrixTransformations, long platformId, BlockState state, int leftBlocks, int rightBlocks, Direction facing, int color, int light) {
      List<Route> routes = routesAtStation(platformId);
      if (routes.size() <= 1) {
         this.drawGlassRoute(storedMatrixTransformations, platformId, state, leftBlocks, rightBlocks, facing, color, light);
      } else {
         float routeHeight = 1.0F - this.topPadding - this.bottomPadding;
         float width = (float)(leftBlocks + rightBlocks + 1) - this.sidePadding * 2.0F;
         int arrowDirection = IBlock.getStatePropertySafe(state, BlockPSDTop.ARROW_DIRECTION);
         boolean flip = arrowDirection == 2;
         float count = (float)routes.size();
         float rowHeight = routeHeight / count;

         for(int i = 0; i < routes.size(); ++i) {
            long routeId = ((Route)routes.get(i)).id;
            float y0 = this.topPadding + (float)i * rowHeight;
            float aspectRatio = width / rowHeight;
            String key = PSDTextureKeys.perRoute(platformId, routeId, aspectRatio, flip, refreshSignature(platformId));
            PSDTextureCache.Entry texture = PSDTextureCache.get(key, () -> {
               RouteMapGenerator.setConstants();
               return RouteMapGenerator.generateRouteMap(platformId, routeId, false, flip, aspectRatio, true);
            });
            if (texture.ready) {
               RenderTrains.scheduleRender(texture.identifier, false, QueuedRenderLayer.EXTERIOR, (matrices, vertexConsumer) -> {
                  storedMatrixTransformations.transform(matrices);
                  IDrawing.drawTexture(matrices, vertexConsumer, leftBlocks == 0 ? this.sidePadding : 0.0F, y0, 0.0F, 1.0F - (rightBlocks == 0 ? this.sidePadding : 0.0F), y0 + rowHeight, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, facing.getOpposite(), color, light);
                  matrices.popPose();
               });
            }
         }

      }
   }

   private void drawGroovedScreen(StoredMatrixTransformations storedMatrixTransformations, long platformId, BlockState state, int leftBlocks, int rightBlocks, Direction facing, int color, int light, int routeIndex) {
      float width = (float)(leftBlocks + rightBlocks + 1) - this.sidePadding * 2.0F;
      float screenHeight = 0.818F;
      RenderTrains.scheduleRender(WHITE_TEXTURE, false, QueuedRenderLayer.LIGHT, (matrices, vertexConsumer) -> {
         storedMatrixTransformations.transform(matrices);
         IDrawing.drawTexture(matrices, vertexConsumer, leftBlocks == 0 ? this.sidePadding : 0.0F, 0.056F, 0.1F, 1.0F - (rightBlocks == 0 ? this.sidePadding : 0.0F), 0.874F, 0.1F, 0.0F, 0.0F, 1.0F, 1.0F, facing, -1, light);
         matrices.popPose();
      });
      List<PSDScheduleCache.Row> scheduleRows = PSDScheduleCache.rows(platformId);
      long pageRouteId = this.getRouteIdByIndex(platformId, routeIndex);
      Route directionRoute = !scheduleRows.isEmpty() ? ((PSDScheduleCache.Row)scheduleRows.get(0)).route : (pageRouteId >= 0L && ClientData.DATA_CACHE != null ? (Route)ClientData.DATA_CACHE.routeIdMap.get(pageRouteId) : null);
      boolean routeDirectionLeft = RouteMapGenerator.detectRouteDirectionLeft(directionRoute, platformId);
      boolean leftDirection = routeDirectionLeft ^ PSDCustomText.readDirectionFlip(this.lastWorld, this.lastPos);
      if (!scheduleRows.isEmpty() || pageRouteId != -1L) {
         float aspectRatio = width / 0.818F;
         String customText = this.getCustomText();
         String customImagePath = this.getCustomImagePath();
         String scheduleKey = PSDScheduleCache.revision(platformId);
         String key = PSDTextureKeys.grooved(platformId, scheduleKey, leftDirection, aspectRatio, refreshSignature(platformId), customText, customImagePath, PSDCustomText.imageVersion(customImagePath), pageRouteId);
         PSDTextureCache.Entry routeMap = PSDTextureCache.get(key, () -> {
            RouteMapGenerator.setConstants();
            return RouteMapGenerator.generateCustomSingleRouteMap(platformId, scheduleRows, pageRouteId, false, leftDirection, aspectRatio, true, key, customText, customImagePath);
         });
         if (routeMap.ready) {
            RenderTrains.scheduleRender(routeMap.identifier, false, QueuedRenderLayer.EXTERIOR, (matrices, vertexConsumer) -> {
               storedMatrixTransformations.transform(matrices);
               IDrawing.drawTexture(matrices, vertexConsumer, leftBlocks == 0 ? this.sidePadding : 0.0F, 0.056F, 0.08F, 1.0F - (rightBlocks == 0 ? this.sidePadding : 0.0F), 0.874F, 0.08F, ((float)leftBlocks - (leftBlocks == 0 ? 0.0F : this.sidePadding)) / width, 0.0F, (width - (float)rightBlocks + (rightBlocks == 0 ? 0.0F : this.sidePadding)) / width, 1.0F, facing.getOpposite(), -1, light);
               matrices.popPose();
            });
         }

      }
   }

   private void drawGroovedColorStrip(StoredMatrixTransformations storedMatrixTransformations, long platformId, int leftBlocks, int rightBlocks, Direction facing, int light) {
      ClientCache.DynamicResource colorStrip = colorStripOf(platformId);
      if (colorStrip != null && colorStrip.width == 1) {
         this.drawColorStripQuad(storedMatrixTransformations, colorStrip, leftBlocks, rightBlocks, facing, light, 0.90625F, 0.9375F);
      }

   }

   private void drawColorStrip(StoredMatrixTransformations storedMatrixTransformations, long platformId, int leftBlocks, int rightBlocks, Direction facing, int light) {
      ClientCache.DynamicResource colorStrip = colorStripOf(platformId);
      if (colorStrip != null && colorStrip.width == 1) {
         this.drawColorStripQuad(storedMatrixTransformations, colorStrip, leftBlocks, rightBlocks, facing, light, 0.90625F, 0.9375F);
      }

   }

   private void drawColorStripQuad(StoredMatrixTransformations storedMatrixTransformations, ClientCache.DynamicResource colorStrip, int leftBlocks, int rightBlocks, Direction facing, int light, float v1, float v2) {
      RenderTrains.scheduleRender(colorStrip.resourceLocation, false, QueuedRenderLayer.EXTERIOR, (matrices, vertexConsumer) -> {
         storedMatrixTransformations.transform(matrices);
         IDrawing.drawTexture(matrices, vertexConsumer, leftBlocks == 0 ? this.sidePadding : 0.0F, v1, 0.0F, 1.0F - (rightBlocks == 0 ? this.sidePadding : 0.0F), v2, 0.0F, facing, -1, light);
         matrices.popPose();
      });
   }

   private void drawLedPanel(StoredMatrixTransformations storedMatrixTransformations, long platformId, BlockState state, int leftBlocks, int rightBlocks, Direction facing, int light, BlockPos currentPos) {
      StoredMatrixTransformations ledTransformations = storedMatrixTransformations.copy();
      ledTransformations.add((matrices) -> matrices.translate(0.0D, 0.0D, (double)0.06F));
      float columnWidth = (float)(1 + rightBlocks) * 90.0F / 3.0F;
      float columnWidthNormalised = columnWidth / 90.0F;
      PSDTopTextureGenerator.PanelInfo panel = PSDTopTextureGenerator.getPanelInfo(platformId);
      boolean hasRoute = panel != null && !panel.badgeText.isEmpty();
      int routeColor = hasRoute ? panel.routeColor : 0;
      String destinationText = panel == null ? "" : panel.destinationText;
      int carCount = panel == null ? 0 : panel.carCount;
      boolean infoPage = System.currentTimeMillis() / 5000L % 2L == 1L;
      String weatherTimeText = infoPage ? weatherTimeText() : "";
      long arrivalRemainingMillis = PSDScheduleCache.arrivalRemainingMillis(platformId);
      boolean arriving = arrivalRemainingMillis <= 60000L;
      boolean stopped = arrivalRemainingMillis <= 5000L;
      int textColor = arriving ? -16718218 : -16777216;
      float nearbyDoorValue = this.nearbyDoorValue(currentPos, state, facing);
      boolean doorOpen = nearbyDoorValue > 0.1F;
      this.panelDiag(currentPos, platformId, hasRoute, panel, infoPage, weatherTimeText, doorOpen, arriving, stopped, arrivalRemainingMillis);
      float innerPanelHeight = 0.18F;
      float innerY1 = 0.7F;
      float innerY2 = 0.88F;
      RenderTrains.scheduleRender(WHITE_TEXTURE, false, QueuedRenderLayer.LIGHT, (matrices, vertexConsumer) -> {
         ledTransformations.transform(matrices);
         IDrawing.drawTexture(matrices, vertexConsumer, 0.0F, 0.68F, 0.02F, 1.0F, 0.9F, 0.02F, 0.0F, 0.0F, 1.0F, 1.0F, facing, -1, light);
         matrices.popPose();
      });
      if (leftBlocks == 0 && hasRoute && !stopped) {
         float badgeTextHeight = 0.08888889F;
         float badgeCenterX = columnWidthNormalised / 2.0F;
         PSDTextureCache.Entry badgeText = this.getLedTextTexture(panel.badgeText, -1);
         if (badgeText.ready) {
            float badgeNaturalWidth = 0.08888889F * ((float)badgeText.width / (float)badgeText.height);
            float badgeTextWidth = Math.min(badgeNaturalWidth, columnWidthNormalised);
            float badgeHalfW = badgeTextWidth / 2.0F + 0.03F;
            float badgeX1 = Math.max(badgeCenterX - badgeHalfW, 0.0F);
            float badgeX2 = Math.min(badgeCenterX + badgeHalfW, 1.0F);
            RenderTrains.scheduleRender(WHITE_TEXTURE, false, QueuedRenderLayer.EXTERIOR, (matrices, vertexConsumer) -> {
               ledTransformations.transform(matrices);
               IDrawing.drawTexture(matrices, vertexConsumer, badgeX1, 0.7F, 0.01F, badgeX2, 0.88F, 0.01F, 0.0F, 0.0F, 1.0F, 1.0F, facing, routeColor, light);
               matrices.popPose();
            });
            this.drawPanelColumnText(ledTransformations, panel.badgeText, -1, badgeCenterX, 0.7F, 0.88F, columnWidthNormalised, 0.49382716F, facing, light);
         }
      }

      if (stopped) {
         if (leftBlocks == 0 && (nearbyDoorValue >= 0.0F || isDoubleTopModule(state.getBlock()))) {
            String stoppedMessage;
            int stoppedMessageColor;
            if (nearbyDoorValue >= 0.0F) {
               stoppedMessage = doorOpen ? "\u8f66\u95e8\u5f00\u542f\uff0c\u8bf7\u6ce8\u610f\u5b89\u5168" : "\u8f66\u95e8\u5173\u95ed\uff0c\u8bf7\u52ff\u501a\u9760\u8f66\u95e8";
               stoppedMessageColor = doorOpen ? -16718218 : -65536;
            } else {
               stoppedMessage = "\u8f66\u8f86\u8fdb\u7ad9\uff0c\u8bf7\u6ce8\u610f\u5b89\u5168";
               stoppedMessageColor = -23296;
            }

            this.drawPanelColumnText(ledTransformations, stoppedMessage, stoppedMessageColor, columnWidthNormalised * 1.5F, 0.7F, 0.88F, 2.0F, 0.49382716F, facing, light);
         }
      } else if (leftBlocks == 0) {
         if (infoPage) {
            this.drawPanelColumnText(ledTransformations, "\u8bf7\u6ce8\u610f\u5b89\u5168", -16777216, columnWidthNormalised * 1.5F, 0.7F, 0.88F, columnWidthNormalised, 0.49382716F, facing, light);
            if (!weatherTimeText.isEmpty()) {
               this.drawPanelColumnText(ledTransformations, weatherTimeText, -16777216, columnWidthNormalised * 2.5F, 0.7F, 0.88F, columnWidthNormalised, 0.49382716F, facing, light);
            }
         } else {
            if (!destinationText.isEmpty()) {
               this.drawPanelColumnText(ledTransformations, destinationText, textColor, columnWidthNormalised * 1.5F, 0.7F, 0.88F, columnWidthNormalised, 0.49382716F, facing, light);
            }

            if (carCount > 0) {
               this.drawPanelColumnText(ledTransformations, carCount + "\u8282", -16777216, columnWidthNormalised * 2.5F, 0.7F, 0.88F, columnWidthNormalised, 0.49382716F, facing, light);
            }
         }
      }

   }

   private float nearbyDoorValue(BlockPos currentPos, BlockState state, Direction facing) {
      if (this.lastWorld != null && currentPos != null) {
         BlockState doorStateBelow = this.lastWorld.getBlockState(currentPos.below());
         if (doorStateBelow.getBlock() instanceof BlockPSDAPGDoorBase && IBlock.getStatePropertySafe(doorStateBelow, IBlock.SIDE) == EnumSide.LEFT) {
            return this.isDoorOpenAt(currentPos.below()) ? 1.0F : 0.0F;
         } else {
            return isDoubleTopModule(state.getBlock()) ? this.findNearbyDoorValue(this.lastWorld, currentPos, facing.getOpposite()) : -1.0F;
         }
      } else {
         return -1.0F;
      }
   }

   private boolean isDoorOpenAt(BlockPos doorPos) {
      if (this.lastWorld != null && doorPos != null) {
         BlockEntity doorBE = this.lastWorld.getBlockEntity(doorPos);
         return !(doorBE instanceof BlockPSDAPGDoorBase.TileEntityPSDAPGDoorBase) ? false : ((BlockPSDAPGDoorBase.TileEntityPSDAPGDoorBase)doorBE).isOpen();
      } else {
         return false;
      }
   }

   private float findNearbyDoorValue(Level world, BlockPos pos, Direction facing) {
      BlockPos[] scan = new BlockPos[]{pos.below(), pos.above(), pos.relative(facing.getClockWise()), pos.relative(facing.getClockWise()).below(), pos.relative(facing.getCounterClockWise()), pos.relative(facing.getCounterClockWise()).below()};

      for(BlockPos scanPos : scan) {
         if (this.isDoorOpenAt(scanPos)) {
            return 1.0F;
         }

         BlockEntity blockEntity = world.getBlockEntity(scanPos);
         if (blockEntity instanceof BlockPSDAPGDoorBase.TileEntityPSDAPGDoorBase) {
            return 0.0F;
         }
      }

      return -1.0F;
   }

   private static String weatherTimeText() {
      String weather;
      try {
         ClientLevel world = Minecraft.getInstance().level;
         if (world == null) {
            weather = "\u6674";
         } else if (world.isThundering()) {
            weather = "\u96f7";
         } else if (world.isRaining()) {
            weather = "\u96e8";
         } else {
            weather = "\u6674";
         }
      } catch (Throwable var2) {
         return "";
      }

      LocalDateTime now = LocalDateTime.now();
      return weather + " " + String.format("%02d:%02d", now.getHour(), now.getMinute());
   }

   private boolean drawPanelColumnText(StoredMatrixTransformations ledTransformations, String text, int textColor, float centerX, float innerY1, float innerY2, float maxWidth, float sizeScale, Direction facing, int light) {
      PSDTextureCache.Entry entry = this.getLedTextTexture(text, textColor);
      if (!entry.ready) {
         return false;
      } else {
         float centerY = (innerY1 + innerY2) / 2.0F;
         float textHeight = (innerY2 - innerY1) * sizeScale;
         float naturalWidth = textHeight * ((float)entry.width / (float)entry.height);
         float textWidth = Math.min(naturalWidth, maxWidth);
         float x1 = centerX - textWidth / 2.0F;
         float x2 = centerX + textWidth / 2.0F;
         float y1 = centerY - textHeight / 2.0F;
         float y2 = centerY + textHeight / 2.0F;
         RenderTrains.scheduleRender(entry.identifier, false, QueuedRenderLayer.EXTERIOR, (matrices, vertexConsumer) -> {
            ledTransformations.transform(matrices);
            IDrawing.drawTexture(matrices, vertexConsumer, x1, y1, 0.0F, x2, y2, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, facing, -1, light);
            matrices.popPose();
         });
         return true;
      }
   }

   private void drawNextLineRows(StoredMatrixTransformations storedMatrixTransformations, long platformId, Direction facing, int rightBlocks, int light) {
      PSDTopTextureGenerator.LcdInfoMulti info = PSDTopTextureGenerator.getLcdInfoMulti(platformId);
      if (info != null && !info.nextLines.isEmpty()) {
         List<PSDTopTextureGenerator.LcdNextLine> nextLines = info.nextLines;
         this.nextLineDiag(this.currentPosOf(facing, 0), platformId, nextLines);
         float lineHeight = 0.1F;
         float badgeHeight = 0.065F;
         float badgeWidth = 0.11F;
         float leftX = 0.05F;
         float startY = 0.605F;
         RenderTrains.scheduleRender(WHITE_TEXTURE, false, QueuedRenderLayer.EXTERIOR, (matrices, vertexConsumer) -> {
            storedMatrixTransformations.transform(matrices);

            for(int i = 0; i < nextLines.size(); ++i) {
               PSDTopTextureGenerator.LcdNextLine line = (PSDTopTextureGenerator.LcdNextLine)nextLines.get(i);
               float x = i / 2 == 0 ? 0.05F : 1.0F;
               float y = 0.605F - (float)(i % 2) * 0.1F;
               IDrawing.drawTexture(matrices, vertexConsumer, x, y, 0.0F, x + 0.11F, y + 0.065F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, facing, line.routeColor, light);
            }

            matrices.popPose();
         });

         for(int i = 0; i < nextLines.size(); ++i) {
            PSDTopTextureGenerator.LcdNextLine line = (PSDTopTextureGenerator.LcdNextLine)nextLines.get(i);
            float x = i / 2 == 0 ? 0.05F : 1.0F;
            float y = 0.605F - (float)(i % 2) * 0.1F;
            float textX = x + 0.11F + 0.03F;
            String text = PSDTopTextureGenerator.cjkOnly(line.routeName) + (line.nextStation.isEmpty() ? "" : "\u4e0b\u4e00\u7ad9\uff1a" + PSDTopTextureGenerator.cjkOnly(line.nextStation));
            if (!text.isEmpty()) {
               PSDTextureCache.Entry texture = this.getLedTextTexture(text, -16777216);
               if (texture.ready) {
                  float textHeight = 0.065F;
                  float textWidth = Math.min(0.065F * ((float)texture.width / (float)texture.height), Math.max(0.0F, 1.0F - textX));
                  RenderTrains.scheduleRender(texture.identifier, false, QueuedRenderLayer.EXTERIOR, (matrices, vertexConsumer) -> {
                     storedMatrixTransformations.transform(matrices);
                     IDrawing.drawTexture(matrices, vertexConsumer, textX, y, 0.0F, textX + textWidth, y + 0.065F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, facing, -1, light);
                     matrices.popPose();
                  });
               }
            }
         }

      }
   }

   private void drawLcdPanel(StoredMatrixTransformations storedMatrixTransformations, long platformId, BlockState state, Direction facing, int rightBlocks, int light) {
      PSDTopTextureGenerator.LcdInfoMulti infoMulti = PSDTopTextureGenerator.getLcdInfoMulti(platformId);
      String routeDerivedStation = infoMulti != null && infoMulti.currentStation != null ? infoMulti.currentStation : "";
      String stationName = routeDerivedStation.isEmpty() ? PSDTopTextureGenerator.stationNameOfPlatform(platformId) : routeDerivedStation;
      String stationCjk = PSDTopTextureGenerator.cjkOnly(stationName);
      float arrowY1 = 0.1875F;
      float arrowY2 = 0.625F;
      float panelHeight = 0.4375F;
      float centerX = (float)(1 + rightBlocks) * 90.0F / 2.0F;
      boolean compositeReady = false;
      if (!stationCjk.isEmpty()) {
         float cjkHeight = 0.13125001F;
         float compositeHeight = 0.65625006F;
         float stationAspect = (1.0F + (float)rightBlocks) / 0.65625006F;
         PSDTextureCache.Entry composite = this.getStationCompositeEntry(stationName, stationAspect);
         compositeReady = composite.ready;
         if (composite.ready) {
            float compositeWidth = 0.65625006F * ((float)composite.width / (float)composite.height);
            float compositeCenterY = 0.2925F;
            float compositeY = -0.03562504F;
            float x1 = centerX - compositeWidth * 90.0F / 2.0F;
            RenderTrains.scheduleRender(composite.identifier, false, QueuedRenderLayer.EXTERIOR, (matrices, vertexConsumer) -> {
               storedMatrixTransformations.transform(matrices);
               matrices.scale(0.011111111F, 0.011111111F, 0.011111111F);
               IDrawing.drawTexture(matrices, vertexConsumer, x1, -3.2062535F, 0.0F, x1 + compositeWidth * 90.0F, 55.85625F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, facing, -1, light);
               matrices.popPose();
            });
         }
      }

      boolean drawPlaques = infoMulti != null && !infoMulti.nextLines.isEmpty() && !(state.getBlock() instanceof MyPSDTopLcd15) && !(state.getBlock() instanceof MyPSDTopLcd16);
      this.lcdPanelDiag(this.currentPosOf(facing, 0), platformId, state, stationName, compositeReady, drawPlaques ? infoMulti.nextLines : null);
      if (drawPlaques) {
         List<PSDTopTextureGenerator.LcdNextLine> nextLines = infoMulti.nextLines;
         float lineHeight = 0.1F;
         float badgeHeight = 0.065F;
         float badgeWidth = 0.11F;
         float leftX = 0.05F;
         float startY = 0.605F;
         RenderTrains.scheduleRender(WHITE_TEXTURE, false, QueuedRenderLayer.EXTERIOR, (matrices, vertexConsumer) -> {
            storedMatrixTransformations.transform(matrices);

            for(int i = 0; i < nextLines.size(); ++i) {
               PSDTopTextureGenerator.LcdNextLine line = (PSDTopTextureGenerator.LcdNextLine)nextLines.get(i);
               float x = i / 2 == 0 ? 0.05F : 1.0F;
               float y = 0.605F - (float)(i % 2) * 0.1F;
               IDrawing.drawTexture(matrices, vertexConsumer, x, y, 0.0F, x + 0.11F, y + 0.065F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, facing, line.routeColor, light);
            }

            matrices.popPose();
         });

         for(int i = 0; i < nextLines.size(); ++i) {
            PSDTopTextureGenerator.LcdNextLine line = (PSDTopTextureGenerator.LcdNextLine)nextLines.get(i);
            float x = i / 2 == 0 ? 0.05F : 1.0F;
            float y = 0.605F - (float)(i % 2) * 0.1F;
            float textX = x + 0.11F + 0.03F;
            String text = PSDTopTextureGenerator.cjkOnly(line.routeName) + (line.nextStation.isEmpty() ? "" : "\u4e0b\u4e00\u7ad9\uff1a" + PSDTopTextureGenerator.cjkOnly(line.nextStation));
            if (!text.isEmpty()) {
               PSDTextureCache.Entry texture = this.getLedTextTexture(text, -16777216);
               if (texture.ready) {
                  float textHeight = 0.065F;
                  float textWidth = Math.min(0.065F * ((float)texture.width / (float)texture.height), Math.max(0.0F, 1.0F - textX));
                  RenderTrains.scheduleRender(texture.identifier, false, QueuedRenderLayer.EXTERIOR, (matrices, vertexConsumer) -> {
                     storedMatrixTransformations.transform(matrices);
                     IDrawing.drawTexture(matrices, vertexConsumer, textX, y, 0.0F, textX + textWidth, y + 0.065F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, facing, -1, light);
                     matrices.popPose();
                  });
               }
            }
         }

      }
   }

   private PSDTextureCache.Entry getLedTextTexture(String text, int textColor) {
      String safeText = text == null ? "" : text;
      String key = PSDTextureKeys.ledText(safeText, textColor);
      return PSDTextureCache.get(key, () -> {
         PSDTopTextureGenerator.setConstants();
         return PSDTopTextureGenerator.generatePixelatedText(safeText, textColor, 0.0D, false);
      });
   }

   private PSDTextureCache.Entry getRouteBadgeTexture(String routeName, int routeColor) {
      String key = PSDTextureKeys.routeBadge(routeName, routeColor);
      return PSDTextureCache.get(key, () -> {
         PSDTopTextureGenerator.setConstants();
         return PSDTopTextureGenerator.generateRouteBadge(routeName, routeColor);
      });
   }

   private PSDTextureCache.Entry getStationCompositeEntry(String stationName, float aspectRatio) {
      String key = PSDTextureKeys.mtrStation(stationName, aspectRatio);
      return PSDTextureCache.get(key, () -> {
         PSDTopTextureGenerator.setConstants();
         return PSDTopTextureGenerator.getMTRStationCompositeImage(stationName, aspectRatio);
      });
   }

   private String getCustomImagePath() {
      if (this.lastWorld != null && this.lastPos != null) {
         if (PSDCustomText.hasServerStore()) {
            return PSDCustomText.serverStoreImagePathValue();
         } else {
            String selected = PSDCustomText.getSelectedLibraryImage();
            if (selected != null && !selected.isEmpty()) {
               return selected;
            } else if (PSDCustomText.isLibrarySelectionCleared()) {
               return "";
            } else {
               String fromLibrary = PSDCustomText.findFirstLibraryImage();
               return fromLibrary != null && !fromLibrary.isEmpty() ? fromLibrary : PSDCustomText.readImage(this.lastWorld, this.lastPos);
            }
         }
      } else {
         return "";
      }
   }

   private String getCustomText() {
      return this.lastWorld != null && this.lastPos != null ? PSDCustomText.read(this.lastWorld, this.lastPos) : "\u5730\u94c1\u8f68\u4ea4";
   }

   private PSDTextureCache.Entry getOwnArrowTexture(long platformId, boolean hasLeft, boolean hasRight, float aspectRatio, boolean showPlatform) {
      String signature = RouteMapGenerator.arrowContentSignature(platformId);
      String key = PSDTextureKeys.platformArrow(platformId, hasLeft, hasRight, showPlatform, aspectRatio, signature);
      return PSDTextureCache.get(key, () -> {
         PSDTopTextureGenerator.setConstants();
         com.mojang.blaze3d.platform.NativeImage image = PSDTopTextureGenerator.generateDirectionArrow(platformId, hasLeft, hasRight, HorizontalAlignment.CENTER, true, 0.25F, aspectRatio, -1, -16777216, -1, showPlatform);
         if (image != null && DUMP_TEXTURES) {
            PSDTopTextureGenerator.dumpImage(image, "arrow_p" + platformId + "_" + image.getWidth() + "x" + image.getHeight());
         }

         return image;
      });
   }

   private static ClientCache.DynamicResource colorStripOf(long platformId) {
      try {
         return ClientData.DATA_CACHE == null ? null : ClientData.DATA_CACHE.getColorStrip(platformId);
      } catch (Throwable var3) {
         return null;
      }
   }

   private static String refreshSignature(long platformId) {
      StringBuilder stringBuilder = new StringBuilder();

      try {
         for(Route route : RouteMapGenerator.routesAtPlatform(platformId)) {
            stringBuilder.append(route.id).append(':').append(route.name).append(':').append(route.color).append(';');
         }

         stringBuilder.append(PSDTopTextureGenerator.stationNameOfPlatform(platformId));
      } catch (Throwable var5) {
      }

      return stringBuilder.toString();
   }

   private static boolean isStandaloneModule(Block block) {
      return block instanceof IStandaloneTopModule;
   }

   private static boolean isDoubleTopModule(Block block) {
      return isStandaloneModule(block) && !(block instanceof MyPSDTopLcd12);
   }

   private int getLcd14RouteIndex(BlockState state) {
      if (this.lastWorld != null && this.lastPos != null) {
         BlockEntity blockEntity = this.lastWorld.getBlockEntity(this.lastPos);
         return blockEntity instanceof MyPSDTopLcd14BE ? ((MyPSDTopLcd14BE)blockEntity).getRouteIndex() : 0;
      } else {
         return 0;
      }
   }

   private long getRouteIdByIndex(long platformId, int index) {
      List<Long> routeIds = RouteMapGenerator.collectRouteIdsForStation(platformId);
      if (routeIds.isEmpty()) {
         return -1L;
      } else {
         int n = index % routeIds.size();
         if (n < 0) {
            n += routeIds.size();
         }

         return routeIds.get(n);
      }
   }

   private static List<Route> routesAtStation(long platformId) {
      long stationId = RouteMapGenerator.getStationIdForPlatform(platformId);
      List<Route> routes = new ArrayList();

      for(Route route : RouteMapGenerator.routesAtPlatform(platformId)) {
         int index = stationId >= 0L ? RouteMapGenerator.getStationIndexOnRoute(route, stationId, platformId) : route.getPlatformIdIndex(platformId);
         if (index >= 0) {
            routes.add(route);
         }
      }

      return routes;
   }

   private BlockPos currentPosOf(Direction facing, int leftBlocks) {
      return this.lastWorld != null && this.lastPos != null ? this.lastPos.relative(facing.getOpposite().getClockWise(), leftBlocks) : null;
   }

   private static final java.util.Map<String, Long> DIAG_LAST = new java.util.concurrent.ConcurrentHashMap<>();

   /** Rate-limited diagnostic used while chasing a rendering seams report. */
   private static void diag(String key, String message) {
      long now = System.currentTimeMillis();
      Long last = DIAG_LAST.get(key);
      if (last != null && now - last < 2000L) {
         return;
      }

      DIAG_LAST.put(key, now);
      com.mtrpsdlcd.Diag.log("[diag] " + message);
   }

   private void panelDiag(BlockPos currentPos, long platformId, boolean hasRoute, PSDTopTextureGenerator.PanelInfo panel, boolean infoPage, String weatherTimeText, boolean doorOpen, boolean arriving, boolean stopped, long arrivalRemainingMillis) {
      diag("panel", "panel pos=" + currentPos + " platform=" + platformId + " hasRoute=" + hasRoute
            + " badge=" + (panel == null ? "-" : panel.badgeText) + " dest=" + (panel == null ? "-" : panel.destinationText)
            + " cars=" + (panel == null ? 0 : panel.carCount) + " infoPage=" + infoPage
            + " doorOpen=" + doorOpen + " stopped=" + stopped + " arriveIn=" + arrivalRemainingMillis);
   }

   private void arrowDiag(BlockPos currentPos, long platformId, boolean hasLeft, boolean hasRight, boolean showPlatform, boolean lcd45, boolean lcd67, PSDTextureCache.Entry arrow) {
      diag("arrow", "arrow pos=" + currentPos + " platform=" + platformId + " left=" + hasLeft + " right=" + hasRight
            + " showPlatform=" + showPlatform + " lcd45=" + lcd45 + " lcd67=" + lcd67
            + " tex=" + (arrow == null ? "-" : arrow.width + "x" + arrow.height));
   }

   private void lcdPanelDiag(net.minecraft.core.BlockPos currentPos, long platformId, net.minecraft.world.level.block.state.BlockState state, String stationName, boolean compositeReady, List<PSDTopTextureGenerator.LcdNextLine> nextLines) {
      diag("lcdpanel", "lcdPanel pos=" + currentPos + " block=" + idPath(state) + " platform=" + platformId
            + " station=" + stationName + " composite=" + compositeReady
            + " rows=" + (nextLines == null ? 0 : nextLines.size()) + " colors=" + plaqueColors(nextLines));
   }

   private void nextLineDiag(net.minecraft.core.BlockPos currentPos, long platformId, List<PSDTopTextureGenerator.LcdNextLine> nextLines) {
      diag("nextline", "nextLine pos=" + currentPos + " platform=" + platformId
            + " rows=" + (nextLines == null ? 0 : nextLines.size()) + " colors=" + plaqueColors(nextLines));
   }

   private static String plaqueColors(List<PSDTopTextureGenerator.LcdNextLine> nextLines) {
      if (nextLines != null && !nextLines.isEmpty()) {
         StringBuilder stringBuilder = new StringBuilder();

         for(int i = 0; i < nextLines.size(); ++i) {
            if (i > 0) {
               stringBuilder.append(',');
            }

            stringBuilder.append(Integer.toHexString(((PSDTopTextureGenerator.LcdNextLine)nextLines.get(i)).routeColor));
         }

         return stringBuilder.toString();
      } else {
         return "none";
      }
   }

   private void bandDiag(BlockState state, BlockPos currentPos, Block currentBlockBelow, boolean standaloneModule, boolean lcd45, boolean lcd67) {
      diag("band", "band pos=" + currentPos + " block=" + idPath(state)
            + " below=" + (currentBlockBelow == null ? "-" : BuiltInRegistries.BLOCK.getKey(currentBlockBelow))
            + " standalone=" + standaloneModule + " lcd45=" + lcd45 + " lcd67=" + lcd67);
   }

   private void branchDiag(BlockPos pos, BlockState state) {
      diag("branch", "branch pos=" + pos + " block=" + idPath(state)
            + " persistent=" + IBlock.getStatePropertySafe(state, BlockPSDTop.PERSISTENT)
            + " side=" + IBlock.getStatePropertySafe(state, BlockPSDTop.SIDE)
            + " airL=" + IBlock.getStatePropertySafe(state, BlockPSDTop.AIR_LEFT)
            + " airR=" + IBlock.getStatePropertySafe(state, BlockPSDTop.AIR_RIGHT)
            + " facing=" + IBlock.getStatePropertySafe(state, BlockPSDTop.FACING)
            + " renderType=" + this.lastRenderType);
   }

   private static String idPath(BlockState state) {
      ResourceLocation identifier = BuiltInRegistries.BLOCK.getKey(state.getBlock());
      return identifier == null ? "unknown" : identifier.getPath();
   }
}
