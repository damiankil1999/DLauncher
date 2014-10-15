package dlauncher.modpacks.packs;

import dlauncher.modpacks.download.DownloadLocation;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DefaultModPack implements ModPack {

    private final List<ModPackVersion> versions = new ArrayList<>();

    private final String name;

    private final List<String> authors;

    private final ModPack parent;

    private final String description;

    private final URL donateURL;

    private final DownloadLocation imageURL;

    private final URL websiteURL;

    public DefaultModPack(String name,
            List<String> authors, ModPack parent, String description,
            URL donateURL, DownloadLocation imageURL, URL websiteURL) {
        this.name = name;
        this.authors = authors;
        this.parent = parent;
        this.description = description;
        this.donateURL = donateURL;
        this.imageURL = imageURL;
        this.websiteURL = websiteURL;
    }

    @Override
    public ModPack getParent() {
        return parent;
    }

    @Override
    public List<ModPackVersion> getVersions() {
        return versions;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public List<String> getAuthors() {
        return authors;
    }

    @Override
    public URL getDonateURL() {
        return donateURL;
    }

    @Override
    public DownloadLocation getImage() {
        return imageURL;
    }

    @Override
    public URL getWebsite() {
        return websiteURL;
    }

    public void addModPackVersion(ModPackVersion version) {
        this.versions.add(version);
    }

}
