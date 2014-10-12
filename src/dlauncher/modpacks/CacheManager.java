package dlauncher.modpacks;

import java.io.OutputStream;
import java.net.URL;

public interface CacheManager {
    public void downloadURL(URL url, OutputStream to);
    
    public void addToCache(URL url, byte[] file);
}
