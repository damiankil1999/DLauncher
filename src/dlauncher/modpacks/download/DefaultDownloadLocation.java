package dlauncher.modpacks.download;

import java.net.URL;

public class DefaultDownloadLocation implements DownloadLocation {

    public static final int DEFAULT_CACHE_PRIORITY = 1;
    
    private final URL url;
    private final long size;
    private final byte[] md5;
    private final byte[] sha512;
    private final int cachePriority;

    public DefaultDownloadLocation(URL url, int priority) {
        this(url, -1, null, null, priority);
    }
    
    public DefaultDownloadLocation(URL url) {
        this(url, -1, null, null);
    }

    public DefaultDownloadLocation(URL url, long size, byte[] md5,
            byte[] sha512) {
        this(url, size, md5, sha512, DEFAULT_CACHE_PRIORITY);
    }

    public DefaultDownloadLocation(URL url, long size, byte[] md5,
            byte[] sha512, int cachePriority) {
        this.url = url;
        this.size = size;
        this.md5 = md5;
        this.sha512 = sha512;
        this.cachePriority = cachePriority;
    }

    @Override
    public URL getURL() {
        return url;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public byte[] getMD5() {
        return md5;
    }

    @Override
    public byte[] getSHA512() {
        return sha512;
    }

    @Override
    public int getCachePriority() {
        return cachePriority;
    }

}
