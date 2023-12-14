package healthscape.com.healthscape.fabric.service;

import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.users.service.UserService;
import healthscape.com.healthscape.util.Config;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.gateway.*;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FabricTransactionService {

    private final UserService userService;

    static {
        System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
    }

    private Contract getContract(String email) throws Exception {
        if(email.isBlank() || email.isEmpty()){
            System.out.println("Email cannot be empty.");
            throw new Exception("Email cannot be empty.");
        }
        // Load a file system based wallet for managing identities.
        Path walletPath = Paths.get(Config.WALLET_DIRECTORY);
        Wallet wallet = Wallets.newFileSystemWallet(walletPath);
        // load a CCP
        Path networkConfigPath = Paths.get(Config.NETWORK_CONFIG_PATH);

        Gateway.Builder builder = Gateway.createBuilder();
        AppUser user = userService.getUserByEmail(email);
        builder.identity(wallet, user.getId().toString()).networkConfig(networkConfigPath).discovery(true);

        Gateway gateway = builder.connect();
        Network network = gateway.getNetwork(Config.CHANNEL_NAME);
        return network.getContract(Config.CHAINCODE_NAME);
    }

    public void run(String email) {
        try {

            Contract contract = getContract(email);

            byte[] result;

            System.out.println("Submit Transaction: InitLedger creates the initial set of assets on the ledger.");
            contract.submitTransaction("InitLedger");

            System.out.println("\n");
            result = contract.evaluateTransaction("GetAllAssets");
            System.out.println("Evaluate Transaction: GetAllAssets, result: " + new String(result));

            System.out.println("\n");
            System.out.println("Submit Transaction: CreateAsset asset213");
            // CreateAsset creates an asset with ID asset213, color yellow, owner Tom, size 5 and appraisedValue of 1300
            contract.submitTransaction("CreateAsset", "asset213", "yellow", "5", "Tom", "1300");

            System.out.println("\n");
            System.out.println("Evaluate Transaction: ReadAsset asset213");
            // ReadAsset returns an asset with given assetID
            result = contract.evaluateTransaction("ReadAsset", "asset213");
            System.out.println("result: " + new String(result));

            System.out.println("\n");
            System.out.println("Evaluate Transaction: AssetExists asset1");
            // AssetExists returns "true" if an asset with given assetID exist
            result = contract.evaluateTransaction("AssetExists", "asset1");
            System.out.println("result: " + new String(result));

            System.out.println("\n");
            System.out.println("Submit Transaction: UpdateAsset asset1, new AppraisedValue : 350");
            // UpdateAsset updates an existing asset with new properties. Same args as CreateAsset
            contract.submitTransaction("UpdateAsset", "asset1", "blue", "5", "Tomoko", "350");

            System.out.println("\n");
            System.out.println("Evaluate Transaction: ReadAsset asset1");
            result = contract.evaluateTransaction("ReadAsset", "asset1");
            System.out.println("result: " + new String(result));

            try {
                System.out.println("\n");
                System.out.println("Submit Transaction: UpdateAsset asset70");
                // Non existing asset asset70 should throw Error
                contract.submitTransaction("UpdateAsset", "asset70", "blue", "5", "Tomoko", "300");
            } catch (Exception e) {
                System.err.println("Expected an error on UpdateAsset of non-existing Asset: " + e);
            }

            System.out.println("\n");
            System.out.println("Submit Transaction: TransferAsset asset1 from owner Tomoko > owner Tom");
            // TransferAsset transfers an asset with given ID to new owner Tom
            contract.submitTransaction("TransferAsset", "asset1", "Tom");

            System.out.println("\n");
            System.out.println("Evaluate Transaction: ReadAsset asset1");
            result = contract.evaluateTransaction("ReadAsset", "asset1");
            System.out.println("result: " + new String(result));
        } catch (Exception e) {
            System.err.println(e);
        }

    }
}
