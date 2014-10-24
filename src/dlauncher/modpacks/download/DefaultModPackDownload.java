/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dlauncher.modpacks.download;

import dlauncher.cache.CacheManager;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class DefaultModPackDownload extends DefaultDownloadLocation
implements ModPackDownload {

    public DefaultModPackDownload(URL url) {
        super(url);
    }

    public DefaultModPackDownload(URL url, long size, byte[] md5, byte[] sha512) {
        super(url, size, md5, sha512);
    }
    
    @Override
    public void installToMinecraftDirectory(File minecraftDirectory, CacheManager downloadManager) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
