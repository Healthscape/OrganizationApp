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

    public Wallet getWallet() {
        return wallet;
    }

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
