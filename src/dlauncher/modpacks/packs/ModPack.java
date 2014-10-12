package dlauncher.modpacks.packs;

import dlauncher.modpacks.download.DownloadLocation;
import java.net.URL;
import java.util.List;

public interface ModPack {

    ModPack getParent();

    List<ModPackVersion> getVersions();

    String getName();

    String getDescription();

    List<String> getAuthors();

    DownloadLocation getImage();

    URL getDonateURL();

    URL getWebsite();

}
