package dlauncher.modpacks.packs;

import dlauncher.modpacks.download.ModPackDownload;
import java.net.URL;
import java.util.List;

public interface ModPackVersion extends ModPackVersionDescription {

    ModPack getMainInstance();

    ModPackVersion getParent();

    long getInstalledSize();

    List<ModPackDownload> getDownloadLocations();

    URL getChangeLog();
    
    long getReleaseDate();
    
    String getBranch();

}
