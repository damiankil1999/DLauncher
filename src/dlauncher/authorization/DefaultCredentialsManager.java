/*
 * This work is licensed under a Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * http://creativecommons.org/licenses/by-sa/3.0/
 * Authors: Damian Lamers, Fernando van Loenhout
 */
package dlauncher.authorization;

import dlauncher.cache.util.SizeLimitedByteArrayOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DefaultCredentialsManager implements CredentialsManager {

    private final String clientToken;
    private final File accountsFile;
    private final Charset encoding;
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
        this.accountsFile = accountsFile;
        this.encoding = encoding;
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
    public void save() throws IOException {
        JSONObject obj = new JSONObject();
        obj.put("clientToken", this.clientToken);
        JSONArray authInfo = new JSONArray();
        for (AuthorizationInfoImpl token : authDatabase) {
            JSONObject tokenObj = new JSONObject();
            tokenObj.put("accessToken", token.getAccessToken());
            tokenObj.put("uuid", token.getUuid());
            tokenObj.put("displayName", token.getDisplayName());
            tokenObj.put("twitch_acces_token", token.getTwitchAccessToken());
            tokenObj.put("userid", token.getUserID());
            tokenObj.put("username", token.getUsername());
            authInfo.put(tokenObj);
        }
        obj.put("authenticationDatabase", authInfo);
        try (Writer writer = new BufferedWriter(
            new OutputStreamWriter(new FileOutputStream(this.accountsFile),
                this.encoding.newEncoder()))) {
            writer.write(obj.toString());
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
        Set<AuthorizationInfoImpl> toAdd = new HashSet<>();
        Iterator<AuthorizationInfoImpl> loop = this.authDatabase.iterator();
        while (loop.hasNext()) {
            AuthorizationInfoImpl v = loop.next();
            if (!v.valid) {
                try {
                    v.validate();
                    if (!v.isValidated()) {
                        toAdd.add(v.refresh());
                        loop.remove();
                    }
                } catch (IOException | AuthorizationException ex) {
                    Logger.getGlobal().log(Level.SEVERE, null, ex);
                    v.setValid(false);
                    loop.remove();
                }
            }
        }
        authDatabase.addAll(toAdd);
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
    public AuthorizationInfo addAccessToken(String account, String password)
        throws IOException, AuthorizationException {
        JSONObject obj = new JSONObject();
        obj.put("clientToken", DefaultCredentialsManager.this.clientToken);
        obj.put("username", account);
        obj.put("password", password);
        JSONObject agent = new JSONObject();
        agent.put("name", "minecraft");
        agent.put("version", "1");
        obj.put("agent", agent);
        obj.put("requestUser", true);
        obj = makeRequest(authenticate, obj);
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
            AuthorizationInfoImpl token = new AuthorizationInfoImpl(
                obj.getString("accessToken"), true,
                obj.getJSONObject("selectedProfile").getString("id"),
                obj.getJSONObject("selectedProfile").getString("user"),
                props.get("twitch_access_token"),
                obj.getJSONObject("user").getString("id"),
                account
            );
            this.authDatabase.add(token);
            return token;
        } catch (JSONException ex) {
            throw new InvalidResponseException(ex, obj);
        }
    }

    @Override
    public int removeInvalidAccessTokens() {
        int count = 0;
        Iterator<AuthorizationInfoImpl> tokens = this.authDatabase.iterator();
        while (tokens.hasNext()) {
            if (!tokens.next().isValidated()) {
                tokens.remove();
                count++;
            }
        }
        return count;
    }

    @Override
    public void invalidateAccessToken(AuthorizationInfo token)
        throws IOException, AuthorizationException {
        if (token.getManager() != this) {
            throw new IllegalArgumentException(
                "Token not managed by this manager!");
        }
        assert token instanceof AuthorizationInfoImpl;
        AuthorizationInfoImpl toRefresh = (AuthorizationInfoImpl) token;
        toRefresh.invalidate();
        this.authDatabase.remove(toRefresh);
    }

    private class AuthorizationInfoImpl implements AuthorizationInfo {

        private final String accessToken;
        private boolean valid;
        private final String userid;
        private final String uuid;
        private final String displayName;
        private final String twitchAccessToken;
        private final String username;

        public AuthorizationInfoImpl(String accessToken, boolean valid,
            String uuid, String displayName, String twitchAccesToken,
            String userid, String username) {
            this.accessToken = accessToken;
            this.valid = valid;
            this.uuid = uuid;
            this.displayName = displayName;
            this.twitchAccessToken = twitchAccesToken;
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

        @Override
        public String toString() {
            return "AuthorizationInfoImpl{"
                + "accessToken=" + accessToken
                + ", valid=" + valid
                + ", userid=" + userid
                + ", uuid=" + uuid
                + ", displayName=" + displayName
                + ", twitchAccessToken=" + twitchAccessToken
                + ", username=" + username
                + '}';
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
                return new AuthorizationInfoImpl(obj.getString("accessToken"),
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

        private void invalidate()
            throws IOException, AuthorizationException {
            this.valid = false;
            JSONObject obj = new JSONObject();
            obj.put("accessToken", this.accessToken);
            obj.put("clientToken", this.getManager().getClientToken());
            makeRequest(invalidate, obj, true);
            this.valid = false;
        }

        @Override
        public String getTwitchAccessToken() {
            return twitchAccessToken;
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
                        error + (cause != null ? "." + cause : "") + ": "
                        + errorMessage);
                }
                return obj;
            }
        } catch (JSONException ex) {
            throw new InvalidResponseException(ex, obj);
        }
    }
}
