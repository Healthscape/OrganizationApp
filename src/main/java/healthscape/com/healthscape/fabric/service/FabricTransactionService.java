package healthscape.com.healthscape.fabric.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.users.service.UserService;
import healthscape.com.healthscape.util.Config;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.gateway.*;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FabricTransactionService {

    static {
        System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
    }

    private final UserService userService;

    private Contract getContract(String email) throws Exception {
        if (email.isBlank() || email.isEmpty()) {
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

    public void queryAssets(String email) throws Exception {

        Contract contract = getContract(email);
        byte[] result;
        System.out.println("\n");
        System.out.println("Evaluate Transaction:QueryAssets assets of size 15");
        result = contract.evaluateTransaction("QueryAssets", "{\"selector\":{\"size\":15}}");
        System.out.println("result: " + new String(result));
    }

    public void test(String email) throws Exception {

        Contract contract = getContract(email);
        byte[] result;
        System.out.println("\n");
        System.out.println("Evaluate Transaction:QueryAssets assets of size 15");
        result = contract.evaluateTransaction("CreatePatientRecord");
        System.out.println("result: " + new String(result));
    }

    public void getAssetHistory(String email, String id) throws Exception {
        Contract contract = getContract(email);
        byte[] result;
        System.out.println("\n");
        System.out.println("Evaluate Transaction:GetAssetHistory " + id);
        result = contract.evaluateTransaction("GetAssetHistory", id);
        System.out.println("result: " + new String(result));
    }

    public void queryAssetsByOwner(String email, String owner) throws Exception {
        Contract contract = getContract(email);
        byte[] result;
        System.out.println("\n");
        System.out.println("Evaluate Transaction:QueryAssetsByOwner " + owner);
        result = contract.evaluateTransaction("QueryAssetsByOwner", owner);
        System.out.println("result: " + new String(result));
    }

    public void transferAssetByColor(String email, String color, String newOwner) throws Exception {
        Contract contract = getContract(email);
        System.out.println("\n");
        System.out.println("Submit Transaction: TransferAssetByColor " + color + " assets > newOwner " + newOwner);
        //        contract.submitTransaction("TransferAssetByColor", "yellow", "Michel");
        contract.submitTransaction("TransferAssetByColor", color, newOwner);
    }

    public void transferAsset(String email, String id, String newOwner) throws Exception {
        Contract contract = getContract(email);
        System.out.println("\n");
        System.out.println("Submit Transaction: TransferAsset " + id + " to owner " + newOwner);
        // TransferAsset transfers an asset with given ID to new owner Tom
        //        contract.submitTransaction("TransferAsset", "asset2", "Tom");
        contract.submitTransaction("TransferAsset", id, newOwner);
    }

    public void deleteAsset(String email, String id) throws Exception {
        Contract contract = getContract(email);
        System.out.println("\n");
        System.out.println("Submit Transaction: DeleteAsset " + id);
        contract.submitTransaction("DeleteAsset", id);
    }

    public void assetExists(String email, String id) throws Exception {
        Contract contract = getContract(email);
        byte[] result;
        System.out.println("\n");
        System.out.println("Evaluate Transaction: AssetExists " + id);
        // AssetExists returns "true" if an asset with given assetID exist
        result = contract.evaluateTransaction("AssetExists", id);
        System.out.println("result: " + new String(result));
    }

    public void readAsset(String email, String id) throws Exception {
        Contract contract = getContract(email);
        byte[] result;
        System.out.println("\n");
        System.out.println("Evaluate Transaction: ReadAsset " + id);
        // ReadAsset returns an asset with given assetID
        result = contract.evaluateTransaction("ReadAsset", id);
        System.out.println("result: " + new String(result));
    }

    public void createAsset(String email, String id) throws Exception {
        Contract contract = getContract(email);
        System.out.println("\n");
        System.out.println("Submit Transaction: CreateAsset " + id);
        // CreateAsset creates an asset with ID asset13, color yellow, owner Tom, size 5 and appraisedValue of 1300
        contract.submitTransaction("CreateAsset", id, "yellow", "5", "Tom", "1300");
    }

    public void getAssetsByRange(String email) throws Exception {
        Contract contract = getContract(email);
        byte[] result;
        System.out.println("\n");
        result = contract.evaluateTransaction("GetAssetsByRange", "", "");
        String jsonString = new String(result, StandardCharsets.UTF_8);
        System.out.println("result: " + jsonString);
    }

    public void initLedger(String email) throws Exception {
        Contract contract = getContract(email);
        System.out.println("\n");
        System.out.println("Submit Transaction: InitLedger creates the initial set of assets on the ledger.");
        contract.submitTransaction("InitLedger");
    }

    public String getAccessRequest(String email, String userId) throws Exception {
        Contract contract = getContract(email);
        System.out.println("\n");
        System.out.println("Evaluate Transaction: GetAccessRequest returns access request if it exists.");
        byte[] result = contract.evaluateTransaction("GetAccessRequest", userId);
        return new String(result);
    }

    public String sendAccessRequest(String email, String userId) throws Exception {
        Contract contract = getContract(email);
        System.out.println("\n");
        System.out.println("Submit Transaction: CreateAccessRequest creates new access request if it does not exist.");
        byte[] result = contract.submitTransaction("CreateAccessRequest", userId, String.valueOf(new Date().getTime()));
        return new String(result);
    }
}
