/*
 * This work is licensed under a Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * http://creativecommons.org/licenses/by-sa/3.0/
 * Authors: Damian Lamers, Fernando van Loenhout
 */
package dlauncher.authorization;

/**
 *
 * @author Fernando
 */
public class UserMigratedException extends ForbiddenOperationException {

    /**
     * Creates a new instance of <code>UserMigratedException</code> without
     * detail message.
     */
    public UserMigratedException() {
    }

    /**
     * Constructs an instance of <code>UserMigratedException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public UserMigratedException(String msg) {
        super(msg);
    }
}
