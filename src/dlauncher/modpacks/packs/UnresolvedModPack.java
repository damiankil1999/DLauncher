/*
 * This work is licensed under a Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * http://creativecommons.org/licenses/by-sa/3.0/
 * Authors: Damian Lamers, Fernando van Loenhout
 */
package dlauncher.modpacks.packs;

import java.util.Collection;
import java.util.Map;

/**
 *
 * @author Fernando
 */
public interface UnresolvedModPack {

    Collection<? extends ModPackVersionDescription> getRequiredDependencies();

    ModPack createModPack(Map<String, ModPack> dependencies);
    
    String getName();
}
