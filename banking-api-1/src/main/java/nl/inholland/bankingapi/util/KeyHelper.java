package nl.inholland.bankingapi.util;

import io.jsonwebtoken.io.IOException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;

import java.security.*;
import java.security.cert.CertificateException;

public class KeyHelper {
    private KeyHelper() {
    }
    public static Key getPrivateKey(String alias, String keystore, String password) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException, java.io.IOException {
        Resource resource = new ClassPathResource(keystore);
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(resource.getInputStream(), password.toCharArray());
        return keyStore.getKey(alias, password.toCharArray());
    }
}
