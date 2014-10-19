/*
 * This work is licensed under a Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * http://creativecommons.org/licenses/by-sa/3.0/
 * Authors: Damian Lamers, Fernando van Loenhout
 */
package dlauncher.cache;

import java.io.IOException;

/**
 *
 * @author Fernando
 */
public interface CacheResult {

    byte[] getResource();

    IOException getException();

    byte[] getResult() throws IOException;
}
