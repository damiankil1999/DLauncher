package dlauncher.cache;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import javax.swing.JLabel;

public interface CacheManager {

    public void downloadURL(URL url, OutputStream to) throws IOException;
    
    public byte[] downloadURL(URL url) throws IOException;

    public void addToCache(URL url, byte[] file);

    public void lazyLoadImageToLabel(URL url, JLabel target,
            int heigth, int width, boolean mayResize);
}
