/*
 * This work is licensed under a Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * http://creativecommons.org/licenses/by-sa/3.0/
 * Authors: Damian Lamers, Fernando van Loenhout
 */
package dlauncher.authorization;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;

public class CredentialsManager {

    private final String clientToken;
    private final List<AuthorizationInfoImpl> authDatabase = new ArrayList<>();

    public CredentialsManager(File accountsFile, Charset encoding)
        throws IOException {
        if (accountsFile.exists()) {
            byte[] encoded = Files.readAllBytes(accountsFile.toPath());
            JSONObject obj = new JSONObject(new String(encoded, encoding));
            this.clientToken = obj.optString("clientToken",
                UUID.randomUUID().toString());
            JSONArray arr = obj.optJSONArray("authenticationDatabase");
            if (arr != null) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj1 = arr.getJSONObject(i);
                    this.authDatabase.add(new AuthorizationInfoImpl(
                        obj1.getString("accessToken"), false,
                        obj1.getString("userid"), obj1.getString("username")));
                }
            }
        } else {
            this.clientToken = UUID.randomUUID().toString();
        }
    }

    public List<? extends AuthorizationInfo> getAccessTokens() {
        return Collections.<AuthorizationInfo>unmodifiableList(authDatabase);
    }
    
    public String getClientToken() {
        return clientToken;
    }
    
    public void validateAndRefreshTokens() {
        
    }

    public interface AuthorizationInfo {

        public CredentialsManager getManager();

        public boolean isValidated();

        public String getAccessToken();

        public String getAcountID();

        public String getAcountName();
        
        public AuthorizationInfo refresh() throws IOException;
        
        public void validate() throws IOException;
    }

    private class AuthorizationInfoImpl implements AuthorizationInfo {

        private final String accessToken;
        private boolean valid;
        private final String acountID;
        private final String acountName;

        public AuthorizationInfoImpl(String accessToken, boolean valid, String acountID, String acountName) {
            this.accessToken = accessToken;
            this.valid = valid;
            this.acountID = acountID;
            this.acountName = acountName;
        }

        @Override
        public CredentialsManager getManager() {
            return CredentialsManager.this;
        }

        @Override
        public boolean isValidated() {
            return valid;
        }

        @Override
        public String getAccessToken() {
            return accessToken;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        @Override
        public String getAcountID() {
            return acountID;
        }

        @Override
        public String getAcountName() {
            return acountName;
        }

        @Override
        public AuthorizationInfoImpl refresh() throws IOException {
            
        }

        @Override
        public void validate() throws IOException {
            
        }
    }
}
