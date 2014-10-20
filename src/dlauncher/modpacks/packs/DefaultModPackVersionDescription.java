/*
 * This work is licensed under a Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * http://creativecommons.org/licenses/by-sa/3.0/
 * Authors: Damian Lamers, Fernando van Loenhout
 */
package dlauncher.modpacks.packs;

import java.util.Objects;

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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.modpack);
        hash = 37 * hash + Objects.hashCode(this.version);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultModPackVersionDescription other = (DefaultModPackVersionDescription) obj;
        if (!Objects.equals(this.modpack, other.modpack)) {
            return false;
        }
        return Objects.equals(this.version, other.version);
    }

    @Override
    public String toString() {
        return "ModPackVersion{" + "modpack=" + modpack + ", version=" + version + '}';
    }

}
