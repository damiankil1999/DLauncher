/*
 * This work is licensed under a Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * http://creativecommons.org/licenses/by-sa/3.0/
 * Authors: Damian Lamers, Fernando van Loenhout
 */
package dlauncher.modpacks.download;

import dlauncher.modpacks.packs.UnresolvedModPack;
import java.io.IOException;
import java.util.Collection;

public interface ModPackListingDownload extends DownloadLocation {
    public Collection<? extends UnresolvedModPack> readModPacksFromResource(
            byte[] resourceBytes) throws IOException;
}
