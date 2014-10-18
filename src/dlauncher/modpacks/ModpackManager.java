package dlauncher.modpacks;

import dlauncher.cache.CacheManager;
import dlauncher.modpacks.packs.ModPack;
import dlauncher.modpacks.download.DownloadLocation;
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

    private final List<DownloadLocation> dataSources;
    private boolean isRefreshing = false;
    private final Map<String, ModPack> allModPacks = new HashMap<>();
    private final CacheManager cache;

    public ModpackManager(List<DownloadLocation> dataSources,
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
                List<DownloadLocation> newSources = new LinkedList<>(dataSources);
                for (int attempts = 0; attempts < 10; attempts++) {
                    Iterator<DownloadLocation> source = newSources.iterator();
                    while (source.hasNext()) {
                        DownloadLocation next = source.next();
                        try {
                            ByteArrayOutputStream in = new ByteArrayOutputStream();
                            cache.downloadURL(next.getURL(), in);
                            byte[] data = in.toByteArray();
                            if (next.getSize() != -1 && next.getSize() != data.length) {
                                throw new IOException("Resource length mismatch! "
                                        + "Excepted length " + next.getSize()
                                        + ", but got " + data.length);
                            }
                            boolean containsHashes = false;
                            {
                                if (next.getMD5() != null) {
                                    containsHashes = true;
                                    MessageDigest md = MessageDigest.getInstance("MD5");
                                    byte[] md5 = md.digest(data);
                                    if (!Arrays.equals(md5, next.getMD5())) {
                                        throw new IOException("Resource MD5 mismatch! "
                                                + "Excepted hash " + Arrays.toString(next.getMD5())
                                                + ", but got " + Arrays.toString(md5));
                                    }
                                }
                                if (next.getSHA512() != null) {
                                    containsHashes = true;
                                    MessageDigest md = MessageDigest.getInstance("SHA-512");
                                    byte[] sha512 = md.digest(data);
                                    if (!Arrays.equals(sha512, next.getSHA512())) {
                                        throw new IOException("Resource SHA512 mismatch! "
                                                + "Excepted hash " + Arrays.toString(next.getSHA512())
                                                + ", but got " + Arrays.toString(sha512));
                                    }
                                }
                            }
                            if (!containsHashes) {
                                Logger.getGlobal().log(Level.WARNING, "Resource {0} doesn''t contain any hashes, file may not be downloaded correctly "
                                        + "(remember that attaching hashes to a file enabled caching)", next.getURL());
                            } else {
                                cache.addToCache(next.getURL(), data);
                            }
                        } catch (IOException ex) {
                            Logger.getGlobal().log(Level.WARNING, "Error downloading {0}: {1}", new Object[]{next.getURL(), ex.toString()});
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
