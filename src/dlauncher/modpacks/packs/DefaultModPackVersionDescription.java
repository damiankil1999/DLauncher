/*
 * This work is licensed under a Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * http://creativecommons.org/licenses/by-sa/3.0/
 * Authors: Damian Lamers, Fernando van Loenhout
 */
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
