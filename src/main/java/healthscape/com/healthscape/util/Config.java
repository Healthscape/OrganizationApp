package healthscape.com.healthscape.util;

import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class Config {

    public static final String HEALTHSCAPE_URL = "http://healthscape.com";
    public static final String CHANNEL_NAME = System.getenv().getOrDefault("CHANNEL_NAME", "mychannel");
    public static final String CHAINCODE_NAME = System.getenv().getOrDefault("CHAINCODE_NAME", "ledger");
    public static final String CA_ENDPOINT = System.getenv().getOrDefault("CA_ENDPOINT", "localhost:7054");
    public static final String CA_URL = "https://" + CA_ENDPOINT;
    // Path to crypto materials.
    public static final Path CRYPTO_PATH = Paths.get(System.getenv().getOrDefault("CRYPTO_PATH", "src/main/resources/org1.example.com"));
    // Path to user certificate.
    public static final Path CERT_PATH = Paths.get(CRYPTO_PATH + "users/User1@org1.example.com/msp/signcerts/cert.pem");
    public static final Path ORG_CA_CERT_PATH = Paths.get(CRYPTO_PATH + "/ca/ca.org1.example.com-cert.pem");
    public static final String WALLET_DIRECTORY = "wallet";
    public static final String ADMIN_IDENTITY_ID = "admin";
    public static final String CA_HOST = System.getenv().getOrDefault("CA_HOST", "localhost");
    public static final String ADMIN_PASSWORD = "adminpw";
    public static final String NETWORK_CONFIG_PATH = Config.CRYPTO_PATH + "/connection-org1.yaml";
    public static final String IPFS_URL = "/ip4/0.0.0.0/tcp/5001";
    public static String ADMIN_ID = "admin";

    public static void setAdminId(String id) {
        ADMIN_ID = id;
    }
}
