package dlauncher.modpacks.packs;

/**
 *
 * @author Fernando
 */
public interface UnresolvedModPack {

    String getRequiredDependency();

    ModPack createModPack(ModPack dependency);
}
