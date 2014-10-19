/*
 * This work is licensed under a Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * http://creativecommons.org/licenses/by-sa/3.0/
 * Authors: Damian Lamers, Fernando van Loenhout
 */
package dlauncher.cache;

import dlauncher.modpacks.download.DownloadLocation;
import java.io.IOException;
import java.io.OutputStream;
import javax.swing.JLabel;

public interface CacheManager {

    public void downloadURL(DownloadLocation url, OutputStream to)
            throws IOException;
    
    public byte[] downloadURL(DownloadLocation url) throws IOException;

    public void lazyLoadImageToLabel(DownloadLocation url, JLabel target,
            int heigth, int width, boolean mayResize);
}
