package healthscape.com.healthscape.util;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Component
@Slf4j
public class HashUtil {
    private static int SALT_LENGTH = 32;
    private static String ALGORITHM = "SHA-256";

    private static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[SALT_LENGTH];
        random.nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }

    public static HashWithSalt hashWithSalt(String data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
        String salt = generateSalt();
        String saltedData = salt + data;
        byte[] hashBytes = digest.digest(saltedData.getBytes(StandardCharsets.UTF_8));
        StringBuilder hashString = new StringBuilder();

        for (byte b : hashBytes) {
            hashString.append(String.format("%02x", b));
        }

        return new HashWithSalt(hashString.toString(), salt);
    }

    public static boolean checkIntegrity(String data, String salt, String hash) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
        String saltedData = salt + data;
        byte[] hashBytes = digest.digest(saltedData.getBytes(StandardCharsets.UTF_8));
        StringBuilder hashString = new StringBuilder();

        for (byte b : hashBytes) {
            hashString.append(String.format("%02x", b));
        }

        return hashString.toString().equals(hash);
    }

    public static String hashData(String data) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(data.getBytes());

            return bytesToHex(encodedHash);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
