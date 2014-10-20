/*
 * This work is licensed under a Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * http://creativecommons.org/licenses/by-sa/3.0/
 * Authors: Damian Lamers, Fernando van Loenhout
 */
package dlauncher.modpacks.download;

import java.net.URL;
import java.util.Objects;

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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.url);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultDownloadLocation other = (DefaultDownloadLocation) obj;
        if (!Objects.equals(this.url, other.url)) {
            return false;
        }
        return true;
    }

}
