/*
 * This work is licensed under a Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * http://creativecommons.org/licenses/by-sa/3.0/
 * Authors: Damian Lamers, Fernando van Loenhout
 */
package dlauncher.authorization;

import dlauncher.cache.util.SizeLimitedByteArrayOutputStream;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DefaultCredentialsManager implements CredentialsManager {

    private final String clientToken;
    private final List<AuthorizationInfoImpl> authDatabase = new ArrayList<>();
    private static final URL refresh;
    private static final URL validate;
    private static final URL invalidate;
    private static final URL authenticate;

    static {
        try {
            refresh = new URL("https://authserver.mojang.com/refresh");
            validate = new URL("https://authserver.mojang.com/validate");
            invalidate = new URL("https://authserver.mojang.com/invalidate");
            authenticate = new URL("https://authserver.mojang.com/authenticate");
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public DefaultCredentialsManager(File accountsFile, Charset encoding)
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
                        obj1.getString("uuid"), obj1.getString("displayName"),
                        obj1.optString("twitch_acces_token", null),
                        obj1.getString("userid")));
                }
            }
        } else {
            this.clientToken = UUID.randomUUID().toString();
        }
    }

    @Override
    public List<? extends AuthorizationInfo> getAccessTokens() {
        return Collections.<AuthorizationInfo>unmodifiableList(authDatabase);
    }

    @Override
    public String getClientToken() {
        return clientToken;
    }

    @Override
    public void validateAndRefreshTokens() {

    }

    private class AuthorizationInfoImpl implements AuthorizationInfo {

        private final String accessToken;
        private boolean valid;
        private final String mojangID;
        private final String acountID;
        private final String acountName;
        private final String twitchAccesToken;

        public AuthorizationInfoImpl(String accessToken, boolean valid,
            String acountID, String acountName,
            String twitchAccesToken, String mojangID) {
            this.accessToken = accessToken;
            this.valid = valid;
            this.acountID = acountID;
            this.acountName = acountName;
            this.twitchAccesToken = twitchAccesToken;
            this.mojangID = mojangID;
        }

        @Override
        public DefaultCredentialsManager getManager() {
            return DefaultCredentialsManager.this;
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
            this.valid = false;
            JSONObject obj = new JSONObject();
            obj.put("clientToken", DefaultCredentialsManager.this.clientToken);
            obj.put("accessToken", this.accessToken);
            obj.put("requestUser", true);
            obj = makeRequest(refresh, obj);
            int length;
            try {
                if (obj.has("error")) {

                }

                JSONArray userProperties = obj.getJSONObject("user")
                    .optJSONArray("properties");
                Map<String, String> props = new HashMap<>();
                if (userProperties != null) {
                    length = userProperties.length();
                    for (int i = 0; i < length; i++) {
                        props.put(
                            userProperties.getJSONObject(i).getString("name"),
                            userProperties.getJSONObject(i).getString("value"));
                    }
                }
                return new AuthorizationInfoImpl(obj.getString("accesToken"),
                    true, obj.getJSONObject("selectedProfile").getString("id"),
                    obj.getJSONObject("selectedProfile").getString("user"),
                    props.get("twitch_access_token"),
                    obj.getJSONObject("user").getString("id")
                );
            } catch (JSONException ex) {
                throw new IOException(ex);
            }
        }

        @Override
        public void validate() throws IOException {

        }

        @Override
        public String getTwitchAccesToken() {
            return twitchAccesToken;
        }

        @Override
        public String getUserID() {
            return mojangID;
        }
    }

    private static JSONObject makeRequest(URL url, JSONObject post) throws ProtocolException, IOException {
        HttpURLConnection con = (HttpURLConnection) refresh.openConnection();
        con.setDoOutput(true);
        con.setDoInput(true);
        con.setUseCaches(false);
        con.setRequestMethod("POST");
        con.setConnectTimeout(15 * 1000);
        con.setReadTimeout(15 * 1000);

        con.connect();
        try (OutputStream out = con.getOutputStream()) {
            out.write(post.toString().getBytes(Charset.forName("UTF-8")));
        }
        con.getResponseCode();
        InputStream instr;
        instr = con.getErrorStream();
        if (instr == null) {
            instr = con.getInputStream();
        }
        byte[] data = new byte[1024];
        int length;
        try (SizeLimitedByteArrayOutputStream bytes
            = new SizeLimitedByteArrayOutputStream(1024 * 4)) {
            try (InputStream in
                = new BufferedInputStream(instr)) {
                while ((length = in.read(data)) >= 0) {
                    bytes.write(data, 0, length);
                }
            }
            return new JSONObject(new String(bytes.toByteArray(),
                Charset.forName("UTF-8")));
        }
    }
}
