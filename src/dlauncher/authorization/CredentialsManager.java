/*
 * This work is licensed under a Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * http://creativecommons.org/licenses/by-sa/3.0/
 * Authors: Damian Lamers, Fernando van Loenhout
 */
package dlauncher.authorization;

import java.io.IOException;
import java.util.List;

public interface CredentialsManager {

    List<? extends AuthorizationInfo> getAccessTokens();

    String getClientToken();

    void validateAndRefreshTokens();

    public AuthorizationInfo refresh(AuthorizationInfo token) throws
        IOException, AuthorizationException;

    public AuthorizationInfo validate(AuthorizationInfo token) throws
        IOException, AuthorizationException;

    public AuthorizationInfo addAccessToken(String account, String password)
        throws IOException, AuthorizationException;

    public int removeInvalidAccessTokens();

    public void invalidateAccessToken(AuthorizationInfo token) throws
        IOException, AuthorizationException;

    public void save() throws
        IOException;

    public interface AuthorizationInfo {

        public CredentialsManager getManager();

        public boolean isValidated();

        public String getAccessToken();

        public String getUserID();

        public String getUuid();

        public String getDisplayName();

        public String getTwitchAccesToken();

        public String getUsername();
    }
}
