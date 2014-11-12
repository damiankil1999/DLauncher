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
import java.util.List;
import java.util.UUID;
import org.json.JSONObject;

public class CredentialsManager {

    private final String clientToken;
    private final List<AuthorizationInfo> authDatabase = new ArrayList<>();

    public CredentialsManager(File accountsFile, Charset encoding) throws IOException {
        if (accountsFile.exists()) {
            byte[] encoded = Files.readAllBytes(accountsFile.toPath());
            JSONObject obj = new JSONObject(new String(encoded, encoding));
            this.clientToken = obj.optString("clientToken", UUID.randomUUID().toString());
        } else {
            this.clientToken = UUID.randomUUID().toString();
        }
    }

    public interface AuthorizationInfo {

        public CredentialsManager getManager();
    }

    private class AuthorizationInfoImpl implements AuthorizationInfo {

        @Override
        public CredentialsManager getManager() {
            return CredentialsManager.this;
        }
    }
}
