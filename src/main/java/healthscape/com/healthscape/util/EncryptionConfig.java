package healthscape.com.healthscape.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
public class EncryptionConfig {

    @Value("${encryption.keys.default}")
    private String defaultKeyString;

    @Value("${encryption.keys.data}")
    private String dataKeyString;

    @Bean
    public EncryptionUtil defaultEncryptionUtil() {
        SecretKey key = createSecretKey(defaultKeyString);
        return new EncryptionUtil(key);
    }

    @Bean
    public EncryptionUtil anotherEncryptionUtil() {
        SecretKey key = createSecretKey(dataKeyString);
        return new EncryptionUtil(key);
    }

    private SecretKey createSecretKey(String keyString) {
        byte[] decodedKey = Base64.getDecoder().decode(keyString);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }
}
