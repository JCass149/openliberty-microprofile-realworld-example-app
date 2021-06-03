package api.utils;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.util.Base64;

public class GenerateJWT {

    public static String buildJwt(String userName, String userEmail) throws IOException {

        Config config = ConfigProvider.getConfig();
        String jwtIssuer = config.getValue("mp.jwt.verify.issuer", String.class);

        JWTAuth provider = JWTAuth.create(null, new JWTAuthOptions()
                .addPubSecKey(new PubSecKeyOptions().setAlgorithm("RS256").setSecretKey(getPrivateKey())));

        io.vertx.core.json.JsonObject claimsObj = new JsonObject()
                .put("exp", (System.currentTimeMillis() / 1000) + 86400) // Expire time +300 = 5 minutes, +86400 = 1 day
                .put("iat", (System.currentTimeMillis() / 1000)) // Issued time
                .put("jti", Long.toHexString(System.nanoTime())) // Unique value
                .put("sub", userName) // Subject name
                .put("upn", userEmail) // Subject Email
                .put("iss", jwtIssuer).put("groups", new JsonArray().add("user"));

        String token = provider.generateToken(claimsObj, new JWTOptions().setAlgorithm("RS256"));

        return token;

    }

    private final static String keystorePath = System.getProperty("user.dir") + "/resources/security/key.p12";

    private static String getPrivateKey() throws IOException {
        try {
            KeyStore keystore = KeyStore.getInstance("PKCS12");
            char[] password = new String("realworldPassword").toCharArray();
            keystore.load(new FileInputStream(keystorePath), password);
            Key key = keystore.getKey("default", password);
            return Base64.getEncoder().encodeToString(key.getEncoded());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}