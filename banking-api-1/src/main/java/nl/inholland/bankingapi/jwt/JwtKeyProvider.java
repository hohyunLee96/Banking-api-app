package nl.inholland.bankingapi.jwt;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import java.io.IOException;

@Data
@Component
public class JwtKeyProvider {

    @Value("${jwt.key-store}")
    private String keystore;

    @Value("${jwt.key-store-password}")
    private String keystorePassword;

    @Value("${jwt.key-alias}")
    private String keyAlias;

    private Key privateKey;

    @PostConstruct
    public void init() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        Resource resource = new ClassPathResource(keystore);
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(resource.getInputStream(), keystorePassword.toCharArray());
        privateKey = keyStore.getKey(keyAlias, keystorePassword.toCharArray());
    }

    public Key getPrivateKey() {
        return privateKey;
    }

}