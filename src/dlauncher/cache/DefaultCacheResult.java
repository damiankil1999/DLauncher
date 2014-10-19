/*
 * This work is licensed under a Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * http://creativecommons.org/licenses/by-sa/3.0/
 * Authors: Damian Lamers, Fernando van Loenhout
 */
package dlauncher.cache;

import java.io.IOException;

public class DefaultCacheResult implements CacheResult {

    private final byte[] resource;
    private final IOException exception;

    public DefaultCacheResult(byte[] resource, IOException exception) {
        this.resource = resource;
        this.exception = exception;
    }

    @Override
    public byte[] getResource() {
        return resource;
    }

    @Override
    public IOException getException() {
        return exception;
    }

    @Override
    public byte[] getResult() throws IOException {
        if (exception != null) {
            throw new IOException("IOException inside cache: " + exception.toString(), exception);
        }
        return resource;
    }
}
