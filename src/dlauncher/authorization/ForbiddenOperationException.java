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
public class ForbiddenOperationException extends AuthorizationException {

    /**
     * Creates a new instance of <code>ForbiddenOperationException</code>
     * without detail message.
     */
    public ForbiddenOperationException() {
    }

    /**
     * Constructs an instance of <code>ForbiddenOperationException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public ForbiddenOperationException(String msg) {
        super(msg);
    }
}
