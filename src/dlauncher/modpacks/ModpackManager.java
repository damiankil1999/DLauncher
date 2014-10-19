/*
 * This work is licensed under a Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * http://creativecommons.org/licenses/by-sa/3.0/
 * Authors: Damian Lamers, Fernando van Loenhout
 */
package dlauncher.modpacks;

import dlauncher.cache.CacheManager;
import dlauncher.modpacks.packs.ModPack;
import dlauncher.modpacks.download.DownloadLocation;
import dlauncher.modpacks.download.ModPackListingDownload;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;

public class ModpackManager {

    private final List<ModPackListingDownload> dataSources;
    private boolean isRefreshing = false;
    private final Map<String, ModPack> allModPacks = new HashMap<>();
    private final CacheManager cache;

    public ModpackManager(List<ModPackListingDownload> dataSources,
            CacheManager cache) {
        if (dataSources == null) {
            throw new IllegalArgumentException("datasources == null");
        }
        this.dataSources = dataSources;
        this.cache = cache;
    }

    public void refreshModPackListing(final ModPackListNoticer eventReciever) {
        if (isRefreshing == true) {
            throw new IllegalStateException("Downloading of modpack listing already started");
        }
        isRefreshing = true;
        new SwingWorker<List<ModPack>, ModPackUpdateEvent>() {

            @Override
            protected List<ModPack> doInBackground() throws Exception {
                List<ModPackListingDownload> newSources = new LinkedList<>(dataSources);
                for (int attempts = 0; attempts < 10; attempts++) {
                    Iterator<ModPackListingDownload> source = newSources.iterator();
                    while (source.hasNext()) {
                        ModPackListingDownload next = source.next();
                        try {
                            byte[] data = cache.downloadURL(next);
                            next.readModPacksFromResource(data);

                        } catch (IOException ex) {
                            Logger.getGlobal().log(Level.WARNING,
                                    "Error downloading {0}, attempt {2}: {1}",
                                    new Object[]{next.getURL(), ex.toString(), attempts});
                        }
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                this.publish(new AllReadyEvent(null));
            }

            @Override
            protected void process(List<ModPackUpdateEvent> chunks) {
                for (ModPackUpdateEvent evt : chunks) {
                    evt.callEvent(eventReciever);
                }
            }

        };
    }

    private interface ModPackUpdateEvent {

        public void callEvent(ModPackListNoticer eventReciever);
    }

    private class ModPackListingRecievedEvent implements ModPackUpdateEvent {

        private final DownloadLocation finishedURL;

        public ModPackListingRecievedEvent(DownloadLocation finishedURL) {
            this.finishedURL = finishedURL;
        }

        @Override
        public void callEvent(ModPackListNoticer eventReciever) {
            eventReciever.modPackListingRecieved(finishedURL);
        }
    }

    private class ModPackReadyEvent implements ModPackUpdateEvent {

        private final ModPack newModPack;

        public ModPackReadyEvent(ModPack newModPack) {
            this.newModPack = newModPack;
        }

        @Override
        public void callEvent(ModPackListNoticer eventReciever) {
            eventReciever.modPackReady(newModPack);
        }
    }

    private class ModPackListingUpdatedEvent implements ModPackUpdateEvent {

        private final int totalModPacks;
        private final int downloadedModPacks;
        private final int failedModPacks;
        private final boolean stillListingModPacks;

        public ModPackListingUpdatedEvent(int totalModPacks,
                int downloadedModPacks, int failedModPacks,
                boolean stillListingModPacks) {
            this.totalModPacks = totalModPacks;
            this.downloadedModPacks = downloadedModPacks;
            this.failedModPacks = failedModPacks;
            this.stillListingModPacks = stillListingModPacks;
        }

        @Override
        public void callEvent(ModPackListNoticer eventReciever) {
            eventReciever.modPackListingUpdated(totalModPacks,
                    downloadedModPacks, failedModPacks, stillListingModPacks);
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

        public void modPackListingRecieved(DownloadLocation finishedURL);

        public void modPackReady(ModPack newModPack);

        public void modPackListingUpdated(int totalModPacks,
                int downloadedModPacks, int failedModPacks,
                boolean stillListingModPacks);

        public void allReady(Collection<ModPack> modpacks);
    }
}
