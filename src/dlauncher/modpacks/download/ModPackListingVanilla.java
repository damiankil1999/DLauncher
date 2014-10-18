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
import org.json.JSONArray;
import org.json.JSONObject;

public class ModPackListingVanilla extends DefaultDownloadLocation
        implements ModPackListingDownload {

    public ModPackListingVanilla(URL url) {
        super(url);
    }

    public ModPackListingVanilla(URL url, long size, byte[] md5,
            byte[] sha512) {
        super(url, size, md5, sha512);
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
                ), null);
        UnresolvedModPack unresolved = new UnresolvedModPack() {

            @Override
            public List<ModPackVersionDescription> getRequiredDependencies() {
                return Collections.emptyList();
            }

            @Override
            public ModPack createModPack(List<ModPack> dependencies) {
                return modpack;
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
