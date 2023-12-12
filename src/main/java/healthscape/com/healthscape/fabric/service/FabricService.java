package healthscape.com.healthscape.fabric.service;

import healthscape.com.healthscape.util.Config;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.hyperledger.fabric.gateway.*;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.util.Properties;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FabricService {

    private static final String CHANNEL_NAME = System.getenv().getOrDefault("CHANNEL_NAME", "mychannel");
    private static final String CHAINCODE_NAME = System.getenv().getOrDefault("CHAINCODE_NAME", "basic");

    static {
        System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
    }

    // helper function for getting connected to the gateway
    public static Gateway connect() throws Exception {
        // Load a file system based wallet for managing identities.
        Path walletPath = Paths.get("wallet");
        Wallet wallet = Wallets.newFileSystemWallet(walletPath);
        // load a CCP
        Path networkConfigPath = Paths.get(String.valueOf(Config.CRYPTO_PATH), "connection-org1.yaml");

        Gateway.Builder builder = Gateway.createBuilder();
        builder.identity(wallet, "javaAppUser").networkConfig(networkConfigPath).discovery(true);

        return builder.connect();
    }

    public void run() throws Exception {
        // enrolls the admin and registers the user
//        try {
//            enrollAdmin();
//            registerUser();
//        } catch (Exception e) {
//            System.err.println(e);
//        }

        // connect to the network and invoke the smart contract
        try (Gateway gateway = connect()) {

            // get the network and contract
            Network network = gateway.getNetwork(CHANNEL_NAME);
            Contract contract = network.getContract(CHAINCODE_NAME);

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
            System.exit(1);
        }

    }

    private void enrollAdmin() throws Exception {
        // Create a CA client for interacting with the CA.
        Properties props = new Properties();
        props.put("pemFile", Config.CRYPTO_PATH + "/ca/ca.org1.example.com-cert.pem");
        props.put("allowAllHostNames", "true");
        HFCAClient caClient = HFCAClient.createNewInstance("https://" + "localhost:7054", props);
        CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();
        caClient.setCryptoSuite(cryptoSuite);

        // Delete wallet if it exists from prior runs
        FileUtils.deleteDirectory(new File("wallet"));

        // Create a wallet for managing identities
        Wallet wallet = Wallets.newFileSystemWallet(Paths.get("wallet"));

        // Check to see if we've already enrolled the admin user.
        if (wallet.get("admin") != null) {
            System.out.println("An identity for the admin user \"admin\" already exists in the wallet");
            return;
        }

        // Enroll the admin user, and import the new identity into the wallet.
        final EnrollmentRequest enrollmentRequestTLS = new EnrollmentRequest();
        enrollmentRequestTLS.addHost("localhost");
        enrollmentRequestTLS.setProfile("tls");
        Enrollment enrollment = caClient.enroll("admin", "adminpw", enrollmentRequestTLS);
        Identity user = Identities.newX509Identity("Org1MSP", enrollment);
        wallet.put("admin", user);
        System.out.println("Successfully enrolled user \"admin\" and imported it into the wallet");
    }

    private void registerUser() throws Exception {
        // Create a CA client for interacting with the CA.
        Properties props = new Properties();
        props.put("pemFile", Config.CRYPTO_PATH + "/ca/ca.org1.example.com-cert.pem");
        props.put("allowAllHostNames", "true");
        HFCAClient caClient = HFCAClient.createNewInstance("https://" + Config.CA_ENDPOINT, props);
        CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();
        caClient.setCryptoSuite(cryptoSuite);

        // Create a wallet for managing identities
        Wallet wallet = Wallets.newFileSystemWallet(Paths.get("wallet"));

        // Check to see if we've already enrolled the user.
        if (wallet.get("javaAppUser") != null) {
            System.out.println("An identity for the user \"javaAppUser\" already exists in the wallet");
            return;
        }

        X509Identity adminIdentity = (X509Identity) wallet.get("admin");
        if (adminIdentity == null) {
            System.out.println("\"admin\" needs to be enrolled and added to the wallet first");
            return;
        }
        User admin = new User() {

            @Override
            public String getName() {
                return "admin";
            }

            @Override
            public Set<String> getRoles() {
                return null;
            }

            @Override
            public String getAccount() {
                return null;
            }

            @Override
            public String getAffiliation() {
                return "org1.department1";
            }

            @Override
            public Enrollment getEnrollment() {
                return new Enrollment() {

                    @Override
                    public PrivateKey getKey() {
                        return adminIdentity.getPrivateKey();
                    }

                    @Override
                    public String getCert() {
                        return Identities.toPemString(adminIdentity.getCertificate());
                    }
                };
            }

            @Override
            public String getMspId() {
                return "Org1MSP";
            }

        };

        // Register the user, enroll the user, and import the new identity into the wallet.
        RegistrationRequest registrationRequest = new RegistrationRequest("javaAppUser");
        registrationRequest.setAffiliation("org1.department1");
        registrationRequest.setEnrollmentID("javaAppUser");
        String enrollmentSecret = caClient.register(registrationRequest, admin);
        Enrollment enrollment = caClient.enroll("javaAppUser", enrollmentSecret);
        Identity user = Identities.newX509Identity("Org1MSP", enrollment);
        wallet.put("javaAppUser", user);
        System.out.println("Successfully enrolled user \"javaAppUser\" and imported it into the wallet");
    }

}
