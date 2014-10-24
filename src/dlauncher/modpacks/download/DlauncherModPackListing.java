/*
 * This work is licensed under a Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * http://creativecommons.org/licenses/by-sa/3.0/
 * Authors: Damian Lamers, Fernando van Loenhout
 */
package dlauncher.modpacks.download;

import dlauncher.modpacks.packs.DefaultModPack;
import dlauncher.modpacks.packs.DefaultModPackVersionDescription;
import dlauncher.modpacks.packs.ModPack;
import dlauncher.modpacks.packs.ModPackVersion;
import dlauncher.modpacks.packs.ModPackVersionDescription;
import dlauncher.modpacks.packs.SimpleModPackVersion;
import dlauncher.modpacks.packs.UnresolvedModPack;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DlauncherModPackListing extends DefaultDownloadLocation
        implements ModPackListingDownload {

    public DlauncherModPackListing(URL url) {
        super(url, 10);
    }

    public DlauncherModPackListing(URL url, long size, byte[] md5,
            byte[] sha512) {
        super(url, size, md5, sha512, 10);
    }

    @Override
    public Collection<? extends UnresolvedModPack> readModPacksFromResource(
            byte[] resourceBytes) throws IOException {
        try {
            JSONObject json = new JSONObject(new String(resourceBytes,
                    Charset.forName("UTF-8")));
            List<UnresolvedModPack> mods = new ArrayList<>();
            JSONObject list = json.getJSONObject("ModPacks");
            for (String key : list.keySet()) {
                mods.add(new SimpleModPack(list.getJSONObject(key), key));
            }
            return mods;
        } catch (JSONException jsonException) {
            throw new IOException("Problem while reading json: " + 
                    jsonException.toString(), jsonException);
        } catch (EmptyStackException exception) {
            throw new IOException("No valid modpack versions found!", exception);
        }
    }

    private static class SimpleModPack implements UnresolvedModPack {

        private final JSONObject modpackJson;

        private final Set<ModPackVersionDescription> dependencies;

        private final List<JSONObject> versions;

        private final String modpackName;

        public SimpleModPack(JSONObject modpackJson, String modpackName) {
            this.modpackJson = modpackJson;
            this.modpackName = modpackName;
            Set<ModPackVersionDescription> dependencies = new HashSet<>();
            List<JSONObject> versions = new ArrayList<>();
            JSONArray array = this.modpackJson.getJSONArray("Versions");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String version = object.getString("Version");
                String optParents = object.optString("Parent", null);
                if (optParents != null) {
                    String[] split = optParents.split(":", 2);
                    if (split.length != 2) {
                        Logger.getGlobal().log(Level.WARNING,
                                "Problem while parsing modpack {0}:{1}",
                                new Object[]{modpackName, version});
                        continue;
                    }
                    String modname = split[0];
                    String modVersion = split[1];
                    dependencies.add(new DefaultModPackVersionDescription(
                            modname, modVersion));
                }
                versions.add(object);
            }
            if(versions.isEmpty()) {
                throw new EmptyStackException();
            }
            this.versions = Collections.unmodifiableList(versions);
            this.dependencies = Collections.unmodifiableSet(dependencies);
        }

        @Override
        public Collection<? extends ModPackVersionDescription>
                getRequiredDependencies() {
            return dependencies;
        }

        @Override
        public ModPack createModPack(Map<String, ModPack> dependencies) {
            List<String> authors = new ArrayList<>();
            JSONArray authorsJson = this.modpackJson.optJSONArray("Authors");
            String authorsString;
            if (authorsJson != null) {
                for (int i = 0; i < authorsJson.length(); i++) {
                    if ((authorsString = authorsJson.optString(i, null))
                            != null) {
                        authors.add(authorsString);
                    }
                }
            }
            if ((authorsString = modpackJson.optString("Authors", null))
                    != null) {
                authors.add(authorsString);
            }
            URL donateURL = null;
            String donateStr = this.modpackJson.optString("DonateLink", null);
            try {
                if (donateStr != null) {
                    donateURL = new URL(donateStr);
                }
            } catch (MalformedURLException ex) {
                Logger.getGlobal().log(Level.SEVERE,
                        "Problem parsing " + modpackName + ".DonateLink", ex);
            }
            DownloadLocation icon;
            Object get = this.modpackJson.get("Icon");
            try {
                if (get instanceof JSONObject) {
                    icon = DefaultDownloadLocation.valueOf((JSONObject) get);
                } else if (get instanceof String) {
                    icon = DefaultDownloadLocation.valueOf((String) get);
                } else {
                    icon = null;
                }
            } catch (MalformedURLException | JSONException ex) {
                Logger.getGlobal().log(Level.SEVERE,
                        "Problem parsing " + modpackName + ".Icon", ex);
                icon = null;
            }
            URL websiteURL = null;
            try {
                String websiteStr = this.modpackJson.optString("Website", null);
                if (websiteStr != null) {
                    websiteURL = new URL(websiteStr);
                }
            } catch (MalformedURLException ex) {
                Logger.getGlobal().log(Level.SEVERE,
                        "Problem parsing " + modpackName + ".Website", ex);
            }
            DefaultModPack main = new DefaultModPack(modpackName, authors, null,
                    this.modpackJson.optString("Description", ""), donateURL,
                    icon, websiteURL);
            for (JSONObject v : this.versions) {
                ModPackVersion parentVersions = null;
                String optParents = v.optString("Parent");
                if (optParents != null) {
                    String[] split = optParents.split(":", 2);
                    if (split.length != 2) {
                        throw new AssertionError("Already checked at L83");
                    }
                    parentVersions = dependencies.get(split[0])
                            .getVersions().get(split[1]);
                }
                List<ModPackDownload> downloadLocations = new ArrayList<>();
                {
                    
                }
                main.addModPackVersion(new SimpleModPackVersion(main,
                        v.optString("Branch", "default"), parentVersions, 
                        v.optLong("InstalledSize", -1), downloadLocations, 
                        donateURL, donateStr, v.optLong("ReleaseDate", -1)));
            }
            return main;
        }

        @Override
        public String getName() {
            return this.modpackName;
        }
    }

}
