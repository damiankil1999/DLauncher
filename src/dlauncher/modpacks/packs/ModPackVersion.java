package dlauncher.modpacks.packs;

import dlauncher.modpacks.download.ModPackDownload;
import java.net.URL;
import java.util.List;

public interface ModPackVersion {

    ModPack getMainInstance();

    ModPackVersion getParent();

    String getVersion();

    long getInstalledSize();

    List<ModPackDownload> getDownloadLocations();

    URL getChangeLog();

}
