/*
 * This work is licensed under a Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * http://creativecommons.org/licenses/by-sa/3.0/
 * Authors: Damian Lamers, Fernando van Loenhout
 */
package dlauncher.cache;

import dlauncher.modpacks.download.DownloadLocation;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;

public class DefaultCacheManager implements CacheManager {

    private final List<CacheStorage> caches;

    public DefaultCacheManager(List<CacheStorage> caches,
            ExecutorService labelLoadPool) {
        if (caches == null) {
            throw new IllegalArgumentException("caches == null");
        }
        if (labelLoadPool == null)
            throw new IllegalArgumentException("labelLoadPool == null");
        this.caches = caches;
    }

    @Override
    public void downloadURL(DownloadLocation url, OutputStream to) throws IOException {
        CacheResult resource;
        for (CacheStorage cache : caches) {
            if ((resource = cache.getResource(url)) != null) {
                to.write(resource.getResult());
            }
        }
        to.write(this.downloadURL0(url));
    }

    @Override
    public byte[] downloadURL(DownloadLocation url) throws IOException {
        CacheResult resource;
        for (CacheStorage cache : caches) {
            if ((resource = cache.getResource(url)) != null) {
                byte[] bytes = resource.getResult();
                byte[] copy = new byte[bytes.length];
                System.arraycopy(bytes, 0, copy, 0, copy.length);
                return copy;
            }
        }
        byte[] bytes = this.downloadURL0(url);
        byte[] copy = new byte[bytes.length];
        System.arraycopy(bytes, 0, copy, 0, copy.length);
        return copy;
    }

    private byte[] downloadURL0(DownloadLocation next) throws IOException {
        try {
            byte[] resource = downloadURL1(next);
            for (CacheStorage cache : caches) {
                if (cache.getLowestStorePriority() <= next.getCachePriority()) {
                    cache.saveResource(next,
                            new DefaultCacheResult(resource, null));
                }
            }
            return resource;
        } catch (IOException err) {
            for (CacheStorage cache : caches) {
                if (cache.getLowestStorePriority() <= next.getCachePriority()) {
                    cache.saveResource(next,
                            new DefaultCacheResult(null, err));
                }
            }
            throw err;
        }
    }

    private byte[] downloadURL1(DownloadLocation next) throws IOException {

        long sizeL = next.getSize();
        if (sizeL > Integer.MAX_VALUE) {
            Logger.getGlobal().log(Level.WARNING, "url({0}" + ").size({1}" + ")"
                    + " is longer than maxium allowed({2})", new Object[]{
                        next.toString(), next.getSize(), Integer.MAX_VALUE});
        }
        int size = sizeL < 0 ? Integer.MAX_VALUE : (int) sizeL;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (InputStream in = next.getURL().openStream()) {
            byte[] copy = new byte[1024 * 8];
            int read;
            int totalRead = 0;
            while ((read = in.read(copy)) != -1) {
                totalRead += read;
                if (totalRead > size) {
                    throw new IOException(String.format(
                            "File %s larger than size adversized(%s)",
                            next.toString(), next.getSize()));
                }
                out.write(copy, 0, read);
            }
        }
        byte[] data = out.toByteArray();
        if (next.getSize() != -1 && next.getSize() != data.length) {
            throw new IOException("Resource length mismatch! "
                    + "Excepted length " + next.getSize()
                    + ", but got " + data.length);
        }
        boolean containsHashes = false;
        {
            if (next.getMD5() != null) {
                containsHashes = true;
                MessageDigest md;
                try {
                    md = MessageDigest.getInstance("MD5");
                } catch (NoSuchAlgorithmException ex) {
                    throw new IOException("Cannot has MD5", ex);
                }
                byte[] md5 = md.digest(data);
                if (!Arrays.equals(md5, next.getMD5())) {
                    throw new IOException("Resource MD5 mismatch! "
                            + "Excepted hash " + Arrays.toString(next.getMD5())
                            + ", but got " + Arrays.toString(md5));
                }
            }
            if (next.getSHA512() != null) {
                containsHashes = true;
                MessageDigest md;
                try {
                    md = MessageDigest.getInstance("SHA-512");
                } catch (NoSuchAlgorithmException ex) {
                    throw new IOException("Cannot has SHA-512", ex);
                }
                byte[] sha512 = md.digest(data);
                if (!Arrays.equals(sha512, next.getSHA512())) {
                    throw new IOException("Resource SHA512 mismatch! "
                            + "Excepted hash " + Arrays.toString(
                                    next.getSHA512())
                            + ", but got " + Arrays.toString(sha512));
                }
            }
        }
        if (!containsHashes) {
            Logger.getGlobal().log(Level.FINE,
                    "Resource {0} doesn''t contain any hashes, file cannot be"
                    + " checked if its downloaded correctly ",
                    next.getURL());
        }
        return data;
    }

    @Override
    public void lazyLoadImageToLabel(DownloadLocation url, JLabel target, int heigth, int width, boolean mayResize) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
