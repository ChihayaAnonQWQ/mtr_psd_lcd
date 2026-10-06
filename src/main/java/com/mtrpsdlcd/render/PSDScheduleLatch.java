package com.mtrpsdlcd.render;

public final class PSDScheduleLatch {
   public static final long PASSED_GRACE_MILLIS = 60000L;

   private PSDScheduleLatch() {
   }

   public static PSDScheduleLatch.Decision decide(long[] arrivalsAscending, long now, boolean hasPreviousNonEmpty, long previousFirstArrivalMillis) {
      return decide(arrivalsAscending, now, hasPreviousNonEmpty, previousFirstArrivalMillis, 60000L);
   }

   public static PSDScheduleLatch.Decision decide(long[] arrivalsAscending, long now, boolean hasPreviousNonEmpty, long previousFirstArrivalMillis, long graceMillis) {
      int expired;
      for(expired = 0; expired < arrivalsAscending.length && expired(arrivalsAscending[expired], now, graceMillis); ++expired) {
      }

      int liveCount = arrivalsAscending.length - expired;
      if (liveCount > 0) {
         return new PSDScheduleLatch.Decision(liveCount, false, expired);
      } else {
         boolean hold = hasPreviousNonEmpty && !expired(previousFirstArrivalMillis, now, graceMillis);
         return new PSDScheduleLatch.Decision(0, hold, expired);
      }
   }

   public static boolean expired(long arrivalMillis, long now, long graceMillis) {
      return now - arrivalMillis > graceMillis;
   }

   public static final class Decision {
      public final int liveCount;
      public final boolean holdPrevious;
      public final int expiredCount;

      private Decision(int liveCount, boolean holdPrevious, int expiredCount) {
         this.liveCount = liveCount;
         this.holdPrevious = holdPrevious;
         this.expiredCount = expiredCount;
      }

      public int shownCount(int previousCount) {
         if (this.liveCount > 0) {
            return this.liveCount;
         } else {
            return this.holdPrevious ? previousCount : 0;
         }
      }

      public String toString() {
         return "liveCount=" + this.liveCount + " holdPrevious=" + this.holdPrevious + " expiredCount=" + this.expiredCount;
      }
   }
}
