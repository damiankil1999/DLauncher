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

    public interface AuthorizationInfo {

        public CredentialsManager getManager();

        public boolean isValidated();

        public String getAccessToken();

        public String getUserID();

        public String getAcountID();

        public String getAcountName();

        public String getTwitchAccesToken();

        public AuthorizationInfo refresh() throws IOException;

        public void validate() throws IOException;
    }
}
