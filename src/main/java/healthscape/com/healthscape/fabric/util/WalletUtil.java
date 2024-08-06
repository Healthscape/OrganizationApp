package healthscape.com.healthscape.fabric.util;

import healthscape.com.healthscape.util.Config;
import healthscape.com.healthscape.util.EncryptionConfig;
import lombok.extern.slf4j.Slf4j;

import org.hyperledger.fabric.gateway.Identity;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;
import org.hyperledger.fabric.gateway.X509Identity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Paths;

@Component
public class WalletUtil {
    private Wallet wallet;

    @Autowired
    private EncryptionConfig encryptionConfig;

    public WalletUtil() {
        try {
            wallet = Wallets.newFileSystemWallet(Paths.get(Config.WALLET_DIRECTORY));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public X509Identity getIdentity(String userIdentityId) throws IOException {
        return (X509Identity) wallet.get(userIdentityId);
    }

    public String getRoleFromIdentity(String userIdentityId) throws IOException {
        X509Identity identity = getIdentity(userIdentityId);
        String roleStr = new String(identity.getCertificate().getExtensionValue("1.2.3.4.5.6.7.8.1"));
        int startIndex = roleStr.indexOf("ROLE_");
        int endIndex = roleStr.indexOf("\"", startIndex);
        return roleStr.substring(startIndex, endIndex);
    }

    public String getCNFromIdentity(String userIdentityId) throws IOException {
        X509Identity identity = getIdentity(userIdentityId);
        String subject = identity.getCertificate().getSubjectX500Principal().getName();
        String[] strings = subject.split(",");
        String[] cnStr = strings[0].split("=");
        return cnStr[1];
    }

    public X509Identity getAdminIdentity() throws IOException {
        String encryptedId = encryptionConfig.encryptDefaultData(Config.ADMIN_ID);
        return (X509Identity) wallet.get(encryptedId);
    }

    public boolean doesExistById(String userIdentityId) throws IOException {
        return wallet.get(userIdentityId) != null;
    }

    public void putIdentity(String userIdentityId, Identity identity) throws IOException {
        wallet.put(userIdentityId, identity);
    }

    public void deleteIdentity(String userIdentityId) throws IOException {
        wallet.remove(userIdentityId);
    }

    public boolean doesAdminExist() throws IOException {
        String encryptedId = encryptionConfig.encryptDefaultData(Config.ADMIN_ID);
        return wallet.get(encryptedId) != null;
    }
}
