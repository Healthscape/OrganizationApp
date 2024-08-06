package healthscape.com.healthscape.practitioner.service;

import healthscape.com.healthscape.fhir.dtos.FhirUserDto;
import healthscape.com.healthscape.fhir.mapper.FhirMapper;
import healthscape.com.healthscape.fhir.service.FhirMapperService;
import healthscape.com.healthscape.ipfs.IPFSService;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.util.EncryptionConfig;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.security.NoSuchAlgorithmException;

import org.hl7.fhir.r4.model.Practitioner;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PractitionerService {

    private final EncryptionConfig encryptionConfig;
    private final IPFSService ipfsService;
    private final FhirMapper fhirMapper;
    private final FhirMapperService fhirUserService;

    public String createNewPractitioner(String practitionerJson) {
        log.info("Creating new practitioner");
        log.info("Practitioner string: {}", practitionerJson);
        String CID = ipfsService.saveJSONObject(practitionerJson);
        return encryptionConfig.encryptDefaultData(CID);
    }

    public FhirUserDto getMe(AppUser user) throws JsonMappingException, JsonProcessingException {
        String offlineDataUrl = encryptionConfig.decryptDefaultData(user.getData());
        String practitionerString = ipfsService.getJSONObject(offlineDataUrl);
        if(practitionerString == ""){
            throw new Error("Practitioner not found!");
        }
        Practitioner practitioner = fhirUserService.parseJSON(practitionerString, Practitioner.class);
        return fhirMapper.map(practitioner);
    }

    public String updatePractitioner(AppUser user, FhirUserDto userDto) throws Exception {
        Practitioner practitioner = getPractitioner(user.getData());
        Practitioner updatedPractitioner = fhirMapper.updatePractitioner(userDto, practitioner);
       return savePractitioner(updatedPractitioner);
    }

    private Practitioner getPractitioner(String encryptedUrl){
        String offlineDataUrl = this.encryptionConfig.decryptDefaultData(encryptedUrl);
        String practitionerData = ipfsService.getJSONObject(offlineDataUrl);
        Practitioner practitioner = fhirUserService.parseJSON(practitionerData, Practitioner.class);
        return practitioner;
    }

    private String savePractitioner(Practitioner practitioner) throws NoSuchAlgorithmException{
        String data = fhirUserService.toJSON(practitioner, Practitioner.class);
        String offlineDataUrl = ipfsService.saveJSONObject(data);
        return encryptionConfig.encryptDefaultData(offlineDataUrl);
    }


}
