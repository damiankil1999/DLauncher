/*
 * This work is licensed under a Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * http://creativecommons.org/licenses/by-sa/3.0/
 * Authors: Damian Lamers, Fernando van Loenhout
 */
package dlauncher.modpacks.packs;

import dlauncher.modpacks.download.ModPackDownload;
import java.net.URL;
import java.util.List;

public class SimpleModPackVersion implements ModPackVersion {

    private final ModPack main;
    private final String branch;
    private final ModPackVersion parent;
    private final long installedSize;
    private final List<ModPackDownload> downloads;
    private final URL changeLog;
    private final String version;
    private final long releasedate;

    public SimpleModPackVersion(ModPack main, String branch,
            ModPackVersion parent, long installedSize,
            List<ModPackDownload> downloads, URL changeLog, String version,
            long releasedate) {
        this.main = main;
        this.branch = branch;
        this.parent = parent;
        this.installedSize = installedSize;
        this.downloads = downloads;
        this.changeLog = changeLog;
        this.version = version;
        this.releasedate = releasedate;
    }

    @Override
    public ModPack getMainInstance() {
        return main;
    }

    @Override
    public ModPackVersion getParent() {
        return parent;
    }

    @Override
    public long getInstalledSize() {
        return installedSize;
    }

    @Override
    public List<ModPackDownload> getDownloadLocations() {
        return downloads;
    }

    @Override
    public URL getChangeLog() {
        return changeLog;
    }

    @Override
    public long getReleaseDate() {
        return releasedate;
    }

    @Override
    public String getBranch() {
        return branch;
    }

    @Override
    public String getModPackName() {
        return main.getName();
    }

    @Override
    public String getVersion() {
        return version;
    }
    
    @Override
    public int compareTo(ModPackVersion o) {
        return Long.compare(this.getReleaseDate(), o.getReleaseDate());
    }
}
