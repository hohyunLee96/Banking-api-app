package nl.inholland.bankingapi.unittesting.jwt;

import nl.inholland.bankingapi.jwt.JwtKeyProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtKeyProviderTest {

    @Mock
    private Resource resource;

    private JwtKeyProvider jwtKeyProvider;

    @BeforeEach
    void setUp() throws IOException {
        jwtKeyProvider = new JwtKeyProvider();
        ReflectionTestUtils.setField(jwtKeyProvider, "keystore", "inholland.p12");
        ReflectionTestUtils.setField(jwtKeyProvider, "keystorePassword", "123456");
        ReflectionTestUtils.setField(jwtKeyProvider, "keyAlias", "inholland");
    }

    @Test
    void testGetPrivateKey() throws Exception {
        when(resource.getInputStream()).thenReturn(getClass().getResourceAsStream("/inholland.p12"));
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(resource.getInputStream(), jwtKeyProvider.getKeystorePassword().toCharArray());
        Key privateKey = keyStore.getKey(jwtKeyProvider.getKeyAlias(), jwtKeyProvider.getKeystorePassword().toCharArray());

        ReflectionTestUtils.invokeMethod(jwtKeyProvider, "init");

        assertArrayEquals(privateKey.getEncoded(), jwtKeyProvider.getPrivateKey().getEncoded());
    }

    @Test
    void testSetAndGetKeystore() {
        String newKeystorePath = "newKeystore.p12";
        jwtKeyProvider.setKeystore(newKeystorePath);
        assertEquals(newKeystorePath, jwtKeyProvider.getKeystore());
    }

    @Test
    void testSetAndGetKeystorePassword() {
        String newKeystorePassword = "newPassword";
        jwtKeyProvider.setKeystorePassword(newKeystorePassword);
        assertEquals(newKeystorePassword, jwtKeyProvider.getKeystorePassword());
    }

    @Test
    void testSetAndGetKeyAlias() {
        String newKeyAlias = "newAlias";
        jwtKeyProvider.setKeyAlias(newKeyAlias);
        assertEquals(newKeyAlias, jwtKeyProvider.getKeyAlias());
    }

    @Test
    void testInvalidKeystorePath() {
        String invalidKeystorePath = "invalid.p12";
        ReflectionTestUtils.setField(jwtKeyProvider, "keystore", invalidKeystorePath);

        assertThrows(IOException.class, () -> {
            jwtKeyProvider.getPrivateKey();
            jwtKeyProvider.init();
        });
    }

    @Test
    void testInvalidKeystorePassword() {
        String invalidKeystorePassword = "invalidPassword";
        ReflectionTestUtils.setField(jwtKeyProvider, "keystorePassword", invalidKeystorePassword);

        assertThrows(Exception.class, () -> ReflectionTestUtils.invokeMethod(jwtKeyProvider, "init"));
    }

    @Test
    void testInitMethodBehavior() throws Exception {
        ReflectionTestUtils.invokeMethod(jwtKeyProvider, "init");

        assertNotNull(jwtKeyProvider.getPrivateKey());
    }
}
