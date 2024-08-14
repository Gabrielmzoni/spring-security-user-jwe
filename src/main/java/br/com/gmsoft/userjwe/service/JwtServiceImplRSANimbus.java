package br.com.gmsoft.userjwe.service;

import br.com.gmsoft.userjwe.service.exception.*;
import com.google.gson.Gson;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

@Service
public class JwtServiceImplRSANimbus implements JwtService {
    static final long EXPIRATION_TIME_30_MINUTES = ((long) 1000 * 60 * 30);
    static final String PREFIX = "Bearer";

    private final Gson gson = new Gson();

    private static final String KEY_CLAIM_JWE = "gmsoft.userjwe_userdetails";

    @Value("${key.public}")
    private String publicKey;

    @Value("${key.private}")
    private String privateKey;



    public String getAuthorizationToken(String content) {

            var rsaPublicKey = getPublicKey(publicKey);

            var jwtClaims = new JWTClaimsSet.Builder();


            jwtClaims.claim("expiration", Instant.now().plusMillis(EXPIRATION_TIME_30_MINUTES).toEpochMilli());
            jwtClaims.claim(KEY_CLAIM_JWE, gson.toJson(content));

            var header = new JWEHeader(JWEAlgorithm.RSA_OAEP_256, EncryptionMethod.A128GCM);
            var jwt = new EncryptedJWT(header, jwtClaims.build());
            var encrypter = new RSAEncrypter(rsaPublicKey);
        try {
            jwt.encrypt(encrypter);
        } catch (JOSEException e) {
            throw new EncryptionException("Error encrypting JWT", e);
        }

        return jwt.serialize();

    }

    private String readJwe(String jwe) throws ParseException, JOSEException {

           var rsaPrivateKey = getPrivateKey(privateKey);
           var jweObject = JWEObject.parse(jwe);
           jweObject.decrypt(new RSADecrypter(rsaPrivateKey));
           Map<String, Object> payloadMap  = jweObject.getPayload().toJSONObject();
           var expirationTime = Long.parseLong(payloadMap.get("expiration").toString());

            if (Instant.now().isAfter(Instant.ofEpochMilli(expirationTime)))
                throw new JweExpiredException("JWE has expired, please log in again");

            if (payloadMap.containsKey(KEY_CLAIM_JWE)) {
                return payloadMap.get(KEY_CLAIM_JWE).toString();
            } else {
                  throw new InvalidJweException("Key '" + KEY_CLAIM_JWE + "' not found in JWE.");
            }

    }

    public String getAuthUser(HttpServletRequest request)  {

        var token = request.getHeader(HttpHeaders.AUTHORIZATION).replace(PREFIX, "").trim();

            String content;

            try {
                content = readJwe(token);
            } catch (ParseException e) {
                throw new InvalidJweException("Invalid JWE format");
            } catch ( JOSEException  e) {
                throw new InvalidJweException(e.getMessage());
            }

            return gson.fromJson(content, String.class);

    }

    private RSAPublicKey getPublicKey(String key) {
        try {
            var publicKeyContent = key
                    .replace("\n", "")
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "");

            byte[] byteKey = Base64.getDecoder().decode(publicKeyContent.getBytes());
            var x509publicKey = new X509EncodedKeySpec(byteKey);
            var kf = KeyFactory.getInstance("RSA");

            return (RSAPublicKey) kf.generatePublic(x509publicKey);
        } catch (NoSuchAlgorithmException e) {
            throw new RSAAlgorithmException("RSA algorithm not found getting RSA public key", e);
        } catch (InvalidKeySpecException e) {
            throw new InvalidKeySpecExceptionForRSA("Invalid Key Spec for RSA public key", e);
        } catch (IllegalArgumentException e) {
            throw new InvalidBase64ContentException("Invalid Base64 content for RSA public key", e);
        }
    }

    private RSAPrivateKey getPrivateKey(String key) {
        try {

            var privateKeyContent = key
                    .replace("\n", "")
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "");

            byte[] byteKey = Base64.getDecoder().decode(privateKeyContent.getBytes());
            var pKCS8privateKey = new PKCS8EncodedKeySpec(byteKey);
            var kf = KeyFactory.getInstance("RSA");

            return (RSAPrivateKey) kf.generatePrivate(pKCS8privateKey);
        } catch (NoSuchAlgorithmException e) {
            throw new RSAAlgorithmException("RSA algorithm not found getting RSA private key", e);
        } catch (InvalidKeySpecException e) {
            throw new InvalidKeySpecExceptionForRSA("Invalid Key Spec for RSA private key", e);
        } catch (IllegalArgumentException e) {
            throw new InvalidBase64ContentException("Invalid Base64 content RSA private key", e);
        }
    }
}