/*
 * This work is licensed under a Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * http://creativecommons.org/licenses/by-sa/3.0/
 * Authors: Damian Lamers, Fernando van Loenhout
 */
package dlauncher.cache;

import dlauncher.modpacks.download.DownloadLocation;
import java.io.Closeable;

/**
 *
 * @author Fernando
 */
public interface CacheStorage extends Closeable {

    public CacheResult getResource(DownloadLocation url) ;
    
    public void saveResource(DownloadLocation url, CacheResult result);
    
    public int getMaxSize();
    
    /**
     * Returns the amoutn of resources that this cache inside ideal times have.
     * If the amount of resources is higher than this number, the cache MAY
     * remove some of the files when saving, but this is not required
     * @return the recommand size
     */
    public int getRecommendSize();
    
    public int getStoredSize();
    
    /**
     * Removed old resources from the cache
     * @param resources amount resources to clear, -1 to clear all
     * @return this returns the amount of resources this method has cleaned
     */
    public int purgeUnusedResources(int resources);
    
    public void flush();
    
    @Override
    public void close();
    
    /**
     * Returns the lowest priority of DownloadLocations that will be 
     * stored inside the cache
     * @return 
     */
    public int getLowestStorePriority();
    
}
