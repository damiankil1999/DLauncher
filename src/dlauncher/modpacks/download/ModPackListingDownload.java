package dlauncher.modpacks.download;

import dlauncher.modpacks.packs.UnresolvedModPack;
import java.io.IOException;
import java.util.Collection;

public interface ModPackListingDownload extends DownloadLocation {
    public Collection<? extends UnresolvedModPack> readModPacksFromResource(
            byte[] resourceBytes) throws IOException;
}
