package healthscape.com.healthscape.util;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Config {

    public static final String MSP_ID = System.getenv().getOrDefault("MSP_ID", "Org1MSP");
    public static final String CHANNEL_NAME = System.getenv().getOrDefault("CHANNEL_NAME", "mychannel");
    public static final String CHAINCODE_NAME = System.getenv().getOrDefault("CHAINCODE_NAME", "basic");
    // Gateway peer end point.
    public static final String PEER_ENDPOINT = System.getenv().getOrDefault("PEER_ENDPOINT", "localhost:7051");
    public static final String CA_ENDPOINT = System.getenv().getOrDefault("CA_ENDPOINT", "localhost:7054");
    public static final String OVERRIDE_AUTH = System.getenv().getOrDefault("OVERRIDE_AUTH", "peer0.org1.example.com");
    // Path to crypto materials.
    public static final Path CRYPTO_PATH = Paths.get(System.getenv().getOrDefault("CRYPTO_PATH", "src/main/resources/org1.example.com"));
    // Path to user certificate.
    public static final Path CERT_PATH = CRYPTO_PATH.resolve(Paths.get("users/User1@org1.example.com/msp/signcerts/cert.pem"));
    // Path to user private key directory.
    public static final Path KEY_DIR_PATH = CRYPTO_PATH.resolve(Paths.get("users/User1@org1.example.com/msp/keystore"));
    // Path to peer tls certificate.
    public static final Path TLS_CERT_PATH = CRYPTO_PATH.resolve(Paths.get("peers/peer0.org1.example.com/tls/ca.crt"));
}
