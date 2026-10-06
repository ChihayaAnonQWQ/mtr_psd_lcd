package com.mtrpsdlcd.render;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.NativeImage.Format;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import mtr.client.ClientCache;
import mtr.client.ClientData;
import mtr.client.Config;
import mtr.data.IGui;
import mtr.data.Platform;
import mtr.data.Route;
import mtr.data.Station;
import mtr.data.IGui.HorizontalAlignment;
import mtr.data.IGui.VerticalAlignment;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

public final class PSDTopTextureGenerator {
   private static int scale;
   private static int lineSize;
   private static int lineSpacing;
   private static int fontSizeBig;
   private static int fontSizeSmall;
   public static final int PIXEL_SCALE = 4;
   public static final int BAKE_SUPERSAMPLE_BADGE = 4;
   private static final int MIN_VERTICAL_SIZE = 5;
   private static final String LOGO_RESOURCE = "textures/block/sign/logo.png";
   private static final String EXIT_RESOURCE = "textures/block/sign/exit_letter_blank.png";
   private static final String ARROW_RESOURCE = "textures/block/sign/arrow.png";
   private static final String CIRCLE_RESOURCE = "textures/block/sign/circle.png";
   public static final String PLATFORM_LABEL = "\u7ad9\u53f0";
   private static final int PIXEL_RESOLUTION = 24;

   private PSDTopTextureGenerator() {
   }

   public static void setConstants() {
      scale = (int)Math.pow(2.0D, (double)(Config.dynamicTextureResolution() + 5));
      lineSize = scale / 8;
      lineSpacing = lineSize * 3 / 2;
      fontSizeBig = lineSize * 2;
      fontSizeSmall = fontSizeBig / 2;
   }

   public static int getScale() {
      return scale;
   }

   public static NativeImage generatePixelatedText(String text, int textColor, double cjkSizeRatio, boolean fullPixel) {
      try {
         int sourceMaxWidth = PSDTextureKeys.textSourceWidth(text);
         int pixelScale = fullPixel ? 1 : 4;
         int[] dimensions = new int[2];
         byte[] pixels = getTextPixels(text, dimensions, sourceMaxWidth, Integer.MAX_VALUE, (int)Math.round(24.0D * (cjkSizeRatio > 0.0D ? cjkSizeRatio + 1.0D : 1.0D)), (int)Math.round(24.0D * (cjkSizeRatio < 0.0D ? 1.0D - cjkSizeRatio : 1.0D)), 0, HorizontalAlignment.CENTER);
         if (pixels != null && dimensions[0] > 0 && dimensions[1] > 0) {
            int width = Math.min(sourceMaxWidth, dimensions[0]) * pixelScale;
            int height = dimensions[1] * pixelScale;
            NativeImage nativeImage = new NativeImage(Format.RGBA, width, height, false);
            nativeImage.fillRect(0, 0, width, height, 0);
            drawStringPixelated(nativeImage, pixels, dimensions, textColor, fullPixel);
            return nativeImage;
         } else {
            return null;
         }
      } catch (Exception var12) {
         return null;
      }
   }

   public static NativeImage generateRouteSquare(int color, String routeName, IGui.HorizontalAlignment horizontalAlignment) {
      try {
         int padding = scale / 32;
         int[] dimensions = new int[2];
         byte[] pixels = getTextPixels(routeName, dimensions, Integer.MAX_VALUE, (int)((float)(fontSizeBig + fontSizeSmall) * 1.25F), fontSizeBig, fontSizeSmall, padding, horizontalAlignment);
         if (pixels != null && dimensions[0] > 0 && dimensions[1] > 0) {
            int width = dimensions[0] + padding * 2;
            int height = dimensions[1] + padding * 2;
            NativeImage nativeImage = new NativeImage(Format.RGBA, width, height, false);
            nativeImage.fillRect(0, 0, width, height, invertColor(-16777216 | color));
            drawString(nativeImage, pixels, width / 2, height / 2, dimensions, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, 0, -1, false);
            return nativeImage;
         } else {
            return null;
         }
      } catch (Exception var9) {
         return null;
      }
   }

   public static NativeImage generateRouteBadge(String routeName, int routeColor) {
      try {
         if (routeName != null && !routeName.isEmpty()) {
            int sample = 4;
            int padding = scale / 16 * 4;
            int height = scale / 6 * 4;
            int[] dimensions = new int[2];
            byte[] pixels = getTextPixels(routeName, dimensions, scale * 4, (int)((float)height * 1.25F), height * 3 / 5, height * 3 / 10, 0, HorizontalAlignment.CENTER);
            if (pixels != null && dimensions[0] > 0 && dimensions[1] > 0) {
               int width = Math.max(dimensions[0] + padding * 2, scale / 5 * 4);
               NativeImage nativeImage = new NativeImage(Format.RGBA, width, height, false);
               nativeImage.fillRect(0, 0, width, height, routeColor);
               drawString(nativeImage, pixels, width / 2, height / 2, dimensions, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, 0, -1, false);
               return nativeImage;
            } else {
               return null;
            }
         } else {
            return null;
         }
      } catch (Exception var9) {
         return null;
      }
   }

   public static NativeImage generateStationName(String stationName, float aspectRatio) {
      if (aspectRatio <= 0.0F) {
         return null;
      } else {
         try {
            int height = scale * 2;
            int width = Math.round((float)height * aspectRatio);
            int padding = scale / 16;
            int[] dimensions = new int[2];
            byte[] pixels = getTextPixels(stationName, dimensions, width - padding * 2, height - padding * 2, fontSizeBig * 2, fontSizeSmall * 2, padding, HorizontalAlignment.CENTER);
            if (pixels != null && dimensions[0] > 0 && dimensions[1] > 0) {
               NativeImage nativeImage = new NativeImage(Format.RGBA, width, height, false);
               nativeImage.fillRect(0, 0, width, height, 0);
               drawString(nativeImage, pixels, width / 2, height / 2, dimensions, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, 0, -1, false);
               return nativeImage;
            } else {
               return null;
            }
         } catch (Exception var8) {
            return null;
         }
      }
   }

   public static NativeImage getMTRStationCompositeImage(String stationName, float aspectRatio) {
      try {
         if (stationName != null && !stationName.isEmpty()) {
            int height = scale * 2;
            int width = Math.max(1, Math.round((float)height * aspectRatio));
            int padding = scale / 16;
            int[] dims = new int[2];
            byte[] pixels = getTextPixels(stationName, dims, width - padding * 2, height - padding * 2, fontSizeBig * 2, fontSizeSmall * 2, padding, HorizontalAlignment.CENTER);
            if (pixels != null && dims[0] > 0 && dims[1] > 0) {
               NativeImage img = new NativeImage(Format.RGBA, width, height, false);
               img.fillRect(0, 0, width, height, 0);
               drawString(img, pixels, width / 2, height / 2, dims, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, 0, -16777216, false);
               return img;
            } else {
               return null;
            }
         } else {
            return null;
         }
      } catch (Exception var8) {
         return null;
      }
   }

   public static NativeImage generateDirectionArrow(long platformId, boolean hasLeft, boolean hasRight, IGui.HorizontalAlignment horizontalAlignment, boolean showToString, float paddingScale, float aspectRatio, int backgroundColor, int textColor, int transparentColor, boolean showPlatform) {
      if (aspectRatio <= 0.0F) {
         return null;
      } else {
         try {
            List<String> destinations = new ArrayList();
            List<Integer> colors = RouteMapGenerator.getRouteStream(platformId, (route, currentStationIndex) -> destinations.add(RouteMapGenerator.formattedDestinationWithMarker(route, currentStationIndex)));
            boolean isTerminating = destinations.isEmpty();
            boolean leftToRight = horizontalAlignment == HorizontalAlignment.CENTER ? hasLeft || !hasRight : horizontalAlignment != HorizontalAlignment.RIGHT;
            int height = scale;
            int width = Math.round((float)height * aspectRatio);
            int padding = Math.round((float)height * paddingScale);
            int tileSize = height - padding * 2;
            int tilePadding = tileSize / 4;
            if (width > 0 && height > 0 && tileSize > 0) {
               NativeImage nativeImage = new NativeImage(Format.RGBA, width, height, false);
               nativeImage.fillRect(0, 0, width, height, invertColor(backgroundColor));
               int[] dimensionsLabel = new int[2];
               byte[] pixelsLabel = null;
               int labelShift = 0;
               int circleX;
               if (isTerminating) {
                  circleX = (int)horizontalAlignment.getOffset(0.0F, (float)(tileSize - width));
               } else {
                  String destinationString = IGui.mergeStations(destinations);
                  boolean isCircular = destinationString.startsWith("\u0001");
                  destinationString = destinationString.replace("\u0001", "");
                  if (!destinationString.isEmpty() && !isCircular && showToString) {
                     destinationString = IGui.insertTranslation("gui.mtr.to_cjk", "gui.mtr.to", 1, new String[]{destinationString});
                  }

                  int leftSize = ((hasLeft ? 1 : 0) + (showPlatform && leftToRight ? 1 : 0)) * (tileSize + tilePadding);
                  int rightSize = ((hasRight ? 1 : 0) + (showPlatform && !leftToRight ? 1 : 0)) * (tileSize + tilePadding);
                  int[] dimensionsDestination = new int[2];
                  byte[] pixelsDestination = getTextPixels(destinationString, dimensionsDestination, Math.max(1, width - leftSize - rightSize - padding * (showToString ? 2 : 1)), (int)((float)tileSize * 1.25F), tileSize * 3 / 5, tileSize * 3 / 10, tilePadding, leftToRight ? HorizontalAlignment.LEFT : HorizontalAlignment.RIGHT);
                  if (pixelsDestination == null || dimensionsDestination[0] <= 0 || dimensionsDestination[1] <= 0) {
                     return null;
                  }

                  dimensionsLabel = new int[2];
                  pixelsLabel = getTextPixels("\u7ad9\u53f0", dimensionsLabel, 4 * tileSize, (int)((float)tileSize * 1.25F * 3.0F / 4.0F), tileSize * 3 / 4, tileSize * 3 / 4, 0, HorizontalAlignment.LEFT);
                  labelShift = showPlatform && leftToRight ? dimensionsLabel[0] + tilePadding : 0;
                  int leftPadding = (int)horizontalAlignment.getOffset(0.0F, (float)(leftSize + rightSize + dimensionsDestination[0] + labelShift - tilePadding * 2 - width));
                  if (!leftToRight) {
                     leftPadding -= tileSize;
                  }

                  drawString(nativeImage, pixelsDestination, leftPadding + leftSize - tilePadding + labelShift, height / 2, dimensionsDestination, HorizontalAlignment.LEFT, VerticalAlignment.CENTER, backgroundColor, textColor, false);
                  if (hasLeft) {
                     drawResource(nativeImage, "textures/block/sign/arrow.png", leftPadding, padding, tileSize, tileSize, false, 0.0F, 1.0F, textColor, false);
                  }

                  if (hasRight) {
                     int arrowShift = showPlatform ? (leftToRight ? labelShift : dimensionsLabel[0] + tilePadding) : 0;
                     drawResource(nativeImage, "textures/block/sign/arrow.png", leftPadding + leftSize + dimensionsDestination[0] - tilePadding * 2 + rightSize - tileSize + arrowShift, padding, tileSize, tileSize, true, 0.0F, 1.0F, textColor, false);
                  }

                  circleX = leftPadding + leftSize + (leftToRight ? -tileSize - tilePadding : dimensionsDestination[0] - tilePadding);
               }

               if (showPlatform) {
                  for(int i = 0; i < colors.size(); ++i) {
                     drawResource(nativeImage, "textures/block/sign/circle.png", circleX, padding, tileSize, tileSize, false, (float)i / (float)colors.size(), ((float)i + 1.0F) / (float)colors.size(), colors.get(i), false);
                  }

                  Platform platform = ClientData.DATA_CACHE == null ? null : (Platform)ClientData.DATA_CACHE.platformIdMap.get(platformId);
                  if (platform != null && platform.name != null) {
                     int[] dimensionsPlatformNumber = new int[2];
                     byte[] pixelsPlatformNumber = getTextPixels(platform.name, dimensionsPlatformNumber, tileSize, (int)((float)tileSize * 1.25F * 3.0F / 4.0F), tileSize * 3 / 4, tileSize * 3 / 4, 0, HorizontalAlignment.CENTER);
                     if (pixelsPlatformNumber != null && dimensionsPlatformNumber[0] > 0 && dimensionsPlatformNumber[1] > 0) {
                        drawString(nativeImage, pixelsPlatformNumber, circleX + tileSize / 2, padding + tileSize / 2, dimensionsPlatformNumber, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, 0, -1, false);
                     }

                     if (pixelsLabel != null && dimensionsLabel[0] > 0 && dimensionsLabel[1] > 0) {
                        drawString(nativeImage, pixelsLabel, circleX + tileSize + tilePadding, padding + tileSize / 2, dimensionsLabel, HorizontalAlignment.LEFT, VerticalAlignment.CENTER, 0, -16777216, false);
                     }
                  }
               }

               if (transparentColor != 0) {
                  clearColor(nativeImage, invertColor(transparentColor));
               }

               return nativeImage;
            } else {
               return null;
            }
         } catch (Exception var34) {
            return null;
         }
      }
   }

   public static PSDTopTextureGenerator.LcdInfo getLcdInfo(long platformId) {
      try {
         for(Route route : RouteMapGenerator.routesAtPlatform(platformId)) {
            int index = route.getPlatformIdIndex(platformId);
            if (index >= 0 && index < route.platformIds.size() - 1 && route.name != null && !route.name.isEmpty()) {
               String currentStation = stationNameOf(route, index);
               String nextStation = stationNameOf(route, index + 1);
               return new PSDTopTextureGenerator.LcdInfo(currentStation, nextStation, route.name, RouteMapGenerator.routeColorArgb(route));
            }
         }

         return null;
      } catch (Exception var7) {
         return null;
      }
   }

   public static PSDTopTextureGenerator.LcdInfoMulti getLcdInfoMulti(long platformId) {
      try {
         List<PSDTopTextureGenerator.LcdNextLine> nextLines = new ArrayList();
         List<String> stationNames = new ArrayList();

         for(Route route : RouteMapGenerator.routesAtPlatform(platformId)) {
            int index = route.getPlatformIdIndex(platformId);
            if (index >= 0) {
               stationNames.add(stationNameOf(route, index));
               if (index + 1 < route.platformIds.size()) {
                  nextLines.add(new PSDTopTextureGenerator.LcdNextLine(route.name, RouteMapGenerator.routeColorArgb(route), stationNameOf(route, index + 1)));
               }
            }
         }

         String currentStation = stationNames.isEmpty() ? "" : (String)stationNames.get(0);
         return new PSDTopTextureGenerator.LcdInfoMulti(currentStation, nextLines);
      } catch (Exception var7) {
         return null;
      }
   }

   public static PSDTopTextureGenerator.PanelInfo getPanelInfo(long platformId) {
      try {
         List<PSDScheduleCache.Row> rows = PSDScheduleCache.rows(platformId);
         Route route = null;
         int scheduleStationIndex = -1;
         int carCount = 0;
         boolean fromSchedule = false;
         if (!rows.isEmpty()) {
            PSDScheduleCache.Row next = (PSDScheduleCache.Row)rows.get(0);
            route = next.route;
            scheduleStationIndex = next.currentStationIndex;
            carCount = next.trainCars;
            fromSchedule = true;
         }

         if (route == null) {
            List<Route> routes = RouteMapGenerator.routesAtPlatform(platformId);
            route = routes.isEmpty() ? null : (Route)routes.get(0);
         }

         if (route == null) {
            return null;
         } else {
            String name = cjkOnly(route.name);
            String badgeText = name.isEmpty() ? cjkOnly(route.lightRailRouteNumber) : name;
            int index = route.getPlatformIdIndex(platformId);
            if (index < 0) {
               index = scheduleStationIndex;
            }

            String destinationText = index < 0 ? "" : cjkOnly(RouteMapGenerator.getDestination(route, index));
            return new PSDTopTextureGenerator.PanelInfo(route, badgeText == null ? "" : badgeText, RouteMapGenerator.routeColorArgb(route), destinationText == null ? "" : destinationText, carCount, fromSchedule);
         }
      } catch (Throwable var11) {
         return null;
      }
   }

   public static String stationNameOf(Route route, int index) {
      return route != null && index >= 0 && index < route.platformIds.size() ? stationNameOfPlatform(((Route.RoutePlatform)route.platformIds.get(index)).platformId) : "";
   }

   public static String stationNameOfPlatform(long platformId) {
      ClientCache clientCache = ClientData.DATA_CACHE;
      if (clientCache == null) {
         return "";
      } else {
         Station station = (Station)clientCache.platformIdToStation.get(platformId);
         return station != null && station.name != null ? station.name : "";
      }
   }

   /** Writes a generated texture to <gamedir>/psd-debug so its pixel content can be inspected. */
   public static void dumpImage(NativeImage image, String name) {
      try {
         java.nio.file.Path dir = net.minecraftforge.fml.loading.FMLPaths.GAMEDIR.get().resolve("psd-debug");
         java.nio.file.Files.createDirectories(dir);
         java.nio.file.Path file = dir.resolve(name + ".png");
         image.writeToFile(file);
         System.out.println("[mtr_psd_lcd][dump] " + file.getFileName());
      } catch (Throwable t) {
         System.out.println("[mtr_psd_lcd][dump] failed " + name + ": " + t);
      }
   }

   public static byte[] getTextPixels(String text, int[] dimensions, int maxWidth, int maxHeight, int bigFontSize, int smallFontSize, int padding, IGui.HorizontalAlignment horizontalAlignment) {
      setConstants();
      ClientCache clientCache = ClientData.DATA_CACHE;
      return clientCache == null ? null : clientCache.getTextPixels(text == null ? "" : text, dimensions, maxWidth, maxHeight, bigFontSize, smallFontSize, padding, horizontalAlignment);
   }

   public static void drawString(NativeImage nativeImage, byte[] pixels, int x, int y, int[] textDimensions, IGui.HorizontalAlignment horizontalAlignment, IGui.VerticalAlignment verticalAlignment, int backgroundColor, int textColor, boolean rotate90) {
      if (pixels != null && textDimensions[0] > 0 && textDimensions[1] > 0 && pixels.length >= textDimensions[0] * textDimensions[1]) {
         if ((backgroundColor >> 24 & 255) > 0) {
            for(int drawX = 0; drawX < textDimensions[rotate90 ? 1 : 0]; ++drawX) {
               for(int drawY = 0; drawY < textDimensions[rotate90 ? 0 : 1]; ++drawY) {
                  drawPixelSafe(nativeImage, (int)horizontalAlignment.getOffset((float)(drawX + x), (float)textDimensions[rotate90 ? 1 : 0]), (int)verticalAlignment.getOffset((float)(drawY + y), (float)textDimensions[rotate90 ? 0 : 1]), backgroundColor);
               }
            }
         }

         int drawX = 0;
         int drawY = rotate90 ? textDimensions[0] - 1 : 0;

         for(int i = 0; i < textDimensions[0] * textDimensions[1]; ++i) {
            blendPixel(nativeImage, (int)horizontalAlignment.getOffset((float)(x + drawX), (float)textDimensions[rotate90 ? 1 : 0]), (int)verticalAlignment.getOffset((float)(y + drawY), (float)textDimensions[rotate90 ? 0 : 1]), ((pixels[i] & 255) << 24) + (textColor & 16777215));
            if (rotate90) {
               --drawY;
               if (drawY < 0) {
                  drawY = textDimensions[0] - 1;
                  ++drawX;
               }
            } else {
               ++drawX;
               if (drawX == textDimensions[0]) {
                  drawX = 0;
                  ++drawY;
               }
            }
         }

      }
   }

   private static void drawStringPixelated(NativeImage nativeImage, byte[] pixels, int[] textDimensions, int textColor, boolean fullPixel) {
      int pixelScale = fullPixel ? 1 : 4;
      int yOffset = (textDimensions[1] * pixelScale - nativeImage.getHeight()) / 2;
      int drawX = 0;
      int drawY = 0;

      for(int i = 0; i < textDimensions[0] * textDimensions[1]; ++i) {
         if ((pixels[i] & 255) > 127) {
            if (fullPixel) {
               drawPixelSafe(nativeImage, drawX, drawY - yOffset, textColor);
            } else {
               for(int j = 0; j < 4; ++j) {
                  for(int k = 0; k < 4; ++k) {
                     drawPixelSafe(nativeImage, drawX * 4 + j, drawY * 4 + k - yOffset, textColor);
                  }
               }
            }
         }

         ++drawX;
         if (drawX == textDimensions[0]) {
            drawX = 0;
            ++drawY;
         }
      }

   }

   private static void drawResource(NativeImage nativeImage, String resource, int x, int y, int width, int height, boolean flipX, float v1, float v2, int color, boolean useActualColor) {
      try {
         Minecraft minecraftClient = Minecraft.getInstance();
         if (minecraftClient == null) {
            return;
         }

         Resource minecraftResource = minecraftClient.getResourceManager().getResourceOrThrow(new ResourceLocation("mtr", resource));
         InputStream inputStream = minecraftResource.open();

         NativeImage nativeImageResource;
         try {
            nativeImageResource = NativeImage.read(Format.RGBA, inputStream);
         } catch (Throwable var38) {
            if (inputStream != null) {
               try {
                  inputStream.close();
               } catch (Throwable var37) {
                  var38.addSuppressed(var37);
               }
            }

            throw var38;
         }

         if (inputStream != null) {
            inputStream.close();
         }

         int resourceWidth = nativeImageResource.getWidth();
         int resourceHeight = nativeImageResource.getHeight();

         for(int drawX = 0; drawX < width; ++drawX) {
            for(int drawY = Math.round(v1 * (float)height); drawY < Math.round(v2 * (float)height); ++drawY) {
               float pixelX = (float)drawX / (float)width * (float)resourceWidth;
               float pixelY = (float)drawY / (float)height * (float)resourceHeight;
               int floorX = (int)pixelX;
               int floorY = (int)pixelY;
               int ceilX = floorX + 1;
               int ceilY = floorY + 1;
               float percentX1 = (float)ceilX - pixelX;
               float percentY1 = (float)ceilY - pixelY;
               float percentX2 = pixelX - (float)floorX;
               float percentY2 = pixelY - (float)floorY;
               int pixel1 = nativeImageResource.getPixelRGBA(clamp(floorX, 0, resourceWidth - 1), clamp(floorY, 0, resourceHeight - 1));
               int pixel2 = nativeImageResource.getPixelRGBA(clamp(ceilX, 0, resourceWidth - 1), clamp(floorY, 0, resourceHeight - 1));
               int pixel3 = nativeImageResource.getPixelRGBA(clamp(floorX, 0, resourceWidth - 1), clamp(ceilY, 0, resourceHeight - 1));
               int pixel4 = nativeImageResource.getPixelRGBA(clamp(ceilX, 0, resourceWidth - 1), clamp(ceilY, 0, resourceHeight - 1));
               int newColor;
               if (useActualColor) {
                  newColor = invertColor(pixel1);
               } else {
                  float luminance1 = (float)(pixel1 >> 24 & 255) * percentX1 * percentY1;
                  float luminance2 = (float)(pixel2 >> 24 & 255) * percentX2 * percentY1;
                  float luminance3 = (float)(pixel3 >> 24 & 255) * percentX1 * percentY2;
                  float luminance4 = (float)(pixel4 >> 24 & 255) * percentX2 * percentY2;
                  newColor = (color & 16777215) + ((int)(luminance1 + luminance2 + luminance3 + luminance4) << 24);
               }

               blendPixel(nativeImage, (flipX ? width - drawX - 1 : drawX) + x, drawY + y, newColor);
            }
         }

         nativeImageResource.close();
      } catch (Exception var39) {
      }

   }

   public static void blendPixel(NativeImage nativeImage, int x, int y, int color) {
      if (x >= 0 && y >= 0 && x <= nativeImage.getWidth() - 1 && y <= nativeImage.getHeight() - 1) {
         float percent = (float)(color >> 24 & 255) / 255.0F;
         if (!(percent <= 0.0F)) {
            int existingPixel = nativeImage.getPixelRGBA(x, y);
            boolean existingTransparent = (existingPixel >> 24 & 255) == 0;
            int r1 = existingTransparent ? 255 : existingPixel & 255;
            int g1 = existingTransparent ? 255 : existingPixel >> 8 & 255;
            int b1 = existingTransparent ? 255 : existingPixel >> 16 & 255;
            int r2 = color >> 16 & 255;
            int g2 = color >> 8 & 255;
            int b2 = color & 255;
            float inversePercent = 1.0F - percent;
            int finalColor = -16777216 | ((int)((float)r1 * inversePercent + (float)r2 * percent) << 16) + ((int)((float)g1 * inversePercent + (float)g2 * percent) << 8) + (int)((float)b1 * inversePercent + (float)b2 * percent);
            drawPixelSafe(nativeImage, x, y, finalColor);
         }
      }
   }

   public static void drawPixelSafe(NativeImage nativeImage, int x, int y, int color) {
      if (x >= 0 && y >= 0 && x <= nativeImage.getWidth() - 1 && y <= nativeImage.getHeight() - 1) {
         nativeImage.setPixelRGBA(x, y, invertColor(color));
      }

   }

   public static int invertColor(int color) {
      return ((color & -16777216) != 0 ? -16777216 : 0) + ((color & 255) << 16) + (color & '\uff00') + ((color & 16711680) >> 16);
   }

   public static void clearColor(NativeImage nativeImage, int color) {
      for(int x = 0; x < nativeImage.getWidth(); ++x) {
         for(int y = 0; y < nativeImage.getHeight(); ++y) {
            if (nativeImage.getPixelRGBA(x, y) == color) {
               nativeImage.setPixelRGBA(x, y, 0);
            }
         }
      }

   }

   private static int clamp(int value, int min, int max) {
      return value < min ? min : (value > max ? max : value);
   }

   public static String cjkOnly(String text) {
      if (text != null && !text.isEmpty()) {
         int index = text.indexOf(124);
         return (index >= 0 ? text.substring(0, index) : text).trim();
      } else {
         return "";
      }
   }

   public static String latinOnly(String text) {
      if (text != null && !text.isEmpty()) {
         int index = text.indexOf(124);
         return index >= 0 && index + 1 < text.length() ? text.substring(index + 1).trim() : "";
      } else {
         return "";
      }
   }

   public static final class LcdInfo {
      public final String currentStation;
      public final String nextStation;
      public final String routeName;
      public final int routeColor;

      public LcdInfo(String currentStation, String nextStation, String routeName, int routeColor) {
         this.currentStation = currentStation;
         this.nextStation = nextStation;
         this.routeName = routeName;
         this.routeColor = routeColor;
      }
   }

   public static final class LcdInfoMulti {
      public final String currentStation;
      public final List<PSDTopTextureGenerator.LcdNextLine> nextLines;

      public LcdInfoMulti(String currentStation, List<PSDTopTextureGenerator.LcdNextLine> nextLines) {
         this.currentStation = currentStation;
         this.nextLines = nextLines;
      }
   }

   public static final class LcdNextLine {
      public final String routeName;
      public final int routeColor;
      public final String nextStation;

      public LcdNextLine(String routeName, int routeColor, String nextStation) {
         this.routeName = routeName;
         this.routeColor = routeColor;
         this.nextStation = nextStation;
      }
   }

   public static final class PanelInfo {
      public final Route route;
      public final String badgeText;
      public final int routeColor;
      public final String destinationText;
      public final int carCount;
      public final boolean fromSchedule;

      private PanelInfo(Route route, String badgeText, int routeColor, String destinationText, int carCount, boolean fromSchedule) {
         this.route = route;
         this.badgeText = badgeText;
         this.routeColor = routeColor;
         this.destinationText = destinationText;
         this.carCount = carCount;
         this.fromSchedule = fromSchedule;
      }
   }
}
