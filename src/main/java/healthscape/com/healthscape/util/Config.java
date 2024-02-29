package healthscape.com.healthscape.util;

import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class Config {

    public static final String MSP_ID = System.getenv().getOrDefault("MSP_ID", "Org1MSP");
    public static final String CHANNEL_NAME = System.getenv().getOrDefault("CHANNEL_NAME", "mychannel");
    public static final String CHAINCODE_NAME = System.getenv().getOrDefault("CHAINCODE_NAME", "ledger");
    // Gateway peer end point.
    public static final String PEER_ENDPOINT = System.getenv().getOrDefault("PEER_ENDPOINT", "localhost:7051");
    public static final String CA_ENDPOINT = System.getenv().getOrDefault("CA_ENDPOINT", "localhost:7054");
    public static final String CA_URL = "https://" + CA_ENDPOINT;
    public static final String OVERRIDE_AUTH = System.getenv().getOrDefault("OVERRIDE_AUTH", "peer0.org1.example.com");
    // Path to crypto materials.
    public static final Path CRYPTO_PATH = Paths.get(System.getenv().getOrDefault("CRYPTO_PATH", "src/main/resources/org1.example.com"));
    // Path to user certificate.
    public static final Path CERT_PATH = Paths.get(CRYPTO_PATH + "users/User1@org1.example.com/msp/signcerts/cert.pem");
    // Path to user private key directory.
    public static final Path KEY_DIR_PATH = Paths.get(CRYPTO_PATH + "users/User1@org1.example.com/msp/keystore");
    // Path to peer tls certificate.
    public static final Path TLS_CERT_PATH = Paths.get(CRYPTO_PATH + "peers/peer0.org1.example.com/tls/ca.crt");
    public static final Path ORG_CA_CERT_PATH = Paths.get(CRYPTO_PATH + "/ca/ca.org1.example.com-cert.pem");
    public static final String WALLET_DIRECTORY = "wallet";
    public static final String ADMIN_IDENTITY_ID = "admin";
    public static final String CA_HOST = System.getenv().getOrDefault("CA_HOST", "localhost");
    public static final String ADMIN_PASSWORD = "adminpw";
    public static final String AFFILIATION = "org1";
    public static final String NETWORK_CONFIG_PATH = Config.CRYPTO_PATH + "/connection-org1.yaml";
    public static String ADMIN_ID = "admin";

    public static void setAdminId(String id) {
        ADMIN_ID = id;
    }
}
