package com.mtrpsdlcd.render;

import com.mojang.blaze3d.platform.NativeImage;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

public final class PSDTextureCache {
   private static final Map<String, PSDTextureCache.Entry> ENTRIES = new ConcurrentHashMap();
   private static final Queue<Runnable> REGISTRY_QUEUE = new ConcurrentLinkedQueue();
   private static final Set<String> GENERATING = ConcurrentHashMap.newKeySet();
   private static final Map<String, Long> LAST_SUBMIT = new ConcurrentHashMap();
   private static final Set<String> GENERATION_LOGGED = ConcurrentHashMap.newKeySet();
   private static final Set<String> REREGISTER_LOGGED = ConcurrentHashMap.newKeySet();
   private static final AtomicInteger GENERATIONS = new AtomicInteger();
   private static final long MIN_RESUBMIT_MS = 250L;
   private static final int MAX_SLOTS = 256;
   public static final ResourceLocation FALLBACK = new ResourceLocation("mtr", "textures/block/transparent.png");
   public static final ResourceLocation WHITE = new ResourceLocation("mtr", "textures/block/white.png");

   private PSDTextureCache() {
   }

   public static PSDTextureCache.Entry get(String key, Supplier<com.mojang.blaze3d.platform.NativeImage> supplier) {
      drainQueue();
      PSDTextureCache.Entry existing = (PSDTextureCache.Entry)ENTRIES.get(key);
      if (existing != null) {
         if (!isStale(existing.identifier)) {
            return existing;
         }

         ENTRIES.remove(key);
      }

      long now = System.currentTimeMillis();
      if (!GENERATING.contains(key) && now - LAST_SUBMIT.getOrDefault(key, 0L) > 250L) {
         if (ENTRIES.size() > 256) {
            clearAll();
         }

         GENERATING.add(key);
         LAST_SUBMIT.put(key, now);
         int generation = GENERATIONS.incrementAndGet();
         if (GENERATION_LOGGED.size() < 512 && GENERATION_LOGGED.add(key)) {
         }

         CompletableFuture.supplyAsync(supplier).thenAccept((image) -> REGISTRY_QUEUE.add((Runnable)() -> register(key, image)));
      }

      return existing == null ? new PSDTextureCache.Entry(FALLBACK, 1, 1, false) : existing;
   }

   public static net.minecraft.resources.ResourceLocation getIdentifier(String key, Supplier<com.mojang.blaze3d.platform.NativeImage> supplier) {
      return get(key, supplier).identifier;
   }

   private static void register(String key, NativeImage image) {
      GENERATING.remove(key);
      if (image != null) {
         try {
            ResourceLocation location = new ResourceLocation("mtr_psd_lcd", "psd_dyn_" + Math.abs(key.hashCode()));
            DynamicTexture texture = new DynamicTexture(image);
            texture.setFilter(true, false);
            Minecraft.getInstance().getTextureManager().register(location, texture);
            PSDTextureCache.Entry previous = (PSDTextureCache.Entry)ENTRIES.put(key, new PSDTextureCache.Entry(location, image.getWidth(), image.getHeight(), true));
            if (previous != null && previous.ready && !previous.identifier.equals(location)) {
               releaseTexture(previous.identifier);
            }

            if (previous != null && previous.ready && REREGISTER_LOGGED.size() < 512 && REREGISTER_LOGGED.add(key)) {
            }
         } catch (Throwable var5) {
         }

      }
   }

   private static void drainQueue() {
      for(int i = 0; i < 2; ++i) {
         Runnable task = (Runnable)REGISTRY_QUEUE.poll();
         if (task == null) {
            return;
         }

         try {
            task.run();
         } catch (Throwable var3) {
         }
      }

   }

   private static boolean isStale(ResourceLocation location) {
      if (FALLBACK.equals(location)) {
         return false;
      } else {
         try {
            AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(location);
            return texture == null || texture.getId() <= 0;
         } catch (Throwable var2) {
            return false;
         }
      }
   }

   private static void releaseTexture(ResourceLocation location) {
      try {
         TextureManager textureManager = Minecraft.getInstance().getTextureManager();
         textureManager.release(location);
      } catch (Throwable var2) {
      }

   }

   public static void clearAll() {
      for(PSDTextureCache.Entry entry : ENTRIES.values()) {
         if (entry.ready) {
            releaseTexture(entry.identifier);
         }
      }

      ENTRIES.clear();
   }

   public static final class Entry {
      public final ResourceLocation identifier;
      public final int width;
      public final int height;
      public final boolean ready;

      private Entry(ResourceLocation identifier, int width, int height, boolean ready) {
         this.identifier = identifier;
         this.width = width;
         this.height = height;
         this.ready = ready;
      }
   }
}
