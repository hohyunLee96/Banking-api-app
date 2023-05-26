//package nl.inholland.bankingapi.util;
//
//import jakarta.annotation.PostConstruct;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.core.io.Resource;
//
//import java.io.IOException;
//import java.security.*;
//import java.security.cert.CertificateException;
//
//public class JwtKeyProvider {
//    //get values from application.properties
//    @Value("${server.ssl.key-alias}")
//    private String alias;
//    @Value("${server.ssl.key-store}")
//    private String keystore;
//    @Value("${server.ssl.key-store-password}")
//    private String password;
//
//    private Key privateKey;
//
//    //run after all the constructors have run
//    @PostConstruct
//    protected void init() throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException {
//        Resource resource = new ClassPathResource(keystore);
//        KeyStore keyStore = KeyStore.getInstance("PKCS12");
//        keyStore.load(resource.getInputStream(), password.toCharArray());
//        privateKey = keyStore.getKey(alias, password.toCharArray());
//    }
//
//    public Key getPrivateKey() {
//        return privateKey;
//    }
//}
