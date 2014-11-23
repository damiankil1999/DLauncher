/*
 * This work is licensed under a Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * http://creativecommons.org/licenses/by-sa/3.0/
 * Authors: Damian Lamers, Fernando van Loenhout
 */
package dlauncher.authorization;

import org.json.JSONObject;

/**
 *
 * @author Fernando
 */
public class InvalidResponseException extends AuthorizationException {
    private JSONObject obj;

    /**
     * Creates a new instance of <code>InvalidResponseException</code> without
     * detail message.
     */
    public InvalidResponseException() {
    }

    /**
     * Constructs an instance of <code>InvalidResponseException</code> with the
     * specified detail message.
     *
     * @param cause The cause of the exception
     * @param obj the conflicting json
     */
    public InvalidResponseException(Exception cause, JSONObject obj) {
        super(cause.toString() + "JSON: " + String.valueOf(obj), cause);
        this.obj = obj;
    }
    
    public JSONObject getConflictingJSON() {
        return this.obj;
    }
}
