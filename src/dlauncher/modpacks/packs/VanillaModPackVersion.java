/*
 * This work is licensed under a Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * http://creativecommons.org/licenses/by-sa/3.0/
 * Authors: Damian Lamers, Fernando van Loenhout
 */
package dlauncher.modpacks.packs;

import dlauncher.modpacks.download.ModPackDownload;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

public class VanillaModPackVersion implements ModPackVersion {

    private final ModPack main;
    private final String version;
    private final String branch;
    private final long installedSize;
    private final long releaseDate;
    private final List<ModPackDownload> downloads;
    private static final SimpleDateFormat dateFormat
            = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

    public VanillaModPackVersion(JSONObject jsonObject, ModPack main) {
        this.main = main;
        this.branch = jsonObject.getString("type");
        this.version = jsonObject.getString("id");
        try {
            this.releaseDate = dateFormat.parse(
                    jsonObject.getString("releaseTime")).getTime();
        } catch (ParseException ex) {
            throw new Error(ex);
        }
        this.installedSize = -1;
        this.downloads = Collections.emptyList();
    }

    @Override
    public ModPack getMainInstance() {
        return this.main;
    }

    @Override
    public ModPackVersion getParent() {
        return null;
    }

    @Override
    public String getVersion() {
        return version;
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
        return null;
    }

    @Override
    public long getReleaseDate() {
        return releaseDate;
    }

    @Override
    public String getBranch() {
        return branch;
    }

    @Override
    public String getModPackName() {
        return this.getMainInstance().getName();
    }
}
