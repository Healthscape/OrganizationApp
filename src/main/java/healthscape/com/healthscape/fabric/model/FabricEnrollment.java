package healthscape.com.healthscape.fabric.model;

import lombok.AllArgsConstructor;
import org.hyperledger.fabric.sdk.Enrollment;

import java.security.PrivateKey;

@AllArgsConstructor
public class FabricEnrollment implements Enrollment {
    private PrivateKey key;
    private String cert;

    @Override
    public PrivateKey getKey() {
        return key;
    }

    @Override
    public String getCert() {
        return cert;
    }
}
