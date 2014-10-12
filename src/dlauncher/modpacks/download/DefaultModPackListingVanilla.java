package dlauncher.modpacks.download;

import dlauncher.modpacks.packs.UnresolvedModPack;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class DefaultModPackListingVanilla extends DefaultDownloadLocation
        implements ModPackListingDownload {

    public DefaultModPackListingVanilla(URL url) {
        super(url);
    }

    public DefaultModPackListingVanilla(URL url, long size, byte[] md5,
            byte[] sha512) {
        super(url, size, md5, sha512);
    }

    @Override
    public Collection<? extends UnresolvedModPack> readModPacksFromResource(
            byte[] resourceBytes) throws IOException {
        JSONObject json = new JSONObject(new String(resourceBytes,
                Charset.forName("UTF-8")));
        List<UnresolvedModPack> version = new ArrayList<>();
        JSONArray versions = json.getJSONArray("versions");
        for (int i = 0; i < versions.length(); i++) {
            
        }

    }

}
