/*
 * This work is licensed under a Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * http://creativecommons.org/licenses/by-sa/3.0/
 * Authors: Damian Lamers, Fernando van Loenhout
 */
package dlauncher.modpacks.download;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import org.json.JSONException;
import org.json.JSONObject;

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

    public static DownloadLocation valueOf(JSONObject obj)
            throws MalformedURLException, JSONException {
        byte[] md5 = null;
        byte[] sha512 = null;
        long fileLength = -1;
        String optMd5 = obj.optString("md5", null);
        String optSha512 = obj.optString("sha512", null);
        if(optMd5 != null) {
            md5 = hexStringToByteArray(optMd5);
        }
        if(optSha512 != null) {
            sha512 = hexStringToByteArray(optSha512);
        }
        fileLength = obj.optLong("size", fileLength);
        String urlStr = obj.getString("url");
        URL url;
        if(urlStr.startsWith("default:")) {
            url = DefaultDownloadLocation.class.getResource(urlStr.substring("default:".length()));
        } else {
            url = new URL(urlStr);
        }
        return new DefaultDownloadLocation(url, fileLength, md5, sha512);
    }

    public static DownloadLocation valueOf(String obj)
            throws MalformedURLException {
        byte[] md5 = null;
        byte[] sha512 = null;
        long fileLength = -1;
        URL url;
        if(obj.startsWith("default:")) {
            url = DefaultDownloadLocation.class.getResource(obj.substring("default:".length()));
        } else {
            url = new URL(obj);
        }
        return new DefaultDownloadLocation(url, fileLength, md5, sha512);
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    @Override
    public String toString() {
        return "DefaultDownloadLocation{" + "url=" + url + ", size=" + size + ", md5=" + md5 + ", sha512=" + sha512 + ", cachePriority=" + cachePriority + '}';
    }
}
