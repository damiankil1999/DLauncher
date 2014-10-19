/*
 * This work is licensed under a Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * http://creativecommons.org/licenses/by-sa/3.0/
 * Authors: Damian Lamers, Fernando van Loenhout
 */
package dlauncher.modpacks.download;

import java.net.URL;

public interface DownloadLocation {

    URL getURL();

    long getSize();

    byte[] getMD5();

    byte[] getSHA512();
    
    int getCachePriority();
}
