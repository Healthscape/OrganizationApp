package healthscape.com.healthscape.util;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import lombok.extern.slf4j.Slf4j;

import java.util.Base64;

@Slf4j
public class EncryptionUtil {

    private final SecretKey key;

    public EncryptionUtil(SecretKey key) {
        this.key = key;
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
            return base64Str;
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
            byte[] decodedBytes = Base64.getUrlDecoder().decode(cipherText);
            byte[] plainText = cipher.doFinal(decodedBytes);
            return new String(plainText);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

}
