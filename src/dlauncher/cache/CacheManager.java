package dlauncher.cache;

import dlauncher.modpacks.download.DownloadLocation;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import javax.swing.JLabel;

public interface CacheManager {

    public void downloadURL(DownloadLocation url, OutputStream to)
            throws IOException;
    
    public byte[] downloadURL(DownloadLocation url) throws IOException;

    public void lazyLoadImageToLabel(DownloadLocation url, JLabel target,
            int heigth, int width, boolean mayResize);
}
