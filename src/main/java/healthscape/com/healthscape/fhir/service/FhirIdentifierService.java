package healthscape.com.healthscape.fhir.service;

import java.util.List;
import java.util.ArrayList;

import org.hl7.fhir.r4.model.Identifier;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import healthscape.com.healthscape.fhir.mapper.FhirIdentifiersMapper;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.util.Config;
import healthscape.com.healthscape.util.EncryptionConfig;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FhirIdentifierService {
    
    private final EncryptionConfig encryptionConfig;
    private final FhirIdentifiersMapper mapper;

    public String addHealthscapeId(String userId, List<Identifier> identifiers) throws Exception {
        String encryptedUserId = encryptionConfig.encryptDefaultData(userId);
        Identifier identifier = new Identifier();
        identifier.setSystem(Config.HEALTHSCAPE_URL);
        identifier.setUse(Identifier.IdentifierUse.OFFICIAL);
        identifier.setValue(encryptedUserId);
        identifiers.add(identifier);
        return mapper.identifiersToJson(identifiers);
    }

    public String createIdentifiers(AppUser appUser, String personalId, String userId) throws JsonMappingException, JsonProcessingException{
        List<Identifier> identifiers = new ArrayList<>();
        Identifier identifier = new Identifier();
        identifier.setSystem("http://hl7.org/fhir/sid/us-ssn");
        identifier.setUse(Identifier.IdentifierUse.OFFICIAL);
        identifier.setValue(personalId);
        identifiers.add(identifier);
        Identifier patientIdentifier = new Identifier();
        patientIdentifier.setSystem(Config.HEALTHSCAPE_URL);
        patientIdentifier.setUse(Identifier.IdentifierUse.OFFICIAL);
        patientIdentifier.setValue(userId);
        identifiers.add(patientIdentifier);

        return mapper.identifiersToJson(identifiers);
    }

    public List<Identifier> identifierExist(String identifiersStr) throws Exception{
        List<Identifier> identifiers = mapper.parseJSON(identifiersStr);
        for(Identifier id: identifiers){
            if (id.getSystem().equals(Config.HEALTHSCAPE_URL)) {
                throw new Exception("User is already registered at Healthscape!");
            }
        };
        return identifiers;
    }
}
