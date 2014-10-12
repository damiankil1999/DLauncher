package dlauncher.modpacks.download;

import java.net.URL;

public interface DownloadLocation {

    URL getURL();

    long getSize();

    byte[] getMD5();

    byte[] getSHA512();
}
