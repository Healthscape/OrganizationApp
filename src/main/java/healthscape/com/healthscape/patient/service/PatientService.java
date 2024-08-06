package healthscape.com.healthscape.patient.service;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import healthscape.com.healthscape.fabric.dao.IdentifiersDAO;
import healthscape.com.healthscape.fabric.dao.PatientRecordDAO;
import healthscape.com.healthscape.fabric.dto.IdentifiersDTO;
import healthscape.com.healthscape.fabric.service.FabricAdminService;
import healthscape.com.healthscape.fabric.service.FabricPatientRecordService;
import healthscape.com.healthscape.fhir.dtos.FhirUserDto;
import healthscape.com.healthscape.fhir.dtos.NewPatientRecordDTO;
import healthscape.com.healthscape.fhir.mapper.FhirMapper;
import healthscape.com.healthscape.fhir.service.FhirIdentifierService;
import healthscape.com.healthscape.fhir.service.FhirMapperService;
import healthscape.com.healthscape.ipfs.IPFSService;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.util.Config;
import healthscape.com.healthscape.util.EncryptionConfig;
import healthscape.com.healthscape.util.HashUtil;
import healthscape.com.healthscape.util.HashWithSalt;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PatientService {
    
    private final FabricAdminService fabricAdminService;
    private final FabricPatientRecordService fabricPatientRecordService;
    private final FhirIdentifierService fhirIdentifierService;
    private final FhirMapperService fhirUserService;
    private final EncryptionConfig encryptionConfig;
    private final IPFSService ipfsService;
    private final ObjectMapper objectMapper;
    private final FhirMapper fhirMapper;

    public IdentifiersDTO patientExist(String identifier) throws Exception{
        IdentifiersDAO identifiersDAO = fabricAdminService.userExists(identifier);
        IdentifiersDTO identifiersDTO = null;
        if(identifiersDAO != null){ 
        String stringIds = ipfsService.getJSONObject(identifiersDAO.getOfflineIdentifierUrl());
        List<Identifier> identifiers = objectMapper.readValue(stringIds, new TypeReference<List<Identifier>>() {});
        identifiersDTO = new IdentifiersDTO(identifiersDAO.getIdentifiersId(), identifiersDAO.getHashedIdentifier(), identifiers);
            for(Identifier id: identifiersDTO.getIdentifiers()){
                if (id.getSystem().equals(Config.HEALTHSCAPE_URL)) {
                    throw new Exception("User is already registered at Healthscape!");
                }
            };
        }
        return identifiersDTO;
    }

    public void addNewIdentifier(String userId, IdentifiersDTO identifiersDTO) throws Exception {
        List<Identifier> identifiers = fhirIdentifierService.addHealthscapeId(userId, identifiersDTO.getIdentifiers());
        identifiersDTO.setIdentifiers(identifiers);

        String stringIdentifiers = objectMapper.writeValueAsString(identifiersDTO.getIdentifiers());
        String identifiersCID = ipfsService.saveJSONObject(stringIdentifiers);
        HashWithSalt identifiershashWithSalt = HashUtil.hashWithSalt(stringIdentifiers);

        IdentifiersDAO identifiersDAO = new IdentifiersDAO(
            identifiersDTO.getIdentifiersId(),
            identifiersDTO.getHashedIdentifier(),
            identifiersCID,
            identifiershashWithSalt.getHash(),
            identifiershashWithSalt.getSalt());
        fabricAdminService.updatePatientIdentifiers(identifiersDAO);
    }

    public String createNewPatient(AppUser appUser, String identifier) throws Exception {
        NewPatientRecordDTO newPatientRecordDTO = fhirUserService.createNewPatient(appUser, identifier);

        String encryptedJsonPatient = encryptionConfig.encryptIPFSData(newPatientRecordDTO.getData());
        String offlineDataUrl = ipfsService.saveJSONObject(encryptedJsonPatient);
        HashWithSalt dataHashWithSalt = HashUtil.hashWithSalt(newPatientRecordDTO.getData());
        PatientRecordDAO patientRecordDAO = new PatientRecordDAO(identifier, offlineDataUrl, dataHashWithSalt.getHash(), dataHashWithSalt.getSalt());

        String offlineIdentifiersUrl = ipfsService.saveJSONObject(newPatientRecordDTO.getIdentifiers());
        HashWithSalt identifiershashWithSalt = HashUtil.hashWithSalt(newPatientRecordDTO.getIdentifiers());
        IdentifiersDAO identifiersDAO = new IdentifiersDAO(identifier, offlineIdentifiersUrl, identifiershashWithSalt.getHash(), identifiershashWithSalt.getSalt());

        return fabricPatientRecordService.createPatientRecord(appUser.getId().toString(), patientRecordDAO, identifiersDAO);
    }

    public FhirUserDto getMe(AppUser user) throws Exception {
        String offlineDataUrl = fabricPatientRecordService.getMe(user.getId().toString());
        String encryptedPatientData = ipfsService.getJSONObject(offlineDataUrl);
        String patientData = encryptionConfig.decryptIPFSData(encryptedPatientData);
        log.info("Retreived patient: {}", patientData);
        Patient patient = fhirUserService.parseJSON(patientData, Patient.class);
        return fhirMapper.map(patient);
    }

    public String updateMyPatientRecord(AppUser user, FhirUserDto userDto) throws Exception {
        Patient patient = getPatient(user.getData());
        Patient updatedPatient = fhirMapper.updatePatient(userDto, patient);
        PatientRecordDAO patientRecordDAO = savePatient(updatedPatient);
        fabricPatientRecordService.updateMyPatientRecord(user.getId().toString(), patientRecordDAO);
        return encryptionConfig.encryptDefaultData(patientRecordDAO.getOfflineDataUrl());
    }

    private Patient getPatient(String encryptedUrl){
        String offlineDataUrl = this.encryptionConfig.decryptDefaultData(encryptedUrl);
        String encryptedPatientData = ipfsService.getJSONObject(offlineDataUrl);
        String patientData = encryptionConfig.decryptIPFSData(encryptedPatientData);
        Patient patient = fhirUserService.parseJSON(patientData, Patient.class);
        return patient;
    }

    private PatientRecordDAO savePatient(Patient patient) throws NoSuchAlgorithmException{
        String data = fhirUserService.toJSON(patient, Patient.class);
        String encryptedJsonPatient = encryptionConfig.encryptIPFSData(data);
        String offlineDataUrl = ipfsService.saveJSONObject(encryptedJsonPatient);
        HashWithSalt dataHashWithSalt = HashUtil.hashWithSalt(data);
        PatientRecordDAO patientRecordDAO = new PatientRecordDAO(null, offlineDataUrl, dataHashWithSalt.getHash(), dataHashWithSalt.getSalt());
        return patientRecordDAO;
    }
}
