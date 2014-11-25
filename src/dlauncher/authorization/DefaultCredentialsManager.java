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
                        obj1.getString("userid"),
                        obj1.getString("username")));
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

    @Override
    public AuthorizationInfo refresh(AuthorizationInfo token)
        throws IOException, AuthorizationException {
        if (token.getManager() != this) {
            throw new IllegalArgumentException(
                "Token not managed by this manager!");
        }
        assert token instanceof AuthorizationInfoImpl;
        AuthorizationInfoImpl toRefresh = (AuthorizationInfoImpl) token;
        this.authDatabase.remove(toRefresh);
        AuthorizationInfoImpl output = toRefresh.refresh();
        this.authDatabase.add(output);
        return output;
    }

    @Override
    public AuthorizationInfo validate(AuthorizationInfo token)
        throws IOException, AuthorizationException {
        if (token.getManager() != this) {
            throw new IllegalArgumentException(
                "Token not managed by this manager!");
        }
        assert token instanceof AuthorizationInfoImpl;
        AuthorizationInfoImpl toRefresh = (AuthorizationInfoImpl) token;
        toRefresh.validate();
        if (!toRefresh.isValidated()) {
            this.authDatabase.remove(toRefresh);
            return null;
        }
        return toRefresh;
    }

    @Override
    public AuthorizationInfo addAccessToken(String account, String password) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int removeInvalidAccessTokens() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void invalidateAccessToken(AuthorizationInfo token) throws IOException, AuthorizationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private class AuthorizationInfoImpl implements AuthorizationInfo {

        private final String accessToken;
        private boolean valid;
        private final String userid;
        private final String uuid;
        private final String displayName;
        private final String twitchAccesToken;
        private final String username;

        public AuthorizationInfoImpl(String accessToken, boolean valid, String uuid, String displayName, String twitchAccesToken, String userid, String username) {
            this.accessToken = accessToken;
            this.valid = valid;
            this.uuid = uuid;
            this.displayName = displayName;
            this.twitchAccesToken = twitchAccesToken;
            this.userid = userid;
            this.username = username;
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
        public String getUuid() {
            return uuid;
        }

        @Override
        public String getDisplayName() {
            return displayName;
        }

        private AuthorizationInfoImpl refresh()
            throws IOException, AuthorizationException {
            this.valid = false;
            JSONObject obj = new JSONObject();
            obj.put("clientToken", DefaultCredentialsManager.this.clientToken);
            obj.put("accessToken", this.accessToken);
            obj.put("requestUser", true);
            obj = makeRequest(refresh, obj);
            int length;
            try {
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
                    obj.getJSONObject("user").getString("id"),
                    this.username
                );
            } catch (JSONException ex) {
                throw new InvalidResponseException(ex, obj);
            }
        }

        private void validate()
            throws IOException, AuthorizationException {
            this.valid = false;
            JSONObject obj = new JSONObject();
            obj.put("accessToken", this.accessToken);
            this.valid = !makeRequest(validate, obj, true).has("error");
        }

        @Override
        public String getTwitchAccesToken() {
            return twitchAccesToken;
        }

        @Override
        public String getUserID() {
            return userid;
        }

        @Override
        public String getUsername() {
            return username;
        }
    }

    private static JSONObject makeRequest(URL url, JSONObject post)
        throws ProtocolException, IOException, AuthorizationException {
        return makeRequest(url, post, false);
    }

    private static JSONObject makeRequest(URL url, JSONObject post,
        boolean ignoreErrors)
        throws ProtocolException, IOException, AuthorizationException {
        JSONObject obj = null;
        try {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setUseCaches(false);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
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
                byte[] rawBytes = bytes.toByteArray();
                if (rawBytes.length != 0) {
                    obj = new JSONObject(new String(rawBytes,
                        Charset.forName("UTF-8")));
                } else {
                    obj = new JSONObject();
                }
                if (!ignoreErrors && obj.has("error")) {
                    String error = obj.getString("error");
                    String errorMessage = obj.getString("errorMessage");
                    String cause = obj.optString("cause", null);
                    if ("ForbiddenOperationException".equals(error)) {
                        if ("UserMigratedException".equals(cause)) {
                            throw new UserMigratedException(errorMessage);
                        }
                        throw new ForbiddenOperationException(errorMessage);
                    }
                    throw new AuthorizationException(
                        error + (cause != null ? "." + cause : "") + ": " + errorMessage);
                }
                return obj;
            }
        } catch (JSONException ex) {
            throw new InvalidResponseException(ex, obj);
        }
    }
}
