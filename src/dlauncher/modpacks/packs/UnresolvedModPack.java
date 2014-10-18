package dlauncher.modpacks.packs;

import java.util.List;

/**
 *
 * @author Fernando
 */
public interface UnresolvedModPack {

    List<ModPackVersionDescription> getRequiredDependencies();

    ModPack createModPack(List<ModPack> dependencies);
}
