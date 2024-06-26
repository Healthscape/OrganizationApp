package healthscape.com.healthscape.util;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class EncryptionUtil {

    private static final String SECRET_KEY_STRING = "SFYQFpSdI5JVlhcHXsrKMbamj82SQPuG";
    private final SecretKey key;

    public EncryptionUtil() {
        byte[] decodedKey = Base64.getDecoder().decode(SECRET_KEY_STRING);
        this.key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    public String encryptIfNotAlready(String plainText) {
        if (plainText.length() == 64) {
            return plainText;
        }
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] cipherText = cipher.doFinal(plainText.getBytes());
            String base64Str = Base64.getUrlEncoder().withoutPadding().encodeToString(cipherText);
            return base64Str.replace('_', '.');
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public String decryptIfNotAlready(String cipherText) {
        if (cipherText.length() != 64) {
            return cipherText;
        }
        return decrypt(cipherText);
    }

    public String decrypt(String cipherText) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            cipherText = cipherText.replace('.', '_');
            byte[] decodedBytes = Base64.getUrlDecoder().decode(cipherText);
            byte[] plainText = cipher.doFinal(decodedBytes);
            return new String(plainText);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

}
