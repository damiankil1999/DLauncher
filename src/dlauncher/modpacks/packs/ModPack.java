package dlauncher.modpacks.packs;

import dlauncher.modpacks.download.DownloadLocation;
import java.net.URL;
import java.util.List;
import java.util.Map;

public interface ModPack {

    ModPack getParent();

    Map<String, ModPackVersion> getVersions();

    String getName();

    String getDescription();

    List<String> getAuthors();

    DownloadLocation getImage();

    URL getDonateURL();

    URL getWebsite();

}
