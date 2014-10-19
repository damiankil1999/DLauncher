/*
 * This work is licensed under a Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * http://creativecommons.org/licenses/by-sa/3.0/
 * Authors: Damian Lamers, Fernando van Loenhout
 */
package dlauncher.cache;

import dlauncher.modpacks.download.DownloadLocation;
import java.io.IOException;

public class MemoryCache implements CacheStorage {

    private final AdvancedCacheResult[] cache;
    private final int lowestStorePriority;
    private int index;

    public MemoryCache(int size, int lowestStorePriority) {
        this.lowestStorePriority = lowestStorePriority;
        this.cache = new AdvancedCacheResult[size];
    }

    @Override
    public synchronized CacheResult getResource(DownloadLocation url) {
        for (AdvancedCacheResult cache1 : cache) {
            if (cache1 != null && cache1.url.getURL().equals(url.getURL())) {
                return cache1;
            }
        }
        return null;
    }

    @Override
    public synchronized void saveResource(DownloadLocation url,
            CacheResult result) {
        if (url.getCachePriority() < lowestStorePriority) {
            return;
        }
        int lowestPriority = Integer.MAX_VALUE;
        int lowestPrioritySlot = 0;
        for (int i = 0; i < cache.length; i++) {
            int j = index + i;
            if (j > cache.length) {
                j -= cache.length;
            }
            if (cache[j] == null) {
                cache[j] = new AdvancedCacheResult(result.getResource(),
                        result.getException(), url);
                index = j;
                return;
            } else {
                if (cache[j].url.getCachePriority() <= lowestPriority) {
                    lowestPrioritySlot = j;
                }
            }
        }
        cache[lowestPrioritySlot] = new AdvancedCacheResult(
                result.getResource(), result.getException(), url);
        index = lowestPrioritySlot;
    }

    @Override
    public int getMaxSize() {
        return cache.length;
    }

    @Override
    public int getRecommendSize() {
        return cache.length;
    }

    @Override
    public int getStoredSize() {
        int size = 0;
        for (AdvancedCacheResult cache1 : cache) {
            if (cache1 != null) {
                size++;
            }
        }
        return size;
    }

    @Override
    public int purgeUnusedResources(int resources) {
        return 0;
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }

    @Override
    public int getLowestStorePriority() {
        return this.lowestStorePriority;
    }

    public class AdvancedCacheResult implements CacheResult {

        private final byte[] resource;
        private final IOException exception;
        private int hits = 0;
        private final DownloadLocation url;

        public AdvancedCacheResult(byte[] resource, IOException exception,
                DownloadLocation url) {
            this.resource = resource;
            this.exception = exception;
            this.url = url;
        }

        @Override
        public byte[] getResource() {
            return resource;
        }

        @Override
        public IOException getException() {
            return exception;
        }

        @Override
        public byte[] getResult() throws IOException {
            if (exception != null) {
                throw new IOException("IOException inside cache: " + exception.toString(), exception);
            }
            return resource;
        }

        public void increaseHits(int hits) {
            this.hits += hits;
        }

        public int getHits() {
            return hits;
        }
    }
}
