package healthscape.com.healthscape.fabric.util;

import healthscape.com.healthscape.util.Config;
import org.hyperledger.fabric.gateway.Identity;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;
import org.hyperledger.fabric.gateway.X509Identity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class WalletUtil {
    private Wallet wallet;

    public WalletUtil() {
        try {
            wallet = Wallets.newFileSystemWallet(Paths.get(Config.WALLET_DIRECTORY));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public X509Identity getIdentity(UUID userIdentityId) throws IOException {
        return (X509Identity) wallet.get(userIdentityId.toString());
    }

    public String getRoleFromIdentity(UUID userIdentityId) throws IOException {
        X509Identity identity = getIdentity(userIdentityId);
        String roleStr = new String(identity.getCertificate().getExtensionValue("1.2.3.4.5.6.7.8.1"));
        int startIndex = roleStr.indexOf("ROLE_");
        int endIndex = roleStr.indexOf("\"", startIndex);
        return roleStr.substring(startIndex, endIndex);
    }

    public String getCNFromIdentity(UUID userIdentityId) throws IOException {
        X509Identity identity = getIdentity(userIdentityId);
        String subject = identity.getCertificate().getSubjectX500Principal().getName();
        String[] strings = subject.split(",");
        String[] cnStr = strings[0].split("=");
        return cnStr[1];
    }

    public X509Identity getAdminIdentity() throws IOException {
        return (X509Identity) wallet.get(Config.ADMIN_ID);
    }

    public boolean doesExistById(UUID userIdentityId) throws IOException {
        return wallet.get(userIdentityId.toString()) != null;
    }

    public void putIdentity(String userIdentityId, Identity identity) throws IOException {
        wallet.put(userIdentityId, identity);
    }

    public boolean doesAdminExist() throws IOException {
        return wallet.get(Config.ADMIN_ID) != null;
    }
}
