package dlauncher.modpacks.packs;

public class DefaultModPackVersionDescription
        implements ModPackVersionDescription {

    private final String modpack;
    private final String version;

    public DefaultModPackVersionDescription(String modpack, String version) {
        this.modpack = modpack;
        this.version = version;
    }

    @Override
    public String getModPackName() {
        return modpack;
    }

    @Override
    public String getVersion() {
        return version;
    }

}
