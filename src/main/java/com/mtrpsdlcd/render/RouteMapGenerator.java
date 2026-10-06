package com.mtrpsdlcd.render;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.NativeImage.Format;
import com.mtrpsdlcd.block.PSDCustomText;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import mtr.client.ClientCache;
import mtr.client.ClientData;
import mtr.client.Config;
import mtr.data.IGui;
import mtr.data.Platform;
import mtr.data.Route;
import mtr.data.Station;
import mtr.data.IGui.HorizontalAlignment;
import mtr.data.IGui.VerticalAlignment;
import mtr.data.Route.CircularState;
import net.minecraftforge.fml.loading.FMLPaths;

public final class RouteMapGenerator {
   private static int scale;
   private static int lineSize;
   private static int lineSpacing;
   private static int fontSizeBig;
   private static int fontSizeSmall;
   private static final float SQRT_OF_TWO = (float)Math.sqrt(2.0D);
   private static final float LINE_Y_BASE_RATIO = 0.68F;
   private static final float WIDTH_INSET_RATIO = 0.115F;
   private static final float STATION_NAME_ANGLE = 45.0F;
   private static final float STATION_NAME_FONT_SCALE = 0.92F;
   public static final int PIXEL_SCALE = 4;
   private static final int MIN_VERTICAL_SIZE = 5;
   private static final String CIRCLE_RESOURCE = "textures/block/sign/circle.png";
   private static final int PIXEL_RESOLUTION = 24;
   public static final String CIRCULAR_MARKER_FOR_FORMATTED = "\u0001";
   public static final float TRAIN1_V1 = 0.035F;
   public static final float TRAIN1_V2 = 0.475F;
   public static final float TRAIN2_V1 = 0.525F;
   public static final float TRAIN2_V2 = 0.965F;
   public static final float BLOCK_U1 = 0.05F;
   public static final float BLOCK_U2 = 0.197F;
   public static final float PANEL_BG_U1 = 0.2F;
   public static final float PANEL_BG_U2 = 0.728F;
   public static final float TRAIN_U1 = 0.733F;
   public static final float TRAIN_U2 = 0.948F;
   public static final float CUSTOM_IMAGE_U = 0.3F;
   public static final float BLUE_BOX_V1 = 0.04F;
   public static final float BLUE_BOX_V2 = 0.3F;
   private static final int SCREEN_BG_TOP = -264988;
   private static final int SCREEN_BG_BOTTOM = -673729;
   private static final float SCREEN_RESOLUTION_SCALE = 2.0F;
   private static final int SCREEN_MAX_TEXTURE_HEIGHT = 1024;
   private static final int MAX_IMAGE_SIDE = 512;
   private static final boolean IMAGE_SWAP_RB = false;
   private static final float GOLDEN_RATIO = 1.618F;
   private static final float COLOR_BLOCK_SCALE = 0.7F;
   private static final float COLOR_BLOCK_CENTER_V = 0.66F;
   private static final float PANEL_ROUTE_U1 = 0.2F;
   private static final float PANEL_ROUTE_U2 = 0.728F;
   private static final int SPAN_AFTER = 3;
   private static final int SPAN_BEFORE = 1;
   private static final int SPAN_BEFORE_TERMINUS = 2;
   private static final float LINE_SCALE = 0.75F;
   private static final float BADGE_R_SCALE = 0.42F;
   private static final float BADGE_GAP_SCALE = 0.3F;
   private static final float SCREEN_NAME_FONT_SCALE = 0.75F;
   private static final float SCREEN_NAME_FONT_MIN = 0.6F;
   private static final String[] TRAIN_TITLE = new String[]{"\u672c\u73ed\u5217\u8f66 the train", "\u4e0b\u4e00\u73ed\u5217\u8f66 the next train"};
   private static final int COLOR_PASSED = -5592406;
   private static final Map<String, com.mojang.blaze3d.platform.NativeImage> SCREEN_CANVASES = new ConcurrentHashMap();
   private static final Map<String, byte[]> TEXT_PIXELS_CACHE = new ConcurrentHashMap();
   private static final Map<String, int[]> TEXT_DIMS_CACHE = new ConcurrentHashMap();
   private static final Map<String, com.mojang.blaze3d.platform.NativeImage> CUSTOM_IMAGE_SOURCES = new ConcurrentHashMap();
   private static final Set<String> CUSTOM_IMAGE_SOURCE_FAILED = ConcurrentHashMap.newKeySet();
   private static final Map<String, int[]> CUSTOM_IMAGE_AREA_PIXELS = new ConcurrentHashMap();
   /** Diagnostic: -Dmtrpsdlcd.dumpmaps=true writes every generated map to <gamedir>/psd-debug. */
   private static final boolean DUMP_MAPS = Boolean.getBoolean("mtrpsdlcd.dumpmaps");

   private RouteMapGenerator() {
   }

   public static void setConstants() {
      scale = (int)Math.pow(2.0D, (double)(Config.dynamicTextureResolution() + 5));
      lineSize = scale / 8;
      lineSpacing = lineSize * 3 / 2;
      fontSizeBig = lineSize * 2;
      fontSizeSmall = fontSizeBig / 2;
   }

   public static List<Route> routesAtPlatform(long platformId) {
      List<Route> routes = new ArrayList();

      try {
         if (ClientData.ROUTES == null) {
            return routes;
         }

         for(Route route : ClientData.ROUTES) {
            if (route != null && route.containsPlatformId(platformId)) {
               routes.add(route);
            }
         }

         routes.sort((a, b) -> a.color == b.color ? a.compareTo(b) : a.color - b.color);
      } catch (Throwable var5) {
      }

      return routes;
   }

   public static int routeColorArgb(Route route) {
      return route == null ? 0 : -16777216 | route.color;
   }

   public static List<Integer> getRouteStream(long platformId, RouteMapGenerator.RouteCallback callback) {
      List<Integer> colors = new ArrayList();
      List<Integer> terminatingColors = new ArrayList();

      try {
         for(Route route : routesAtPlatform(platformId)) {
            if (route != null && !route.isHidden) {
               int currentStationIndex = route.getPlatformIdIndex(platformId);
               if (currentStationIndex >= 0) {
                  if (currentStationIndex < route.platformIds.size() - 1) {
                     if (callback != null) {
                        callback.accept(route, currentStationIndex);
                     }

                     if (!colors.contains(route.color)) {
                        colors.add(route.color);
                     }
                  } else if (!terminatingColors.contains(route.color)) {
                     terminatingColors.add(route.color);
                  }
               }
            }
         }
      } catch (Throwable var8) {
      }

      if (colors.isEmpty()) {
         colors.addAll(terminatingColors);
      }

      return colors;
   }

   public static List<Integer> getRouteStreamForStation(long platformId, RouteMapGenerator.RouteCallback callback) {
      long stationId = getStationIdForPlatform(platformId);
      if (stationId < 0L) {
         return getRouteStream(platformId, callback);
      } else {
         List<Integer> colors = new ArrayList();
         List<Integer> terminatingColors = new ArrayList();

         try {
            for(Route route : routesAtPlatform(platformId)) {
               if (route.name != null && !route.name.isEmpty()) {
                  int currentStationIndex = getStationIndexOnRoute(route, stationId, platformId);
                  if (currentStationIndex >= 0) {
                     if (currentStationIndex < route.platformIds.size() - 1) {
                        if (callback != null) {
                           callback.accept(route, currentStationIndex);
                        }

                        if (!colors.contains(route.color)) {
                           colors.add(route.color);
                        }
                     } else if (!terminatingColors.contains(route.color)) {
                        terminatingColors.add(route.color);
                     }
                  }
               }
            }
         } catch (Throwable var10) {
         }

         if (colors.isEmpty()) {
            colors.addAll(terminatingColors);
         }

         return colors;
      }
   }

   public static long getStationIdForPlatform(long platformId) {
      if (platformId < 0L) {
         return -1L;
      } else {
         ClientCache clientCache = ClientData.DATA_CACHE;
         if (clientCache == null) {
            return -1L;
         } else {
            Station station = (Station)clientCache.platformIdToStation.get(platformId);
            return station == null ? -1L : station.id;
         }
      }
   }

   public static List<Long> collectRouteIdsForStation(long platformId) {
      List<Long> ids = new ArrayList();
      long stationId = getStationIdForPlatform(platformId);

      for(Route route : routesAtPlatform(platformId)) {
         if (getStationIndexOnRoute(route, stationId, platformId) >= 0) {
            ids.add(route.id);
         }
      }

      return ids;
   }

   public static int getStationIndexOnRoute(Route route, long stationId, long platformId) {
      if (route == null) {
         return -1;
      } else if (stationId >= 0L) {
         for(int i = 0; i < route.platformIds.size(); ++i) {
            Station station = ClientData.DATA_CACHE == null ? null : (Station)ClientData.DATA_CACHE.platformIdToStation.get(((Route.RoutePlatform)route.platformIds.get(i)).platformId);
            if (station != null && station.id == stationId) {
               return i;
            }
         }

         return -1;
      } else {
         return route.getPlatformIdIndex(platformId);
      }
   }

   public static String getDestination(Route route, int index) {
      try {
         ClientCache cache = ClientData.DATA_CACHE;
         if (cache != null && route != null) {
            String destination = cache.getFormattedRouteDestination(route, index, "\u0001");
            return destination == null ? "" : destination.replace("\u0001", "");
         } else {
            return "";
         }
      } catch (Throwable var4) {
         return "";
      }
   }

   public static String formattedDestinationWithMarker(Route route, int index) {
      try {
         ClientCache cache = ClientData.DATA_CACHE;
         if (cache != null && route != null) {
            String destination = cache.getFormattedRouteDestination(route, index, "\u0001");
            if (destination != null && !destination.isEmpty()) {
               return destination;
            } else if (route.circularState == CircularState.NONE) {
               String lastPlatformStation = lastPlatformStationName(route, cache);
               return lastPlatformStation == null ? "" : lastPlatformStation;
            } else {
               return destination == null ? "" : destination;
            }
         } else {
            return "";
         }
      } catch (Throwable var5) {
         return "";
      }
   }

   public static String lastPlatformStationName(Route route, ClientCache cache) {
      if (route != null && cache != null) {
         Station lastStation = (Station)cache.platformIdToStation.get(route.getLastPlatformId());
         return lastStation == null ? null : lastStation.name;
      } else {
         return null;
      }
   }

   public static List<RouteMapGenerator.ArrowEntry> collectArrowEntries(long platformId) {
      List<RouteMapGenerator.ArrowEntry> entries = new ArrayList();

      try {
         for(Route route : routesAtPlatform(platformId)) {
            if (route != null && !route.isHidden) {
               int index = route.getPlatformIdIndex(platformId);
               if (index >= 0) {
                  int platformCount = route.platformIds == null ? 0 : route.platformIds.size();
                  if (index < platformCount - 1) {
                     ClientCache cache = ClientData.DATA_CACHE;
                     String mtrValue = cache == null ? null : safeFormattedDestination(cache, route, index);
                     boolean mtrEmpty = mtrValue == null || mtrValue.isEmpty();
                     String destination = formattedDestinationWithMarker(route, index);
                     boolean fallbackResolved = mtrEmpty && destination != null && !destination.isEmpty();
                     entries.add(new RouteMapGenerator.ArrowEntry(route.id, route.color, index, platformCount, false, false, destination == null ? "" : destination, mtrEmpty, fallbackResolved));
                  } else {
                     entries.add(new RouteMapGenerator.ArrowEntry(route.id, route.color, index, platformCount, false, true, "", false, false));
                  }
               }
            }
         }
      } catch (Throwable var12) {
      }

      return entries;
   }

   private static String safeFormattedDestination(ClientCache cache, Route route, int index) {
      try {
         return cache.getFormattedRouteDestination(route, index, "\u0001");
      } catch (Throwable var4) {
         return null;
      }
   }

   public static String arrowContentSignature(long platformId) {
      StringBuilder signature = new StringBuilder();

      try {
         for(RouteMapGenerator.ArrowEntry entry : collectArrowEntries(platformId)) {
            signature.append(entry.routeId).append(':').append(entry.routeColor).append(':').append(entry.terminating ? "TERM" : entry.destination).append(';');
         }

         signature.append("P:").append(platformNameOf(platformId));
      } catch (Throwable var5) {
      }

      return signature.toString();
   }

   public static String arrowDiagInfo(long platformId) {
      StringBuilder diag = new StringBuilder();

      try {
         List<RouteMapGenerator.ArrowEntry> entries = collectArrowEntries(platformId);
         int routeCount = routesAtPlatform(platformId).size();
         StringBuilder raw = new StringBuilder();
         int terminating = 0;
         int mtrEmpty = 0;
         int fallbackResolved = 0;
         int index = -1;
         int platformCount = 0;
         boolean hidden = false;

         for(RouteMapGenerator.ArrowEntry entry : entries) {
            if (raw.length() > 0) {
               raw.append(',');
            }

            raw.append('[').append(entry.terminating ? "TERM" : entry.destination).append(']');
            if (entry.terminating) {
               ++terminating;
            }

            if (index < 0) {
               index = entry.platformIndex;
               platformCount = entry.platformCount;
               hidden = entry.hidden;
            }

            if (entry.mtrEmpty) {
               ++mtrEmpty;
            }

            if (entry.fallbackResolved) {
               ++fallbackResolved;
            }
         }

         diag.append(" route_count=").append(routeCount).append(" platform_ids=").append(platformCount).append(" index=").append(index).append(" hidden=").append(hidden).append(" entries=").append(entries.size()).append(" terminating=").append(terminating > 0 || entries.isEmpty()).append(" destinations=").append(entries.isEmpty() ? "[]" : raw.toString()).append(" mtr_empty=").append(mtrEmpty).append(" fallback=").append(fallbackResolved);
      } catch (Throwable var14) {
      }

      return diag.toString();
   }

   public static String platformNameOf(long platformId) {
      try {
         ClientCache cache = ClientData.DATA_CACHE;
         if (cache == null) {
            return "";
         } else {
            Platform platform = (Platform)cache.platformIdMap.get(platformId);
            return platform != null && platform.name != null ? platform.name : "";
         }
      } catch (Throwable var4) {
         return "";
      }
   }

   public static boolean detectRouteDirectionLeft(Route route, long platformId) {
      try {
         ClientCache cache = ClientData.DATA_CACHE;
         if (cache != null && route != null && route.platformIds != null && !route.platformIds.isEmpty()) {
            int thisIdx = route.getPlatformIdIndex(platformId);
            if (thisIdx < 0) {
               return false;
            } else {
               String destination = getDestination(route, thisIdx);
               if (destination != null && !destination.isEmpty()) {
                  String destinationCjk = PSDTopTextureGenerator.cjkOnly(destination);
                  if (destinationCjk.isEmpty()) {
                     return false;
                  } else {
                     for(int i = 0; i < route.platformIds.size(); ++i) {
                        Station station = (Station)cache.platformIdToStation.get(((Route.RoutePlatform)route.platformIds.get(i)).platformId);
                        if (station != null && destinationCjk.equals(PSDTopTextureGenerator.cjkOnly(station.name))) {
                           return i > thisIdx;
                        }
                     }

                     return false;
                  }
               } else {
                  return false;
               }
            }
         } else {
            return false;
         }
      } catch (Throwable var9) {
         return false;
      }
   }

   public static String getCountdownText(long arrivalMillis) {
      long seconds = (arrivalMillis - System.currentTimeMillis()) / 1000L;
      return seconds > 60L ? (seconds + 59L) / 60L + " \u5206\u949f\u8fdb\u7ad9" : "\u5217\u8f66\u8fdb\u7ad9\uff0c\u8bf7\u6ce8\u610f\u5b89\u5168";
   }

   public static boolean isArriving(long arrivalMillis) {
      return arrivalMillis - System.currentTimeMillis() <= 60000L;
   }

   public static NativeImage generateRouteMap(long platformId, boolean vertical, boolean flip, float aspectRatio, boolean transparentWhite) {
      return generateRouteMap(platformId, -1L, vertical, flip, aspectRatio, transparentWhite);
   }

   public static NativeImage generateRouteMap(long platformId, long routeId, boolean vertical, boolean flip, float aspectRatio, boolean transparentWhite) {
      if (aspectRatio <= 0.0F) {
         return null;
      } else {
         try {
            List<Route> routes = new ArrayList();
            List<Integer> currentIndices = new ArrayList();
            long stationId = getStationIdForPlatform(platformId);
            RouteMapGenerator.RouteCallback collect = (routex, currentStationIndex) -> {
               if (routeId < 0L || routex.id == routeId) {
                  routes.add(routex);
                  currentIndices.add(currentStationIndex);
               }

            };
            if (routeId >= 0L) {
               getRouteStreamForStation(platformId, collect);
            } else {
               getRouteStream(platformId, collect);
            }

            int routeCount = routes.size();
            if (routeCount <= 0) {
               return null;
            } else {
               setConstants();
               List<List<Long>> stationsIdsBefore = new ArrayList();
               List<List<Long>> stationsIdsAfter = new ArrayList();
               List<Map<Integer, RouteMapGenerator.StationPosition>> stationPositions = new ArrayList();
               List<Integer> colors = new ArrayList();
               int[] colorIndices = new int[routeCount];
               int colorIndex = -1;

               for(int routeIndex = 0; routeIndex < routeCount; ++routeIndex) {
                  stationsIdsBefore.add(new ArrayList());
                  stationsIdsAfter.add(new ArrayList());
                  stationPositions.add(new HashMap());
                  Route route = (Route)routes.get(routeIndex);
                  int currentIndex = currentIndices.get(routeIndex);

                  for(int stationIndex = 0; stationIndex < route.platformIds.size(); ++stationIndex) {
                     if (stationIndex != currentIndex) {
                        long thisStationId = getStationIdAt(route, stationIndex);
                        if (stationIndex < currentIndex) {
                           ((List)stationsIdsBefore.get(stationsIdsBefore.size() - 1)).add(0, thisStationId);
                        } else {
                           ((List)stationsIdsAfter.get(stationsIdsAfter.size() - 1)).add(thisStationId);
                        }
                     }
                  }

                  if (!colors.contains(route.color)) {
                     colors.add(route.color);
                  }

                  ++colorIndex;
                  colorIndices[routeIndex] = colorIndex;
               }

               for(int routeIndex = 0; routeIndex < routeCount; ++routeIndex) {
                  ((Map)stationPositions.get(routeIndex)).put(0, new RouteMapGenerator.StationPosition(0.0F, getLineOffset(routeIndex, colorIndices), true));
               }

               float[] bounds = new float[3];
               setup(stationPositions, flip ? stationsIdsBefore : stationsIdsAfter, colorIndices, bounds, flip, true);
               float xOffset = bounds[0] + 0.5F;
               setup(stationPositions, flip ? stationsIdsAfter : stationsIdsBefore, colorIndices, bounds, !flip, false);
               float rawHeightPart = Math.abs(bounds[1]) + (vertical ? 0.6F : 1.0F);
               float rawWidth = xOffset + bounds[0] + 0.5F;
               float rawHeightTotal = rawHeightPart + bounds[2] + (vertical ? 0.6F : 1.0F);
               float rawHeight;
               float extraPadding;
               float yOffset;
               if (vertical && rawHeightTotal < 5.0F) {
                  rawHeight = 5.0F;
                  extraPadding = (5.0F - rawHeightTotal) / 2.0F;
                  yOffset = rawHeightPart + extraPadding;
               } else {
                  rawHeight = rawHeightTotal;
                  extraPadding = 0.0F;
                  yOffset = rawHeightPart;
               }

               float widthScale;
               float heightScale;
               int width;
               int height;
               if (rawWidth / rawHeight > aspectRatio) {
                  width = Math.round(rawWidth * (float)scale);
                  height = Math.round((float)width / aspectRatio);
                  widthScale = 1.0F;
                  heightScale = (float)height / rawHeight / (float)scale;
               } else {
                  height = Math.round(rawHeight * (float)scale);
                  width = Math.round((float)height * aspectRatio);
                  heightScale = 1.0F;
                  widthScale = (float)width / rawWidth / (float)scale;
               }

               if (width > 0 && height > 0) {
                  NativeImage nativeImage = new NativeImage(Format.RGBA, width, height, false);
                  nativeImage.fillRect(0, 0, width, height, -1);
                  Set<Integer> currentRouteColors = new HashSet();
                  Set<String> currentRouteNames = new HashSet();

                  for(int routeIndex = 0; routeIndex < routeCount; ++routeIndex) {
                     Route route = (Route)routes.get(routeIndex);
                     currentRouteColors.add(route.color);
                     currentRouteNames.add(route.name.split("\\|\\|")[0]);
                  }

                  int maxStringWidth = (int)((double)scale * 0.9D * (double)((vertical ? heightScale : widthScale) / 2.0F + extraPadding));

                  for(int routeIndex = 0; routeIndex < routeCount; ++routeIndex) {
                     Route route = (Route)routes.get(routeIndex);
                     int currentIndex = currentIndices.get(routeIndex);
                     Map<Integer, RouteMapGenerator.StationPosition> routeStationPositions = (Map)stationPositions.get(routeIndex);

                     for(int stationIndex = 0; stationIndex < route.platformIds.size(); ++stationIndex) {
                        long thisStationId = getStationIdAt(route, stationIndex);
                        RouteMapGenerator.StationPosition stationPosition = (RouteMapGenerator.StationPosition)routeStationPositions.get(stationIndex - currentIndex);
                        if (stationPosition != null) {
                           if (stationIndex < route.platformIds.size() - 1) {
                              RouteMapGenerator.StationPosition nextStationPosition = (RouteMapGenerator.StationPosition)routeStationPositions.get(stationIndex + 1 - currentIndex);
                              if (nextStationPosition != null) {
                                 drawLine(nativeImage, stationPosition, nextStationPosition, widthScale, heightScale, xOffset, yOffset, stationIndex < currentIndex ? -5592406 : -16777216 | route.color);
                              }
                           }

                           int x;
                           int y;
                           int stationOffset;
                           boolean var10000;
                           label383: {
                              int lines = 0;
                              x = Math.round((stationPosition.x + xOffset) * (float)scale * widthScale);
                              y = Math.round((stationPosition.y + yOffset) * (float)scale * heightScale);
                              stationOffset = stationIndex - currentIndex;
                              if (!vertical) {
                                 label359: {
                                    if (stationPosition.isCommon) {
                                       if (Math.abs(stationOffset) % 2 == 0) {
                                          break label359;
                                       }
                                    } else if ((float)y >= yOffset * (float)scale) {
                                       break label359;
                                    }

                                    var10000 = false;
                                    break label383;
                                 }
                              }

                              var10000 = true;
                           }

                           boolean textBelow = var10000;
                           boolean currentStation = stationOffset == 0;
                           boolean passed = stationOffset < 0;
                           Map<Integer, ClientCache.ColorNameTuple> interchangeRoutes = ClientData.DATA_CACHE == null ? null : (Map)ClientData.DATA_CACHE.stationIdToRoutes.get(thisStationId);
                           List<Integer> interchangeColors = new ArrayList();
                           List<String> interchangeNames = new ArrayList();
                           if (interchangeRoutes != null) {
                              List<Integer> allColors = new ArrayList(interchangeRoutes.keySet());
                              Collections.sort(allColors);

                              for(Integer color : allColors) {
                                 ClientCache.ColorNameTuple tuple = (ClientCache.ColorNameTuple)interchangeRoutes.get(color);
                                 if (tuple != null && !currentRouteColors.contains(color) && !currentRouteNames.contains(tuple.name)) {
                                    if (!interchangeColors.contains(color)) {
                                       interchangeColors.add(color);
                                    }

                                    if (!interchangeNames.contains(tuple.name)) {
                                       interchangeNames.add(tuple.name);
                                    }
                                 }
                              }
                           }

                           if (!interchangeColors.isEmpty() && !currentStation) {
                              int lineHeight = lineSize * 2;
                              int lineWidth = (int)Math.ceil((double)((float)lineSize / (float)interchangeColors.size()));

                              for(int i = 0; i < interchangeColors.size(); ++i) {
                                 for(int drawX = 0; drawX < lineWidth; ++drawX) {
                                    for(int drawY = 0; drawY < lineHeight; ++drawY) {
                                       drawPixelSafe(nativeImage, x + drawX + lineWidth * i - lineWidth * interchangeColors.size() / 2, y + (textBelow ? -1 : 0 * lineSpacing) + (textBelow ? -drawY : drawY), passed ? -5592406 : -16777216 | interchangeColors.get(i));
                                    }
                                 }
                              }

                              int[] interchangeDimensions = new int[2];
                              byte[] interchangePixels = getTextPixels(IGui.mergeStations(interchangeNames), interchangeDimensions, maxStringWidth - (vertical ? lineHeight : 0), (int)((float)(fontSizeBig + fontSizeSmall) * 1.25F / 2.0F), fontSizeBig / 2, fontSizeSmall / 2, 0, vertical ? HorizontalAlignment.LEFT : HorizontalAlignment.CENTER);
                              drawString(nativeImage, interchangePixels, x, y + (textBelow ? -1 - lineHeight : 0 * lineSpacing + lineHeight), interchangeDimensions, HorizontalAlignment.CENTER, textBelow ? VerticalAlignment.BOTTOM : VerticalAlignment.TOP, 0, passed ? -5592406 : -16777216, vertical);
                           }

                           drawStation(nativeImage, x, y, heightScale, 0, passed);
                           Station station = ClientData.DATA_CACHE == null ? null : (Station)ClientData.DATA_CACHE.stationIdMap.get(thisStationId);
                           int[] dimensions = new int[2];
                           byte[] pixels = getTextPixels(station == null ? "" : station.name, dimensions, maxStringWidth, (int)((float)(fontSizeBig + fontSizeSmall) * 1.25F), fontSizeBig, fontSizeSmall, fontSizeSmall / 4, vertical ? HorizontalAlignment.RIGHT : HorizontalAlignment.CENTER);
                           drawString(nativeImage, pixels, x, y + (textBelow ? 0 * lineSpacing : -1) + (textBelow ? 1 : -1) * lineSize * 5 / 4, dimensions, HorizontalAlignment.CENTER, textBelow ? VerticalAlignment.TOP : VerticalAlignment.BOTTOM, currentStation ? -16777216 : 0, passed ? -5592406 : (currentStation ? -1 : -16777216), vertical);
                        }
                     }
                  }

                  if (transparentWhite) {
                     clearColor(nativeImage, -1);
                  }

                  if (DUMP_MAPS) {
                     dumpMap(nativeImage, platformId, routeId, routes, currentIndices, width, height);
                  }

                  return nativeImage;
               } else {
                  return null;
               }
            }
         } catch (Exception var59) {
            return null;
         }
      }
   }

   private static void dumpMap(NativeImage image, long platformId, long routeId, List<Route> routes, List<Integer> currentIndices, int width, int height) {
      try {
         Path dir = FMLPaths.GAMEDIR.get().resolve("psd-debug");
         Files.createDirectories(dir);
         Path file = dir.resolve("map_p" + platformId + "_r" + routeId + "_" + width + "x" + height + ".png");
         image.writeToFile(file);
         StringBuilder detail = new StringBuilder();

         for (int i = 0; i < routes.size(); ++i) {
            Route route = routes.get(i);
            detail.append(" [").append(route.name)
                  .append(" platforms=").append(route.platformIds == null ? -1 : route.platformIds.size())
                  .append(" currentIndex=").append(currentIndices.get(i))
                  .append(" circular=").append(route.circularState)
                  .append("]");
         }

         System.out.println("[mtr_psd_lcd][dump] " + file.getFileName() + " routeCount=" + routes.size() + detail);
      } catch (Throwable t) {
         System.out.println("[mtr_psd_lcd][dump] failed: " + t);
      }
   }

   private static long getStationIdAt(Route route, int index) {      if (route != null && index >= 0 && index < route.platformIds.size()) {
         ClientCache clientCache = ClientData.DATA_CACHE;
         if (clientCache == null) {
            return -1L;
         } else {
            Station station = (Station)clientCache.platformIdToStation.get(((Route.RoutePlatform)route.platformIds.get(index)).platformId);
            return station == null ? -1L : station.id;
         }
      } else {
         return -1L;
      }
   }

   private static void setup(List<Map<Integer, RouteMapGenerator.StationPosition>> stationPositions, List<List<Long>> stationsIdLists, int[] colorIndices, float[] bounds, boolean passed, boolean reverse) {
      int passedMultiplier = passed ? -1 : 1;
      int reverseMultiplier = reverse ? -1 : 1;
      bounds[0] = 0.0F;
      List<Long> commonStationIds = new ArrayList();

      for(Long stationId : stationsIdLists.get(0)) {
         if (stationId != 0L && !commonStationIds.contains(stationId) && stationsIdLists.stream().allMatch((stationsIds) -> stationsIds.contains(stationId))) {
            commonStationIds.add(stationId);
         }
      }

      int positionXOffset = 0;
      int routeCount = stationsIdLists.size();
      int[] traverseIndex = new int[routeCount];

      for(int commonStationIndex = 0; commonStationIndex <= commonStationIds.size(); ++commonStationIndex) {
         boolean lastStation = commonStationIndex == commonStationIds.size();
         long commonStationId = lastStation ? -1L : commonStationIds.get(commonStationIndex);
         int intermediateSegmentsMaxCount = 0;
         int[] intermediateSegmentsCounts = new int[routeCount];

         for(int routeIndex = 0; routeIndex < routeCount; ++routeIndex) {
            intermediateSegmentsCounts[routeIndex] = (lastStation ? ((List)stationsIdLists.get(routeIndex)).size() : ((List)stationsIdLists.get(routeIndex)).indexOf(commonStationId) + 1) - traverseIndex[routeIndex];
            intermediateSegmentsMaxCount = Math.max(intermediateSegmentsMaxCount, intermediateSegmentsCounts[routeIndex]);
         }

         List<Integer> routesIndicesInSection = new ArrayList();

         for(int routeIndex = 0; routeIndex < routeCount; ++routeIndex) {
            if (!lastStation || intermediateSegmentsCounts[routeIndex] > 0) {
               routesIndicesInSection.add(routeIndex);
            }
         }

         for(int routeIndex = 0; routeIndex < routeCount; ++routeIndex) {
            if (intermediateSegmentsCounts[routeIndex] > 0) {
               float increment = (float)intermediateSegmentsMaxCount / (float)intermediateSegmentsCounts[routeIndex];

               for(int j = 0; j < intermediateSegmentsCounts[routeIndex] - (lastStation ? 0 : 1); ++j) {
                  float stationX = (float)positionXOffset + increment * (float)(j + 1);
                  bounds[0] = Math.max(bounds[0], stationX / 2.0F);
                  float stationY = (float)routesIndicesInSection.indexOf(routeIndex) - (float)(routesIndicesInSection.size() - 1) / 2.0F + getLineOffset(routeIndex, colorIndices);
                  bounds[1] = Math.min(bounds[1], stationY);
                  bounds[2] = Math.max(bounds[2], stationY);
                  ((Map)stationPositions.get(routeIndex)).put(passedMultiplier * (j + traverseIndex[routeIndex] + 1), new RouteMapGenerator.StationPosition((float)reverseMultiplier * stationX / 2.0F, stationY, false));
               }

               traverseIndex[routeIndex] += intermediateSegmentsCounts[routeIndex];
            }
         }

         if (!lastStation) {
            positionXOffset += intermediateSegmentsMaxCount;

            for(int routeIndex = 0; routeIndex < routeCount; ++routeIndex) {
               float stationY = getLineOffset(routeIndex, colorIndices);
               bounds[1] = Math.min(bounds[1], stationY);
               bounds[2] = Math.max(bounds[2], stationY);
               ((Map)stationPositions.get(routeIndex)).put(passedMultiplier * traverseIndex[routeIndex], new RouteMapGenerator.StationPosition((float)(reverseMultiplier * positionXOffset) / 2.0F, stationY, true));
            }

            bounds[0] = (float)positionXOffset / 2.0F;
         }
      }

   }

   private static float getLineOffset(int routeIndex, int[] colorIndices) {
      return (float)lineSpacing / (float)scale * ((float)colorIndices[routeIndex] - (float)colorIndices[colorIndices.length - 1] / 2.0F);
   }

   private static void drawLine(NativeImage nativeImage, RouteMapGenerator.StationPosition stationPosition1, RouteMapGenerator.StationPosition stationPosition2, float widthScale, float heightScale, float xOffset, float yOffset, int color) {
      int x1 = Math.round((stationPosition1.x + xOffset) * (float)scale * widthScale);
      int x2 = Math.round((stationPosition2.x + xOffset) * (float)scale * widthScale);
      int y1 = Math.round((stationPosition1.y + yOffset) * (float)scale * heightScale);
      int y2 = Math.round((stationPosition2.y + yOffset) * (float)scale * heightScale);
      int xChange = x2 - x1;
      int yChange = y2 - y1;
      int xChangeAbs = Math.abs(xChange);
      int yChangeAbs = Math.abs(yChange);
      int changeDifference = Math.abs(yChangeAbs - xChangeAbs);
      if (xChangeAbs > yChangeAbs) {
         boolean y1OffsetGreater = Math.abs((float)y1 - yOffset * (float)scale) > Math.abs((float)y2 - yOffset * (float)scale);
         drawLine(nativeImage, x1, y1, x2 - x1, y1OffsetGreater ? 0 : y2 - y1, y1OffsetGreater ? changeDifference : yChangeAbs, color);
         drawLine(nativeImage, x2, y2, x1 - x2, y1OffsetGreater ? y1 - y2 : 0, y1OffsetGreater ? yChangeAbs : changeDifference, color);
      } else {
         int halfXChangeAbs = xChangeAbs / 2;
         drawLine(nativeImage, x1, y1, x2 - x1, y2 - y1, halfXChangeAbs, color);
         drawLine(nativeImage, x2, y2, x1 - x2, y1 - y2, halfXChangeAbs, color);
         drawLine(nativeImage, (x1 + x2) / 2, y1 + (int)Math.copySign((float)halfXChangeAbs, (float)(y2 - y1)), 0, y2 - y1, changeDifference, color);
      }

   }

   private static void drawLine(NativeImage nativeImage, int x, int y, int directionX, int directionY, int length, int color) {
      drawLine(nativeImage, x, y, directionX, directionY, length, color, lineSize);
   }

   private static void drawLine(NativeImage nativeImage, int x, int y, int directionX, int directionY, int length, int color, int lineSizePx) {
      int halfLineHeight = lineSizePx / 2;
      int xWidth = directionX == 0 ? halfLineHeight : 0;
      int yWidth = directionX == 0 ? 0 : (directionY == 0 ? halfLineHeight : Math.round((float)lineSizePx * SQRT_OF_TWO / 2.0F));
      int yMin = y - halfLineHeight - (directionY < 0 ? length : 0) + 1;
      int yMax = y + halfLineHeight + (directionY > 0 ? length : 0) - 1;
      int drawOffset = directionX != 0 && directionY != 0 ? halfLineHeight : 0;

      for(int i = -drawOffset; i < Math.abs(length) + drawOffset; ++i) {
         int drawX = x + (directionX == 0 ? 0 : (int)Math.copySign((float)i, (float)directionX)) + (directionX < 0 ? -1 : 0);
         int drawY = y + (directionY == 0 ? 0 : (int)Math.copySign((float)i, (float)directionY)) + (directionY < 0 ? -1 : 0);

         for(int offset = 0; offset < xWidth; ++offset) {
            drawPixelSafe(nativeImage, drawX - offset - 1, drawY, color);
            drawPixelSafe(nativeImage, drawX + offset, drawY, color);
         }

         for(int offset = 0; offset < yWidth; ++offset) {
            drawPixelSafe(nativeImage, drawX, Math.max(drawY - offset, yMin) - 1, color);
            drawPixelSafe(nativeImage, drawX, Math.min(drawY + offset, yMax), color);
         }
      }

   }

   private static void drawStation(NativeImage nativeImage, int x, int y, float heightScale, int lines, boolean passed) {
      drawStation(nativeImage, x, y, heightScale, lines, passed, lineSize, lineSpacing);
   }

   private static void drawStation(NativeImage nativeImage, int x, int y, float heightScale, int lines, boolean passed, int sizePx, int spacingPx) {
      for(int offsetX = -sizePx; offsetX < sizePx; ++offsetX) {
         for(int offsetY = -sizePx; offsetY < sizePx; ++offsetY) {
            int extraOffsetY = offsetY > 0 ? (int)((float)(lines * spacingPx) * heightScale) : 0;
            int repeatDraw = offsetY == 0 ? (int)((float)(lines * spacingPx) * heightScale) : 0;
            double squareSum = ((double)offsetX + 0.5D) * ((double)offsetX + 0.5D) + ((double)offsetY + 0.5D) * ((double)offsetY + 0.5D);
            if (squareSum <= 0.5D * (double)sizePx * (double)sizePx) {
               for(int i = 0; i <= repeatDraw; ++i) {
                  drawPixelSafe(nativeImage, x + offsetX, y + offsetY + extraOffsetY + i, -1);
               }
            } else if (squareSum <= (double)sizePx * (double)sizePx) {
               for(int i = 0; i <= repeatDraw; ++i) {
                  drawPixelSafe(nativeImage, x + offsetX, y + offsetY + extraOffsetY + i, passed ? -5592406 : -16777216);
               }
            }
         }
      }

   }

   private static void drawString(NativeImage nativeImage, byte[] pixels, int x, int y, int[] textDimensions, IGui.HorizontalAlignment horizontalAlignment, IGui.VerticalAlignment verticalAlignment, int backgroundColor, int textColor, boolean rotate90) {
      PSDTopTextureGenerator.drawString(nativeImage, pixels, x, y, textDimensions, horizontalAlignment, verticalAlignment, backgroundColor, textColor, rotate90);
   }

   private static byte[] getTextPixels(String text, int[] dimensions, int maxWidth, int maxHeight, int bigFontSize, int smallFontSize, int padding, IGui.HorizontalAlignment horizontalAlignment) {
      return PSDTopTextureGenerator.getTextPixels(text, dimensions, maxWidth, maxHeight, bigFontSize, smallFontSize, padding, horizontalAlignment);
   }

   private static void drawPixelSafe(NativeImage nativeImage, int x, int y, int color) {
      PSDTopTextureGenerator.drawPixelSafe(nativeImage, x, y, color);
   }

   private static void clearColor(NativeImage nativeImage, int color) {
      PSDTopTextureGenerator.clearColor(nativeImage, color);
   }

   public static com.mojang.blaze3d.platform.NativeImage generateCustomSingleRouteMap(long platformId, List<PSDScheduleCache.Row> scheduleRows, long fallbackRouteId, boolean vertical, boolean flip, float aspectRatio, boolean transparentWhite, String canvasKey, String customText, String customImagePath) {
      if (aspectRatio <= 0.0F) {
         return null;
      } else {
         try {
            float resScale = Math.min(2.0F, 1024.0F / (float)(scale * 2));
            int height = Math.round((float)(scale * 2) * resScale);
            int width = Math.max(Math.round((float)height * aspectRatio), Math.round((float)(scale * 2) * resScale));
            NativeImage reuseCanvas = null;
            if (canvasKey != null) {
               NativeImage cachedCanvas = (NativeImage)SCREEN_CANVASES.get(canvasKey);
               if (cachedCanvas != null && cachedCanvas.getWidth() == width && cachedCanvas.getHeight() == height) {
                  reuseCanvas = cachedCanvas;
               }
            }

            NativeImage nativeImage = reuseCanvas != null ? reuseCanvas : new NativeImage(Format.RGBA, width, height, false);
            if (reuseCanvas == null && canvasKey != null) {
               NativeImage oldCanvas = (NativeImage)SCREEN_CANVASES.put(canvasKey, nativeImage);
               if (oldCanvas != null && oldCanvas != nativeImage) {
                  try {
                     oldCanvas.close();
                  } catch (Exception var24) {
                  }
               }
            }

            for(int gy = 0; gy < height; ++gy) {
               nativeImage.fillRect(0, gy, width, 1, lerpColorAbgr(-264988, -673729, height <= 1 ? 0.0F : (float)gy / (float)(height - 1)));
            }

            float[] rowV1s = new float[]{0.035F, 0.525F};
            float[] rowV2s = new float[]{0.475F, 0.965F};

            for(int k = 0; k < 2; ++k) {
               int wy1 = (int)((float)height * rowV1s[k]);
               int wy2 = (int)((float)height * rowV2s[k]);
               int rowHi = wy2 - wy1;
               nativeImage.fillRect((int)((float)width * 0.2F), wy1, (int)((float)width * 0.528F), Math.max(1, rowHi), -1);
               nativeImage.fillRect((int)((float)width * 0.05F), wy1 + (int)((float)rowHi * 0.04F), (int)((float)width * 0.147F), Math.max(1, (int)((float)rowHi * 0.26000002F)), -1);
               nativeImage.fillRect((int)((float)width * 0.733F), wy1, (int)((float)width * 0.21500003F), Math.max(1, rowHi), -1);
            }

            int frameThickness = Math.max(1, Math.round((float)scale * resScale / 64.0F));
            long stationId = getStationIdForPlatform(platformId);

            for(int k = 0; k < 2; ++k) {
               PSDScheduleCache.Row scheduleRow = scheduleRows != null && k < scheduleRows.size() ? (PSDScheduleCache.Row)scheduleRows.get(k) : null;
               drawScheduleRow(nativeImage, width, height, k, scheduleRow, platformId, stationId, flip, frameThickness, customText, aspectRatio, customImagePath, fallbackRouteId);
            }

            if (transparentWhite) {
               clearColor(nativeImage, -1);
            }

            return nativeImage;
         } catch (Exception var25) {
            return null;
         }
      }
   }

   private static void drawScheduleRow(NativeImage nativeImage, int width, int height, int row, PSDScheduleCache.Row scheduleRow, long platformId, long stationId, boolean flip, int frameThickness, String customText, float currentAspectRatio, String customImagePath, long fallbackRouteId) {
      float rowV1 = row == 0 ? 0.035F : 0.525F;
      float rowV2 = row == 0 ? 0.475F : 0.965F;
      int y1 = (int)((float)height * rowV1);
      int y2 = (int)((float)height * rowV2);
      if (y2 > y1) {
         drawCustomRow(nativeImage, width, height, row, currentAspectRatio, customText, customImagePath);
         int mapX1 = (int)((float)width * 0.2F);
         int mapX2 = (int)((float)width * 0.728F);
         Route route = null;
         int currentIndex = -1;
         String countdown = "";
         boolean arriving = false;
         String destination = "";
         if (scheduleRow != null) {
            route = scheduleRow.route;
            currentIndex = scheduleRow.currentStationIndex;
            countdown = getCountdownText(scheduleRow.arrivalMillis);
            arriving = isArriving(scheduleRow.arrivalMillis);
            destination = scheduleRow.destination;
         } else if (fallbackRouteId >= 0L && ClientData.DATA_CACHE != null) {
            route = (Route)ClientData.DATA_CACHE.routeIdMap.get(fallbackRouteId);
            if (route != null) {
               currentIndex = getStationIndexOnRoute(route, stationId, platformId);
            }
         }

         if (route != null && currentIndex >= 0) {
            int routeColor = route.color;
            drawRouteColorBlock(nativeImage, width, y1, y2, routeColor, route.name == null ? "" : route.name);
            drawRouteMapInBand(nativeImage, width, route, currentIndex, flip, mapX1, mapX2, y1, y2, frameThickness, routeColor);
            if (scheduleRow != null) {
               drawTrainInfoText(nativeImage, width, y1, y2, row >= 0 && row < TRAIN_TITLE.length ? TRAIN_TITLE[row] : "", countdown, destination, arriving);
            }

         }
      }
   }

   private static void drawRouteColorBlock(NativeImage nativeImage, int width, int rowY1, int rowY2, int routeColor, String routeName) {
      int rowH = rowY2 - rowY1;
      int fullW = (int)(0.147F * (float)width);
      int blockW = Math.max(8, (int)((float)fullW * 0.7F));
      if (blockW > 0 && rowH > 0) {
         int blockH = Math.round((float)blockW / 1.618F);
         blockH = Math.max((int)((float)rowH * 0.15F), Math.min((int)((float)rowH * 0.6F), blockH));
         int boxCenterX = (int)(0.1235F * (float)width);
         int x1 = boxCenterX - blockW / 2;
         int x2 = x1 + blockW;
         int centerY = rowY1 + (int)((float)rowH * 0.66F);
         int y1 = centerY - blockH / 2;
         y1 = Math.max(rowY1 + (int)((float)rowH * 0.3F) + 2, Math.min(rowY2 - blockH - 2, y1));
         int y2 = y1 + blockH;

         for(int by = y1; by < y2; ++by) {
            for(int bx = x1; bx < x2; ++bx) {
               drawPixelSafe(nativeImage, bx, by, -16777216 | routeColor);
            }
         }

         int r = routeColor >> 16 & 255;
         int g = routeColor >> 8 & 255;
         int b = routeColor & 255;
         int textColor = (int)(0.299D * (double)r + 0.587D * (double)g + 0.114D * (double)b) > 150 ? -16777216 : -1;
         int cx = (x1 + x2) / 2;
         String cjkName = getCjkOnly(routeName);
         String latinName = getLatinOnly(routeName);
         if (latinName.isEmpty()) {
            drawTextLine(nativeImage, cjkName, blockW * 9 / 10, (int)((float)blockH * 0.55F), (int)((float)blockH * 0.42F), cx, y1 + blockH / 2, HorizontalAlignment.CENTER, textColor);
         } else {
            int cjkSize = (int)((float)blockH * 0.44F);
            int latinSize = Math.max(4, (int)((float)cjkSize * 0.5F));
            drawTextLine(nativeImage, cjkName, blockW * 9 / 10, (int)((float)blockH * 0.38F), cjkSize, cx, y1 + (int)((float)blockH * 0.34F), HorizontalAlignment.CENTER, textColor);
            drawTextLine(nativeImage, latinName, blockW * 9 / 10, (int)((float)blockH * 0.24F), latinSize, cx, y1 + (int)((float)blockH * 0.74F), HorizontalAlignment.CENTER, textColor);
         }

      }
   }

   private static void drawRouteMapInBand(NativeImage nativeImage, int width, Route route, int currentIndex, boolean flip, int routeX1, int routeX2, int bandY1, int bandY2, int frameThickness, int routeColor) {
      List<Route.RoutePlatform> platforms = route.platformIds;
      int n = platforms.size();
      if (n != 0 && currentIndex >= 0 && currentIndex < n) {
         int spanStart;
         int spanEnd;
         // Upstream behaviour (matches the official MTR 4 / Forge build): the band
         // shows the previous station, the current one and the next three.
         if (currentIndex <= 0) {
            spanStart = 0;
            spanEnd = Math.min(n - 1, 3);
         } else if (currentIndex >= n - 1) {
            spanStart = Math.max(0, currentIndex - 2);
            spanEnd = n - 1;
         } else {
            spanStart = Math.max(0, currentIndex - 1);
            spanEnd = Math.min(n - 1, currentIndex + 3);
         }

         int count = spanEnd - spanStart + 1;
         if (count > 0) {
            int bandLineSize = Math.max(2, Math.round((float)lineSize * 0.75F));
            int innerX1 = routeX1 + frameThickness + 2;
            int innerX2 = routeX2 - frameThickness - 2;
            int innerY1 = bandY1 + frameThickness + 2;
            int innerY2 = bandY2 - frameThickness - 2;
            int availH = Math.max(8, innerY2 - innerY1);
            int availW = Math.max(8, innerX2 - innerX1);
            int dotR = Math.max(2, (int)((float)lineSize * 0.42F));
            int lineHalf = Math.max(1, Math.round((float)bandLineSize / 2.0F));
            int badgeGap = Math.max(3, Math.round((float)lineSize * 0.3F));
            int belowNeed = lineHalf + badgeGap + dotR * 2 + 3;
            String[] names = new String[count];
            byte[][] pixels = new byte[count][];
            int[][] dimsAll = new int[count][];
            int[][] dims2x = new int[count][];
            int maxNameW = 1;
            int maxNameH = 1;
            float fontScale = 0.75F;

            for(int attempt = 0; attempt < 6; ++attempt) {
               maxNameW = 1;
               maxNameH = 1;
               int fSB = Math.max(5, (int)((float)fontSizeBig * fontScale));
               int fSS = Math.max(4, (int)((float)fontSizeSmall * fontScale));

               for(int k = 0; k < count; ++k) {
                  names[k] = getFullStationName((Route.RoutePlatform)platforms.get(spanStart + k));
                  int[] dims2 = new int[2];
                  pixels[k] = names[k].isEmpty() ? null : getTextPixelsCached(names[k], dims2, availW * 2, (int)((float)(fSB + fSS) * 1.25F) * 2, fSB * 2, fSS * 2, Math.max(1, fSS / 2), HorizontalAlignment.CENTER);
                  dims2x[k] = dims2;
                  dimsAll[k] = new int[]{Math.max(1, dims2[0] / 2), Math.max(1, dims2[1] / 2)};
                  if (pixels[k] != null) {
                     maxNameW = Math.max(maxNameW, dimsAll[k][0]);
                     maxNameH = Math.max(maxNameH, dimsAll[k][1]);
                  }
               }

               int above = bandLineSize + (int)((float)maxNameW * 0.35F) + (int)(0.354F * (float)(maxNameW + maxNameH)) + 3;
               if (above + belowNeed <= availH || fontScale <= 0.6F) {
                  break;
               }

               fontScale = Math.max(0.6F, fontScale * 0.9F);
            }

            int lineY = Math.max(innerY1 + bandLineSize + 2, innerY2 - belowNeed);
            int nameRightOvershoot = bandLineSize * 3 / 2 + (int)(0.354F * (float)(maxNameW + maxNameH)) + 2;
            int stationX1 = innerX1;
            int stationX2 = Math.max(innerX1 + 1, innerX2 - nameRightOvershoot);
            int spacing = count > 1 ? Math.max(1, (stationX2 - innerX1) / (count - 1)) : 0;
            if (count > 1) {
               int inset = Math.max(nameRightOvershoot, spacing / 2 + 2);
               stationX1 = innerX1 + inset;
               stationX2 = Math.max(stationX1 + 1, innerX2 - inset);
               spacing = Math.max(1, (stationX2 - stationX1) / (count - 1));
            }

            int half = Math.max(1, spacing / 2);
            boolean terminus = currentIndex >= n - 1;
            int[] xs = new int[count];

            for(int k = 0; k < count; ++k) {
               xs[k] = count == 1 ? (innerX1 + innerX2) / 2 : stationX1 + Math.round((float)(stationX2 - stationX1) * (float)k / (float)(count - 1));
               if (flip) {
                  xs[k] = stationX1 + stationX2 - xs[k];
               }
            }

            if (spanStart > 0 && !terminus) {
               int headX = flip ? xs[0] + half : xs[0] - half;
               drawLine(nativeImage, Math.min(xs[0], headX), lineY, Math.abs(headX - xs[0]), 0, Math.abs(headX - xs[0]), -5592406, bandLineSize);
            }

            for(int k = 0; k < count - 1; ++k) {
               int idx = spanStart + k;
               int segLen = Math.abs(xs[k + 1] - xs[k]);
               int segColor = idx < currentIndex ? -5592406 : -16777216 | routeColor;
               drawLine(nativeImage, Math.min(xs[k], xs[k + 1]), lineY, segLen, 0, segLen, segColor, bandLineSize);
            }

            if (spanEnd < n - 1) {
               int lastX = xs[count - 1];
               int tailX = flip ? lastX - half : lastX + half;
               drawLine(nativeImage, Math.min(lastX, tailX), lineY, Math.abs(tailX - lastX), 0, Math.abs(tailX - lastX), -16777216 | routeColor, bandLineSize);
            }

            for(int k = 0; k < count; ++k) {
               int i = spanStart + k;
               int x = xs[k];
               boolean passed = i < currentIndex;
               boolean currentStation = i == currentIndex;
               List<Integer> interchangeColors = new ArrayList();
               List<String> interchangeNames = new ArrayList();
               List<String> interchangeNumbers = new ArrayList();
               collectInterchange((Route.RoutePlatform)platforms.get(i), route, interchangeColors, interchangeNames, interchangeNumbers);
               drawCustomStation(nativeImage, x, lineY, passed, !interchangeColors.isEmpty(), -16777216 | routeColor, bandLineSize);
               if (!interchangeColors.isEmpty()) {
                  drawInterchangeDotsOnly(nativeImage, x, lineY + lineHalf + badgeGap + dotR, interchangeColors, interchangeNumbers, passed, innerY2, dotR);
               }

               int textColor = passed ? -5592406 : (currentStation ? -16777216 | routeColor : -16777216);
               if (pixels[k] != null) {
                  int nameCx = x + bandLineSize * 3 / 2;
                  int nameCy = lineY - bandLineSize - (int)((float)dimsAll[k][0] * 0.35F);
                  drawStringRotatedAA(nativeImage, pixels[k], dims2x[k], nameCx, nameCy, dimsAll[k][0], dimsAll[k][1], textColor, -45.0F);
               }
            }

         }
      }
   }

   private static void drawCustomStation(NativeImage nativeImage, int x, int y, boolean passed, boolean interchange, int color, int sizePx) {
      int ringColor = passed ? -5592406 : color;
      drawStation(nativeImage, x, y, 1.0F, 0, passed, sizePx, lineSpacing);
      if (interchange) {
         int innerRadius = Math.max(1, Math.round((float)sizePx * 0.75F * 0.5F));

         for(int offsetX = -innerRadius; offsetX < innerRadius; ++offsetX) {
            for(int offsetY = -innerRadius; offsetY < innerRadius; ++offsetY) {
               double squareSum = ((double)offsetX + 0.5D) * ((double)offsetX + 0.5D) + ((double)offsetY + 0.5D) * ((double)offsetY + 0.5D);
               if (squareSum <= (double)(innerRadius * innerRadius) && squareSum > 0.5D * (double)innerRadius * (double)innerRadius) {
                  drawPixelSafe(nativeImage, x + offsetX, y + offsetY, ringColor);
               }
            }
         }
      }

   }

   private static void drawInterchangeDotsOnly(com.mojang.blaze3d.platform.NativeImage nativeImage, int cx, int cy, List<Integer> colors, List<String> numbers, boolean passed, int canvasHeight, int dotRParam) {
      int dotR = Math.max(2, dotRParam);
      int gap = dotR * 3;
      int perRow = 2;
      int rows = (colors.size() + 2 - 1) / 2;
      if (rows > 0) {
         int availH = Math.max(0, canvasHeight - cy - dotR - 2);
         int rowGap = dotR * 2 + 2;
         if (rows > 1) {
            rowGap = Math.min(rowGap, Math.max(2, availH / rows));
         }

         for(int c = 0; c < colors.size(); ++c) {
            int rowIndex = c / 2;
            int col = c % 2;
            int nInRow = rowIndex == rows - 1 ? colors.size() - 2 * rowIndex : 2;
            int rowW = (nInRow - 1) * gap;
            int dx = cx - rowW / 2 + col * gap;
            int dy = cy + rowIndex * rowGap;
            int color = passed ? -5592406 : -16777216 | colors.get(c);

            for(int ox = -dotR; ox <= dotR; ++ox) {
               for(int oy = -dotR; oy <= dotR; ++oy) {
                  if (ox * ox + oy * oy <= dotR * dotR) {
                     drawPixelSafe(nativeImage, dx + ox, dy + oy, color);
                  }
               }
            }

            String number = c < numbers.size() ? extractLeadingNumber((String)numbers.get(c)) : "";
            if (!number.isEmpty()) {
               int[] dims = new int[2];
               int numFont = Math.max(4, (int)((float)dotR * 1.15F));
               byte[] px = getTextPixels(number, dims, dotR * 4, (int)((float)(numFont + numFont) * 1.25F), numFont, numFont, Math.max(1, numFont / 4), HorizontalAlignment.CENTER);
               if (px != null) {
                  drawString(nativeImage, px, dx, dy, dims, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, 0, -1, false);
               }
            }
         }

      }
   }

   private static void collectInterchange(Route.RoutePlatform platform, Route currentRoute, List<Integer> colors, List<String> names, List<String> numbers) {
      try {
         if (ClientData.DATA_CACHE == null) {
            return;
         }

         Station station = (Station)ClientData.DATA_CACHE.platformIdToStation.get(platform.platformId);
         if (station == null) {
            return;
         }

         Map<Integer, ClientCache.ColorNameTuple> interchange = (Map)ClientData.DATA_CACHE.stationIdToRoutes.get(station.id);
         if (interchange == null) {
            return;
         }

         List<Integer> sortedColors = new ArrayList(interchange.keySet());
         Collections.sort(sortedColors);

         for(Integer color : sortedColors) {
            ClientCache.ColorNameTuple tuple = (ClientCache.ColorNameTuple)interchange.get(color);
            if (tuple != null && color != currentRoute.color && !colors.contains(color)) {
               colors.add(color);
               names.add(tuple.name == null ? "" : tuple.name);
               numbers.add(extractLeadingNumber(getCjkOnly(tuple.name == null ? "" : tuple.name)));
            }
         }
      } catch (Throwable var11) {
      }

   }

   private static void drawTrainInfoText(NativeImage nativeImage, int width, int y1, int y2, String title, String timeText, String destination, boolean arriving) {
      int x1 = (int)((float)width * 0.733F) + 5;
      int x2 = (int)((float)width * 0.948F) - 5;
      int blockW = x2 - x1;
      int blockH = y2 - y1 - 4;
      int y1i = y1 + 2;
      if (blockW > 0 && blockH > 0) {
         int lineH = blockH / 4;
         if (lineH > 0) {
            int leftX = x1 + (int)((float)blockW * 0.05F);
            int cx = (x1 + x2) / 2;
            int small = (int)((float)lineH * 0.55F);
            int big = (int)((float)lineH * 0.78F);
            String destRaw = destination == null ? "" : destination;
            String destCjk = containsCjk(destRaw) ? getCjkOnly(destRaw) : "";
            String destLatin = getLatinOnly(destRaw);
            drawTextLine(nativeImage, title, blockW, lineH, small, leftX, y1i + lineH / 2, HorizontalAlignment.LEFT, -16777216);
            if (timeText != null && !timeText.isEmpty()) {
               drawTextLine(nativeImage, timeText, blockW, lineH, big, cx, y1i + lineH + lineH / 2, HorizontalAlignment.CENTER, arriving ? -65536 : -16777216);
            }

            if (!destCjk.isEmpty()) {
               drawTextLine(nativeImage, "\u5f00\u5f80 " + destCjk, blockW, lineH, small, leftX, y1i + lineH * 2 + lineH / 2, HorizontalAlignment.LEFT, -16777216);
            }

            if (!destLatin.isEmpty()) {
               drawTextLine(nativeImage, "to " + destLatin, blockW, lineH, small, leftX, y1i + lineH * 3 + lineH / 2, HorizontalAlignment.LEFT, -16777216);
            }

         }
      }
   }

   private static void drawStringRotatedAA(NativeImage nativeImage, byte[] pixels2x, int[] dims2x, int cx, int cy, int dispW, int dispH, int textColor, float degrees) {
      if (pixels2x != null && dims2x != null && dims2x[0] > 0 && dims2x[1] > 0 && dispW > 0 && dispH > 0) {
         int w2 = dims2x[0];
         int h2 = dims2x[1];
         double rad = Math.toRadians((double)degrees);
         double cos = Math.cos(rad);
         double sin = Math.sin(rad);
         double bboxRx = (Math.abs((double)dispW * cos) + Math.abs((double)dispH * sin)) / 2.0D;
         double bboxRy = (Math.abs((double)dispW * sin) + Math.abs((double)dispH * cos)) / 2.0D;
         double srcCx = (double)dispW / 2.0D;
         double srcCy = (double)dispH / 2.0D;
         int rx = (int)Math.ceil(bboxRx) + 1;
         int ry = (int)Math.ceil(bboxRy) + 1;

         for(int dy = -ry; dy <= ry; ++dy) {
            for(int dx = -rx; dx <= rx; ++dx) {
               double u = (double)dx * cos + (double)dy * sin;
               double v = (double)(-dx) * sin + (double)dy * cos;
               int baseX = (int)Math.floor((srcCx + u) * 2.0D);
               int baseY = (int)Math.floor((srcCy + v) * 2.0D);
               int aSum = 0;
               int aCnt = 0;

               for(int ox = 0; ox < 2; ++ox) {
                  for(int oy = 0; oy < 2; ++oy) {
                     int sx = baseX + ox;
                     int sy = baseY + oy;
                     if (sx >= 0 && sx < w2 && sy >= 0 && sy < h2) {
                        aSum += pixels2x[sy * w2 + sx] & 255;
                        ++aCnt;
                     }
                  }
               }

               if (aCnt > 0 && aSum > 0) {
                  int alpha = aSum / aCnt;
                  if (alpha > 0) {
                     PSDTopTextureGenerator.blendPixel(nativeImage, cx + dx, cy + dy, (alpha << 24) + (textColor & 16777215));
                  }
               }
            }
         }

      }
   }

   private static String getFullStationName(Route.RoutePlatform platform) {
      try {
         if (ClientData.DATA_CACHE == null) {
            return "";
         } else {
            Station station = (Station)ClientData.DATA_CACHE.platformIdToStation.get(platform.platformId);
            return station != null && station.name != null ? station.name : "";
         }
      } catch (Throwable var2) {
         return "";
      }
   }

   public static String extractLeadingNumber(String text) {
      if (text == null) {
         return "";
      } else {
         StringBuilder stringBuilder = new StringBuilder();

         for(int i = 0; i < text.length(); ++i) {
            char ch = text.charAt(i);
            if (ch >= '0' && ch <= '9') {
               stringBuilder.append(ch);
            } else if (stringBuilder.length() > 0) {
               break;
            }
         }

         return stringBuilder.toString();
      }
   }

   public static boolean containsCjk(String text) {
      if (text == null) {
         return false;
      } else {
         for(int i = 0; i < text.length(); ++i) {
            if (text.charAt(i) >= 11904) {
               return true;
            }
         }

         return false;
      }
   }

   public static String getCjkOnly(String name) {
      if (name == null) {
         return "";
      } else {
         int index = name.indexOf(124);
         return (index >= 0 ? name.substring(0, index) : name).trim();
      }
   }

   public static String getLatinOnly(String name) {
      if (name == null) {
         return "";
      } else {
         int index = name.indexOf(124);
         if (index >= 0) {
            return index + 1 < name.length() ? name.substring(index + 1).trim() : "";
         } else {
            return containsCjk(name) ? "" : name;
         }
      }
   }

   private static void drawCustomRow(NativeImage nativeImage, int width, int height, int row, float currentAspectRatio, String customText, String customImagePath) {
      float rowV1 = row == 0 ? 0.035F : 0.525F;
      float rowV2 = row == 0 ? 0.475F : 0.965F;
      int y1 = (int)((float)height * rowV1);
      int y2 = (int)((float)height * rowV2);
      if (y2 > y1) {
         int blueX1 = (int)((float)width * 0.05F);
         int blueX2 = (int)((float)width * 0.197F);
         int blueY1 = y1 + (int)((float)(y2 - y1) * 0.04F);
         int blueY2 = y1 + (int)((float)(y2 - y1) * 0.3F);
         boolean hasImage = customImagePath != null && !customImagePath.isEmpty();
         int imageX2 = hasImage ? blueX1 + Math.round((float)width * customImageUFraction(currentAspectRatio)) : blueX1;
         if (hasImage) {
            drawCustomImage(nativeImage, blueX1, blueY1, imageX2, blueY2, customImagePath);
         }

         if (customText != null && !customText.isEmpty()) {
            drawTextLine(nativeImage, customText, Math.max(8, blueX2 - imageX2 - 8), Math.max(6, blueY2 - blueY1 - 6), Math.max(6, (int)((float)(blueY2 - blueY1) * 0.55F)), (imageX2 + blueX2) / 2, (blueY1 + blueY2) / 2, HorizontalAlignment.CENTER, -16777216);
         }

      }
   }

   public static float customImageUFraction(float aspectRatio) {
      float rowH = 0.44F;
      float boxVFrac = 0.11440001F;
      return aspectRatio <= 0.01F ? 0.11440001F : 0.11440001F / aspectRatio;
   }

   private static byte[] getTextPixelsCached(String text, int[] dims, int maxWidth, int maxHeight, int fontSizeBig, int fontSizeSmall, int padding, IGui.HorizontalAlignment alignment) {
      if (text != null && !text.isEmpty()) {
         String key = text + "|" + maxWidth + "|" + maxHeight + "|" + fontSizeBig + "|" + fontSizeSmall + "|" + padding + "|" + alignment;
         byte[] cached = (byte[])TEXT_PIXELS_CACHE.get(key);
         if (cached != null) {
            int[] cachedDims = (int[])TEXT_DIMS_CACHE.get(key);
            if (cachedDims != null) {
               dims[0] = cachedDims[0];
               dims[1] = cachedDims[1];
            }

            return cached;
         } else {
            byte[] pixels = PSDTopTextureGenerator.getTextPixels(text, dims, maxWidth, maxHeight, fontSizeBig, fontSizeSmall, padding, alignment);
            if (pixels != null) {
               if (TEXT_PIXELS_CACHE.size() > 400) {
                  TEXT_PIXELS_CACHE.clear();
                  TEXT_DIMS_CACHE.clear();
               }

               TEXT_PIXELS_CACHE.put(key, pixels);
               TEXT_DIMS_CACHE.put(key, new int[]{dims[0], dims[1]});
            }

            return pixels;
         }
      } else {
         return null;
      }
   }

   private static void drawTextLine(NativeImage nativeImage, String text, int maxWidth, int maxHeight, int fontSize, int x, int cy, IGui.HorizontalAlignment alignment, int textColor) {
      if (text != null && !text.isEmpty() && maxWidth > 0 && maxHeight > 0 && fontSize > 0) {
         int[] dims = new int[2];
         int latinSize = Math.max(1, fontSize * 2 / 3);
         byte[] pixels = getTextPixelsCached(text, dims, maxWidth, maxHeight, fontSize, latinSize, Math.max(1, latinSize / 4), alignment);
         if (pixels != null) {
            drawString(nativeImage, pixels, x, cy, dims, alignment, VerticalAlignment.CENTER, 0, textColor, false);
         }

      }
   }

   private static NativeImage getCustomImageSource(String path) {
      if (path != null && !path.isEmpty()) {
         String key = path + "@" + PSDCustomText.imageVersion(path);
         NativeImage cached = (NativeImage)CUSTOM_IMAGE_SOURCES.get(key);
         if (cached != null) {
            return cached;
         } else if (CUSTOM_IMAGE_SOURCE_FAILED.contains(key)) {
            return null;
         } else {
            try {
               File file = new File(path);
               if (!file.isFile()) {
                  CUSTOM_IMAGE_SOURCE_FAILED.add(key);
                  return null;
               } else {
                  NativeImage source = NativeImage.read(new FileInputStream(file));
                  int sw = source.getWidth();
                  int sh = source.getHeight();
                  if (sw > 0 && sh > 0) {
                     float scaleDown = Math.min(1.0F, Math.min(512.0F / (float)sw, 512.0F / (float)sh));
                     NativeImage scaled;
                     if (scaleDown < 1.0F) {
                        int tw = Math.max(1, Math.round((float)sw * scaleDown));
                        int th = Math.max(1, Math.round((float)sh * scaleDown));
                        scaled = new NativeImage(Format.RGBA, tw, th, false);
                        source.resizeSubRectTo(0, 0, sw, sh, scaled);
                        source.close();
                     } else {
                        scaled = source;
                     }

                     if (CUSTOM_IMAGE_SOURCES.size() > 8) {
                        CUSTOM_IMAGE_SOURCES.clear();
                        CUSTOM_IMAGE_SOURCE_FAILED.clear();
                     }

                     CUSTOM_IMAGE_SOURCES.put(key, scaled);
                     return scaled;
                  } else {
                     source.close();
                     CUSTOM_IMAGE_SOURCE_FAILED.add(key);
                     return null;
                  }
               }
            } catch (Exception var11) {
               CUSTOM_IMAGE_SOURCE_FAILED.add(key);
               return null;
            }
         }
      } else {
         return null;
      }
   }

   private static int[] getCustomImageAreaPixels(String path, int rw, int rh) {
      String key = path + "@" + PSDCustomText.imageVersion(path) + "@" + rw + "x" + rh;
      int[] cached = (int[])CUSTOM_IMAGE_AREA_PIXELS.get(key);
      if (cached != null) {
         return cached;
      } else {
         NativeImage source = getCustomImageSource(path);
         if (source == null) {
            return null;
         } else {
            int sw = source.getWidth();
            int sh = source.getHeight();
            if (sw > 0 && sh > 0) {
               int[] pixels = new int[rw * rh];

               for(int y = 0; y < rh; ++y) {
                  int sy0 = y * sh / rh;
                  int sy1 = Math.max(sy0 + 1, (y + 1) * sh / rh);

                  for(int x = 0; x < rw; ++x) {
                     int sx0 = x * sw / rw;
                     int sx1 = Math.max(sx0 + 1, (x + 1) * sw / rw);
                     long sumR = 0L;
                     long sumG = 0L;
                     long sumB = 0L;
                     int count = 0;

                     for(int sy = sy0; sy < sy1; ++sy) {
                        for(int sx = sx0; sx < sx1; ++sx) {
                           int raw = source.getPixelRGBA(sx, sy);
                           int c0 = raw & 255;
                           int c1 = raw >> 8 & 255;
                           int c2 = raw >> 16 & 255;
                           sumR += (long)c0;
                           sumG += (long)c1;
                           sumB += (long)c2;
                           ++count;
                        }
                     }

                     if (count <= 0) {
                        count = 1;
                     }

                     pixels[y * rw + x] = -16777216 | (int)(sumR / (long)count) << 16 | (int)(sumG / (long)count) << 8 | (int)(sumB / (long)count);
                  }
               }

               if (CUSTOM_IMAGE_AREA_PIXELS.size() > 64) {
                  CUSTOM_IMAGE_AREA_PIXELS.clear();
               }

               CUSTOM_IMAGE_AREA_PIXELS.put(key, pixels);
               return pixels;
            } else {
               return null;
            }
         }
      }
   }

   private static void drawCustomImage(NativeImage nativeImage, int x1, int y1, int x2, int y2, String path) {
      int rw = x2 - x1;
      int rh = y2 - y1;
      if (rw > 0 && rh > 0) {
         int[] pixels = path != null && !path.isEmpty() ? getCustomImageAreaPixels(path, rw, rh) : null;

         for(int y = 0; y < rh; ++y) {
            for(int x = 0; x < rw; ++x) {
               drawPixelSafe(nativeImage, x1 + x, y1 + y, pixels == null ? -16777216 : pixels[y * rw + x]);
            }
         }

      }
   }

   private static int lerpColorAbgr(int colorTop, int colorBottom, float t) {
      float k = Math.max(0.0F, Math.min(1.0F, t));
      int a1 = colorTop >>> 24 & 255;
      int b1 = colorTop >>> 16 & 255;
      int g1 = colorTop >>> 8 & 255;
      int r1 = colorTop & 255;
      int a2 = colorBottom >>> 24 & 255;
      int b2 = colorBottom >>> 16 & 255;
      int g2 = colorBottom >>> 8 & 255;
      int r2 = colorBottom & 255;
      int a = Math.round((float)a1 + (float)(a2 - a1) * k);
      int b = Math.round((float)b1 + (float)(b2 - b1) * k);
      int g = Math.round((float)g1 + (float)(g2 - g1) * k);
      int r = Math.round((float)r1 + (float)(r2 - r1) * k);
      return a << 24 | b << 16 | g << 8 | r;
   }

   public static final class ArrowEntry {
      public final long routeId;
      public final int routeColor;
      public final int platformIndex;
      public final int platformCount;
      public final boolean hidden;
      public final boolean terminating;
      public final String destination;
      public final boolean mtrEmpty;
      public final boolean fallbackResolved;

      private ArrowEntry(long routeId, int routeColor, int platformIndex, int platformCount, boolean hidden, boolean terminating, String destination, boolean mtrEmpty, boolean fallbackResolved) {
         this.routeId = routeId;
         this.routeColor = routeColor;
         this.platformIndex = platformIndex;
         this.platformCount = platformCount;
         this.hidden = hidden;
         this.terminating = terminating;
         this.destination = destination;
         this.mtrEmpty = mtrEmpty;
         this.fallbackResolved = fallbackResolved;
      }
   }

   @FunctionalInterface
   public interface RouteCallback {
      void accept(Route var1, int var2);
   }

   private static class StationPosition {
      private final float x;
      private final float y;
      private final boolean isCommon;

      private StationPosition(float x, float y, boolean isCommon) {
         this.x = x;
         this.y = y;
         this.isCommon = isCommon;
      }
   }
}
