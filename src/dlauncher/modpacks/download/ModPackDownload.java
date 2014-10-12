package dlauncher.modpacks.download;

import java.io.File;

public interface ModPackDownload extends DownloadLocation {
    public void installToMinecraftDirectory(File minecraftDirectory);
}
