package com.mtrpsdlcd.render;

public final class PSDTextureKeys {
   public static final int SRC_CHAR_WIDTH_CJK = 30;
   public static final int SRC_CHAR_WIDTH_LATIN = 16;
   public static final int SRC_TEXT_MIN_WIDTH = 30;

   private PSDTextureKeys() {
   }

   public static boolean isCjkChar(int codePoint) {
      return codePoint >= 11904;
   }

   public static int textSourceWidth(String text) {
      if (text != null && !text.isEmpty()) {
         int width = 0;

         for(int index = 0; index < text.length(); ++index) {
            width += isCjkChar(text.charAt(index)) ? 30 : 16;
         }

         return Math.max(30, width);
      } else {
         return 30;
      }
   }

   public static String ledText(String text, int textColor) {
      return "ledtext_" + text + "_" + textColor;
   }

   public static String routeBadge(String routeName, int routeColor) {
      return "routebadge_" + routeName + "_" + routeColor;
   }

   public static String mtrStation(String stationName, float aspectRatio) {
      return "mtrstation_" + stationName + "_" + (int)(aspectRatio * 1000.0F);
   }

   public static String platformArrow(long platformId, boolean hasLeft, boolean hasRight, boolean showPlatform, float aspectRatio, String signature) {
      return "platformarrow_" + platformId + "_" + hasLeft + "_" + hasRight + "_" + showPlatform + "_" + (int)(aspectRatio * 1000.0F) + "_" + signature;
   }

   public static String glassRoute(long platformId, float aspectRatio, boolean flip, String signature) {
      return "glassroute_" + platformId + "_" + (int)(aspectRatio * 1000.0F) + "_" + flip + "_" + signature;
   }

   public static String perRoute(long platformId, long routeId, float aspectRatio, boolean flip, String signature) {
      return "perroute_" + platformId + "_" + routeId + "_" + (int)(aspectRatio * 1000.0F) + "_" + flip + "_" + signature;
   }

   public static String grooved(long platformId, String scheduleKey, boolean leftDirection, float aspectRatio, String signature, String customText, String customImagePath, String imageVersion, long pageRouteId) {
      return "grooved_" + platformId + "_" + scheduleKey + "_" + leftDirection + "_" + (int)(aspectRatio * 1000.0F) + "_" + signature + "#" + customText + "#" + customImagePath + "#" + imageVersion + "#p" + pageRouteId;
   }
}
