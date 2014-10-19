/*
 * This work is licensed under a Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * http://creativecommons.org/licenses/by-sa/3.0/
 * Authors: Damian Lamers, Fernando van Loenhout
 */
package dlauncher.modpacks;

import dlauncher.cache.CacheManager;
import dlauncher.modpacks.packs.ModPack;
import dlauncher.modpacks.packs.ModPackVersionDescription;
import dlauncher.modpacks.packs.UnresolvedModPack;
import dlauncher.modpacks.download.ModPackListingDownload;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;

public class ModpackManager {
    
    private final List<ModPackListingDownload> dataSources;
    private boolean isRefreshing = false;
    private final Map<String, ModPack> allModPacks = new HashMap<>();
    private final CacheManager cache;
    private final ModPackListNoticer eventReciever;
    
    public ModpackManager(List<ModPackListingDownload> dataSources,
            CacheManager cache, ModPackListNoticer parentEventReciever) {
        if (dataSources == null) {
            throw new IllegalArgumentException("datasources == null");
        }
        this.dataSources = dataSources;
        this.cache = cache;
        this.eventReciever = parentEventReciever;
    }
    
    public void refreshModPackListing() {
        if (isRefreshing == true) {
            throw new IllegalStateException("Downloading of modpack listing "
                    + "already started");
        }
        isRefreshing = true;
        allModPacks.clear();
        new SwingWorker<Map<String, ModPack>, ModPackUpdateEvent>() {
            
            @Override
            protected Map<String, ModPack> doInBackground() {
                List<ModPackListingDownload> newSources
                        = new LinkedList<>(dataSources);
                List<UnresolvedModPack> modpacks = new LinkedList<>();
                for (int attempts = 0; attempts < 10; attempts++) {
                    Iterator<ModPackListingDownload> source
                            = newSources.iterator();
                    ModPackListingDownload last = null;
                    while (source.hasNext()) {
                        ModPackListingDownload next = source.next();
                        this.publish(
                                new ModPackListingUpdatedEvent(modpacks.size(),
                                        next, last));
                        try {
                            byte[] data = cache.downloadURL(next);
                            Collection<? extends UnresolvedModPack> get
                                    = next.readModPacksFromResource(data);
                            modpacks.addAll(get);
                            Logger.getGlobal().log(Level.INFO,
                                    "Correctly downloaded {0}, attempt {1}",
                                    new Object[]{next.getURL(), attempts});
                            source.remove();
                        } catch (IOException ex) {
                            Logger.getGlobal().log(Level.WARNING,
                                    "Error downloading {0}, attempt {2}: {1}",
                                    new Object[]{next.getURL(), ex.toString(),
                                        attempts});
                        }
                        last = next;
                    }
                    this.publish(new ModPackListingUpdatedEvent(modpacks.size(),
                            null, last));
                }
                int resolved;
                Iterator<UnresolvedModPack> m;
                ModPack mod;
                Map<String, ModPack> resolvedModpacks = new HashMap<>();
                do {
                    List<ModPack> resolvedPacks = new ArrayList<>();
                    resolved = 0;
                    m = modpacks.iterator();
                    detectModpack:
                    while (m.hasNext()) {
                        UnresolvedModPack next = m.next();
                        for (ModPackVersionDescription desc
                                : next.getRequiredDependencies()) {
                            if ((mod = resolvedModpacks.get(
                                    desc.getModPackName())) != null) {
                                if (mod.getVersions()
                                        .containsKey(desc.getVersion())) {
                                    continue;
                                }
                            }
                            continue detectModpack;
                        }
                        mod = next.createModPack(allModPacks);
                        if (mod == null) {
                            Logger.getGlobal().log(Level.WARNING,
                                    "Mod {0} doesn't give a valid modpack "
                                    + "after creating. Technical data: {1}",
                                    new Object[]{
                                        next.getName(), next.toString()});
                        } else {
                            resolvedModpacks.put(mod.getName(), mod);
                            resolvedPacks.add(mod);
                        }
                        m.remove();
                        resolved++;
                    }
                    Logger.getGlobal().log(Level.INFO, "Resolved {0} "
                            + "modpacks",
                            new Object[]{resolved});
                    this.publish(new ModPackReadyEvent(resolvedPacks));
                } while (resolved > 0 && (!modpacks.isEmpty()));
                if (!modpacks.isEmpty()) {
                    Logger.getGlobal().log(Level.WARNING,
                            "Failed resolving {0}, missing depencencies!",
                            new Object[]{modpacks});
                }
                return resolvedModpacks;
            }
            
            @Override
            protected void done() {
                try {
                    isRefreshing = false;
                    ModpackManager.this.allModPacks.putAll(this.get());
                    this.publish(new AllReadyEvent(this.get().values()));
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ex) {
                    Logger.getGlobal().log(Level.SEVERE, null, ex);
                }
            }
            
            @Override
            protected void process(List<ModPackUpdateEvent> chunks) {
                for (ModPackUpdateEvent evt : chunks) {
                    evt.callEvent(eventReciever);
                }
            }
            
        }.execute();
    }
    
    public ModPack getModPack(String key) {
        return allModPacks.get(key);
    }
    
    private interface ModPackUpdateEvent {
        
        public void callEvent(ModPackListNoticer eventReciever);
    }
    
    private class ModPackReadyEvent implements ModPackUpdateEvent {
        
        private final Collection<ModPack> newModPack;
        
        public ModPackReadyEvent(Collection<ModPack> newModPack) {
            this.newModPack = newModPack;
        }
        
        @Override
        public void callEvent(ModPackListNoticer eventReciever) {
            eventReciever.modPackReady(newModPack);
        }
    }
    
    private class ModPackListingUpdatedEvent implements ModPackUpdateEvent {
        
        private final int totalModPacks;
        private final ModPackListingDownload currentPage;
        private final ModPackListingDownload nextPage;
        
        public ModPackListingUpdatedEvent(int totalModPacks,
                ModPackListingDownload currentPage, ModPackListingDownload nextPage) {
            this.totalModPacks = totalModPacks;
            this.currentPage = currentPage;
            this.nextPage = nextPage;
        }
        
        @Override
        public void callEvent(ModPackListNoticer eventReciever) {
            eventReciever.modPackListingUpdated(totalModPacks,
                    currentPage, nextPage);
        }
    }
    
    private class AllReadyEvent implements ModPackUpdateEvent {
        
        private final Collection<ModPack> modpacks;
        
        public AllReadyEvent(Collection<ModPack> modpacks) {
            this.modpacks = modpacks;
        }
        
        @Override
        public void callEvent(ModPackListNoticer eventReciever) {
            eventReciever.allReady(modpacks);
        }
    }
    
    public interface ModPackListNoticer {

        /**
         * This method is called when a stage of modpack processing is complete.
         * A modpack stage is defined as a processing cycle of resolving any
         * dependencies this list of modpacks need
         *
         * @param newModPacks Any new resolved modpacks at the end of the stage
         */
        public void modPackReady(Collection<? extends ModPack> newModPacks);

        /**
         * Update a url listing that has been completed
         *
         * @param totalModPacks Total number of raw modpacks so far
         * @param currentPage Currently executing url, or null at the start
         * @param nextPage Url of next page listing, or null at the end
         */
        public void modPackListingUpdated(int totalModPacks,
                ModPackListingDownload currentPage, ModPackListingDownload nextPage);

        /**
         * Called when all unresolved modpacks are converted to fully working
         * modpacks. This is called af the end of every modlist refresh cycle.
         *
         * @param modpacks
         */
        public void allReady(Collection<ModPack> modpacks);
    }
}
