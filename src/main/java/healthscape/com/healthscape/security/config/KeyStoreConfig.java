package healthscape.com.healthscape.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

@Configuration
public class KeyStoreConfig {

    private static final String KEY_STORE = "keystore/healthscape.p12";
    private static final String KEY_STORE_TYPE = "PKCS12";
    private static final String KEY_STORE_PASSWORD = "healthscape";

    @Bean
    public KeyStore keyStore() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE);
        keyStore.load(new ClassPathResource(KEY_STORE).getInputStream(), KEY_STORE_PASSWORD.toCharArray());

        return keyStore;
    }
}
