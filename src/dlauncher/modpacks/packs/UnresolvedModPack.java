package dlauncher.modpacks.packs;

import java.util.List;

/**
 *
 * @author Fernando
 */
public interface UnresolvedModPack {

    List<String> getRequiredDependencies();

    ModPack createModPack(List<ModPack> dependencies);
}
