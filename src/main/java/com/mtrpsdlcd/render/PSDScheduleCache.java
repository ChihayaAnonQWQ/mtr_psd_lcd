package com.mtrpsdlcd.render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import mtr.client.ClientData;
import mtr.data.Route;
import mtr.data.ScheduleEntry;

public final class PSDScheduleCache {
   private static final Map<Long, PSDScheduleCache.Entry> CACHE = new ConcurrentHashMap();
   private static final long RESOLVE_INTERVAL_MS = 1000L;
   private static final int REVISION_ROWS = 2;
   private static final int MAX_PLATFORMS = 512;

   private PSDScheduleCache() {
   }

   public static List<PSDScheduleCache.Row> rows(long platformId) {
      return entry(platformId).rows;
   }

   public static String revision(long platformId) {
      return entry(platformId).revision;
   }

   public static int resolves(long platformId) {
      return entry(platformId).resolves;
   }

   public static boolean latched(long platformId) {
      return entry(platformId).latched;
   }

   public static long countdownMinute(long arrivalMillis) {
      long seconds = (arrivalMillis - System.currentTimeMillis()) / 1000L;
      return seconds <= 0L ? 0L : (seconds + 59L) / 60L;
   }

   public static long arrivalRemainingMillis(long platformId) {
      List<PSDScheduleCache.Row> rows = rows(platformId);
      return rows.isEmpty() ? Long.MAX_VALUE : ((PSDScheduleCache.Row)rows.get(0)).arrivalMillis - System.currentTimeMillis();
   }

   private static PSDScheduleCache.Entry entry(long platformId) {
      long now = System.currentTimeMillis();
      PSDScheduleCache.Entry cached = (PSDScheduleCache.Entry)CACHE.get(platformId);
      if (cached != null && now - cached.stamp < 1000L) {
         return cached;
      } else {
         PSDScheduleCache.Entry resolved = resolve(platformId, now, cached);
         if (CACHE.size() > 512) {
            CACHE.clear();
         }

         CACHE.put(platformId, resolved);
         return resolved;
      }
   }

   private static PSDScheduleCache.Entry resolve(long platformId, long now, PSDScheduleCache.Entry previous) {
      List<PSDScheduleCache.Row> resolvedRows = new ArrayList();

      try {
         Set<ScheduleEntry> schedules = null;
         if (ClientData.SCHEDULES_FOR_PLATFORM != null) {
            schedules = (Set)ClientData.SCHEDULES_FOR_PLATFORM.get(platformId);
         }

         if (schedules != null && ClientData.DATA_CACHE != null) {
            for(ScheduleEntry scheduleEntry : schedules) {
               if (scheduleEntry != null) {
                  Route route = (Route)ClientData.DATA_CACHE.routeIdMap.get(scheduleEntry.routeId);
                  if (route != null) {
                     resolvedRows.add(new PSDScheduleCache.Row(scheduleEntry, route));
                  }
               }
            }
         }
      } catch (Throwable var13) {
      }

      Collections.sort(resolvedRows, (a, b) -> a.arrivalMillis == b.arrivalMillis ? Long.compare(a.routeId, b.routeId) : Long.compare(a.arrivalMillis, b.arrivalMillis));
      long[] arrivals = new long[resolvedRows.size()];

      for(int i = 0; i < arrivals.length; ++i) {
         arrivals[i] = ((PSDScheduleCache.Row)resolvedRows.get(i)).arrivalMillis;
      }

      boolean hasPrevious = previous != null && !previous.rows.isEmpty();
      long previousFirstArrival = hasPrevious ? ((PSDScheduleCache.Row)previous.rows.get(0)).arrivalMillis : 0L;
      PSDScheduleLatch.Decision decision = PSDScheduleLatch.decide(arrivals, now, hasPrevious, previousFirstArrival);
      int resolves = previous == null ? 1 : previous.resolves + 1;
      if (decision.liveCount > 0) {
         List<PSDScheduleCache.Row> live = new ArrayList(resolvedRows.subList(decision.expiredCount, resolvedRows.size()));
         return new PSDScheduleCache.Entry(now, revisionOf(live, platformId), Collections.unmodifiableList(live), resolves, false);
      } else if (decision.holdPrevious) {
         return new PSDScheduleCache.Entry(now, revisionOf(previous.rows, platformId), previous.rows, resolves, true);
      } else {
         List<PSDScheduleCache.Row> released = Collections.emptyList();
         return new PSDScheduleCache.Entry(now, revisionOf(released, platformId), released, resolves, false);
      }
   }

   private static String revisionOf(List<PSDScheduleCache.Row> rows, long platformId) {
      StringBuilder revision = new StringBuilder();

      for(int i = 0; i < 2; ++i) {
         if (i < rows.size()) {
            PSDScheduleCache.Row row = (PSDScheduleCache.Row)rows.get(i);
            revision.append(row.routeId).append('#').append(row.currentStationIndex).append('#').append(row.trainCars).append('#').append(countdownMinute(row.arrivalMillis));
         } else {
            revision.append("none");
         }

         revision.append('|');
      }

      revision.append(routeListSignature(platformId));
      return revision.toString();
   }

   private static String routeListSignature(long platformId) {
      StringBuilder stringBuilder = new StringBuilder();

      try {
         for(Route route : RouteMapGenerator.routesAtPlatform(platformId)) {
            stringBuilder.append(route.id).append(':').append(route.name).append(':').append(route.color).append(';');
         }
      } catch (Throwable var5) {
      }

      return stringBuilder.toString();
   }

   private static final class Entry {
      private final long stamp;
      private final String revision;
      private final List<PSDScheduleCache.Row> rows;
      private final int resolves;
      private final boolean latched;

      private Entry(long stamp, String revision, List<PSDScheduleCache.Row> rows, int resolves, boolean latched) {
         this.stamp = stamp;
         this.revision = revision;
         this.rows = rows;
         this.resolves = resolves;
         this.latched = latched;
      }
   }

   public static final class Row {
      public final long routeId;
      public final int currentStationIndex;
      public final int trainCars;
      public final long arrivalMillis;
      public final Route route;
      public final String destination;

      private Row(ScheduleEntry entry, Route route) {
         this.routeId = entry.routeId;
         this.currentStationIndex = entry.currentStationIndex;
         this.trainCars = entry.trainCars;
         this.arrivalMillis = entry.arrivalMillis;
         this.route = route;
         this.destination = route == null ? "" : RouteMapGenerator.getDestination(route, entry.currentStationIndex);
      }
   }
}
