package healthscape.com.healthscape.fhir.service;

import java.util.List;

import org.hl7.fhir.r4.model.Identifier;
import org.springframework.stereotype.Service;

import healthscape.com.healthscape.util.Config;
import healthscape.com.healthscape.util.EncryptionConfig;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FhirIdentifierService {
    
    private final EncryptionConfig encryptionConfig;

    public List<Identifier> addHealthscapeId(String userId, List<Identifier> identifiers) throws Exception {
        String encryptedUserId = encryptionConfig.encryptDefaultData(userId);
        Identifier identifier = new Identifier();
        identifier.setSystem(Config.HEALTHSCAPE_URL);
        identifier.setUse(Identifier.IdentifierUse.OFFICIAL);
        identifier.setValue(encryptedUserId);
        identifiers.add(identifier);
        return identifiers;
    }
}
