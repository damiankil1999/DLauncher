/*
 * This work is licensed under a Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * http://creativecommons.org/licenses/by-sa/3.0/
 * Authors: Damian Lamers, Fernando van Loenhout
 */
package dlauncher.modpacks.download;

import dlauncher.modpacks.packs.DefaultModPack;
import dlauncher.modpacks.packs.ModPack;
import dlauncher.modpacks.packs.ModPackVersion;
import dlauncher.modpacks.packs.ModPackVersionDescription;
import dlauncher.modpacks.packs.UnresolvedModPack;
import dlauncher.modpacks.packs.VanillaModPackVersion;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class ModPackListingVanilla extends DefaultDownloadLocation
        implements ModPackListingDownload {

    public ModPackListingVanilla(URL url) {
        super(url, 10);
    }

    public ModPackListingVanilla(URL url, long size, byte[] md5,
            byte[] sha512) {
        super(url, size, md5, sha512, 10);
    }

    @Override
    public Collection<? extends UnresolvedModPack> readModPacksFromResource(
            byte[] resourceBytes) throws IOException {
        JSONObject json = new JSONObject(new String(resourceBytes,
                Charset.forName("UTF-8")));
        final DefaultModPack modpack = new DefaultModPack("Vanilla",
                Arrays.asList("Mojang AB"), null,
                "This is the vanilla minecraft",
                null, new DefaultDownloadLocation(
                        this.getClass().getResource("/images/TEMP_Vanilla.png")
                ), new URL("https://minecraft.net/"));
        UnresolvedModPack unresolved = new UnresolvedModPack() {

            @Override
            public List<ModPackVersionDescription> getRequiredDependencies() {
                return Collections.emptyList();
            }

            @Override
            public ModPack createModPack(Map<String, ModPack> dependencies) {
                return modpack;
            }

            @Override
            public String getName() {
                return modpack.getName();
            }
        };
        JSONArray versions = json.getJSONArray("versions");
        for (int i = 0; i < versions.length(); i++)
            modpack.addModPackVersion(
                    new VanillaModPackVersion(versions.getJSONObject(i), modpack
                    ));
        return Collections.singletonList(unresolved);
    }

}
