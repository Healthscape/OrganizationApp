package healthscape.com.healthscape.fabric.service;

import healthscape.com.healthscape.util.Config;
import healthscape.com.healthscape.util.EncryptionConfig;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hyperledger.fabric.gateway.*;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Transactional
public class FabricTransactionService {

    private final EncryptionConfig encryptionConfig;

    public Contract getContract(String userId) throws Exception {
        if (userId.isBlank() || userId.isEmpty()) {
            System.out.println("UserID cannot be empty.");
            throw new Exception("UserID cannot be empty.");
        }
        // Load a file system based wallet for managing identities.
        Path walletPath = Paths.get(Config.WALLET_DIRECTORY);
        Wallet wallet = Wallets.newFileSystemWallet(walletPath);
        // load a CCP
        Path networkConfigPath = Paths.get(Config.NETWORK_CONFIG_PATH);

        Gateway.Builder builder = Gateway.createBuilder();
        String encryptedUserId = this.encryptionConfig.defaultEncryptionUtil().encryptIfNotAlready(userId);
        builder.identity(wallet, encryptedUserId).networkConfig(networkConfigPath).discovery(false);

        Gateway gateway = builder.connect();
        Network network = gateway.getNetwork(Config.CHANNEL_NAME);
        return network.getContract(Config.CHAINCODE_NAME);
    }
}
