/*
 * This work is licensed under a Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * http://creativecommons.org/licenses/by-sa/3.0/
 * Authors: Damian Lamers, Fernando van Loenhout
 */
package dlauncher.modpacks.packs;

import dlauncher.modpacks.download.ModPackDownload;
import java.net.URL;
import java.util.List;

public interface ModPackVersion extends ModPackVersionDescription, 
        Comparable<ModPackVersion> {

    ModPack getMainInstance();

    ModPackVersion getParent();

    long getInstalledSize();

    List<ModPackDownload> getDownloadLocations();

    URL getChangeLog();
    
    long getReleaseDate();
    
    String getBranch();

}
