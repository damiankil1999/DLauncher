package dlauncher.modpacks.packs;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Fernando
 */
public interface UnresolvedModPack {

    List<ModPackVersionDescription> getRequiredDependencies();

    ModPack createModPack(Map<String, ModPack> dependencies);
    
    String getName();
}
