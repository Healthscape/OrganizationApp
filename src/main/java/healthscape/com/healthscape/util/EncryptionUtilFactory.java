package healthscape.com.healthscape.util;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EncryptionUtilFactory {

    private final Map<String, String> keyMap = new HashMap<>();

    public EncryptionUtilFactory(@Value("${encryption.keys.default}") String defaultKey,
                                 @Value("${encryption.keys.another}") String anotherKey) {
        keyMap.put("default", defaultKey);
        keyMap.put("another", anotherKey);
    }

    public EncryptionUtil getEncryptionUtil(String keyName) {
        String keyString = keyMap.get(keyName);
        if (keyString == null) {
            throw new IllegalArgumentException("No key found for: " + keyName);
        }

        return new EncryptionUtil(createSecretKey(keyString));
    }

    private SecretKey createSecretKey(String keyString) {
        byte[] decodedKey = Base64.getDecoder().decode(keyString);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }
}

