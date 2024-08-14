package healthscape.com.healthscape.practitioner.service;

import healthscape.com.healthscape.fhir.dtos.FhirUserDto;
import healthscape.com.healthscape.fhir.mapper.FhirBasicMapper;
import healthscape.com.healthscape.fhir.mapper.FhirUserMapper;
import healthscape.com.healthscape.fhir.mapper.user_mapper.PractitionerMapper;
import healthscape.com.healthscape.ipfs.IPFSService;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.users.model.Specialty;
import healthscape.com.healthscape.users.service.SpecialtyService;
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

    private final PractitionerMapper practitionerMapper;
    private final SpecialtyService specialtyService;
    private final FhirUserMapper fhirUserMapper;
    private final FhirBasicMapper fhirBasicMapper;
    private final EncryptionConfig encryptionConfig;
    private final IPFSService ipfsService;

    public String createNewPractitioner(AppUser appUser, String specialtyCode) {
        log.info("Creating new practitioner");
        Specialty specialty = this.specialtyService.getByCode(specialtyCode);
        Practitioner practitioner = practitionerMapper.appUserToFhirPractitioner(appUser, specialty);
        String practitionerJson = fhirBasicMapper.toJSON(practitioner, Practitioner.class);
        String CID = ipfsService.saveJSONObject(practitionerJson);
        return encryptionConfig.encryptDefaultData(CID);
    }

    public FhirUserDto getUserData(AppUser user) throws JsonMappingException, JsonProcessingException {
        Practitioner practitioner = getPractitioner(user.getData());
        return fhirUserMapper.map(practitioner);
    }

    public String updatePractitioner(AppUser user, FhirUserDto userDto) throws Exception {
        Practitioner practitioner = getPractitioner(user.getData());
        Practitioner updatedPractitioner = fhirUserMapper.updatePractitioner(userDto, practitioner);
       return savePractitioner(updatedPractitioner);
    }

    public Practitioner getPractitioner(String encryptedUrl){
        String offlineDataUrl = this.encryptionConfig.decryptDefaultData(encryptedUrl);
        String practitionerData = ipfsService.getJSONObject(offlineDataUrl);
        if(practitionerData == ""){
            throw new Error("Practitioner not found!");
        }
        Practitioner practitioner = fhirBasicMapper.parseJSON(practitionerData, Practitioner.class);
        return practitioner;
    }

    private String savePractitioner(Practitioner practitioner) throws NoSuchAlgorithmException{
        String data = fhirBasicMapper.toJSON(practitioner, Practitioner.class);
        String offlineDataUrl = ipfsService.saveJSONObject(data);
        return encryptionConfig.encryptDefaultData(offlineDataUrl);
    }

}
