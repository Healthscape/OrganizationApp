package healthscape.com.healthscape.patient.service;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import healthscape.com.healthscape.fabric.dao.IdentifiersDAO;
import healthscape.com.healthscape.fabric.dao.PatientRecordDAO;
import healthscape.com.healthscape.fabric.dto.IdentifiersDTO;
import healthscape.com.healthscape.fabric.service.FabricAdminService;
import healthscape.com.healthscape.fabric.service.FabricPatientRecordService;
import healthscape.com.healthscape.fhir.dtos.FhirUserDto;
import healthscape.com.healthscape.fhir.mapper.FhirUserMapper;
import healthscape.com.healthscape.fhir.mapper.user_mapper.PatientMapper;
import healthscape.com.healthscape.fhir.service.FhirIdentifierService;
import healthscape.com.healthscape.ipfs.IPFSService;
import healthscape.com.healthscape.patient_records.model.PatientRecord;
import healthscape.com.healthscape.patient_records.parser.PatientRecordParser;
import healthscape.com.healthscape.users.model.AppUser;
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
    
    private final PatientMapper patientMapper;
    private final FhirIdentifierService identifierService;
    private final FhirUserMapper fhirUserMapper;
    private final PatientRecordParser parser;
    private final FabricAdminService fabricAdminService;
    private final FabricPatientRecordService fabricPatientRecordService;
    private final EncryptionConfig encryptionConfig;
    private final IPFSService ipfsService;

    public IdentifiersDTO patientExist(String identifier) throws Exception{
        IdentifiersDAO identifiersDAO = fabricAdminService.userExists(identifier);
        IdentifiersDTO identifiersDTO = null;
        if(identifiersDAO != null){ 
        String stringIds = ipfsService.getJSONObject(identifiersDAO.getOfflineIdentifierUrl());
        List<Identifier> identifiers = identifierService.identifierExist(stringIds);
        identifiersDTO = new IdentifiersDTO(identifiersDAO.getIdentifiersId(), identifiersDAO.getHashedIdentifier(), identifiers);
        }
        return identifiersDTO;
    }

    public void addNewIdentifier(String userId, IdentifiersDTO identifiersDTO) throws Exception {
        String identifiers = identifierService.addHealthscapeId(userId, identifiersDTO.getIdentifiers());
        String identifiersCID = ipfsService.saveJSONObject(identifiers);
        HashWithSalt identifiershashWithSalt = HashUtil.hashWithSalt(identifiers);

        IdentifiersDAO identifiersDAO = new IdentifiersDAO(
            identifiersDTO.getIdentifiersId(),
            identifiersDTO.getHashedIdentifier(),
            identifiersCID,
            identifiershashWithSalt.getHash(),
            identifiershashWithSalt.getSalt());
        fabricAdminService.updatePatientIdentifiers(identifiersDAO);
    }

    public String createNewPatient(AppUser appUser, String identifier) throws Exception {
        String userId = appUser.getId().toString();
        Patient patient = patientMapper.appUserToFhirPatient(appUser, identifier);
        String identifiers = identifierService.createIdentifiers(appUser, identifier, userId);
        String patientRecord = parser.convertPatientRecordToJson(new PatientRecord(patient));

        String encryptedJsonPatient = encryptionConfig.encryptIPFSData(patientRecord);
        String offlineDataUrl = ipfsService.saveJSONObject(encryptedJsonPatient);
        HashWithSalt dataHashWithSalt = HashUtil.hashWithSalt(patientRecord);
        PatientRecordDAO patientRecordDAO = new PatientRecordDAO(identifier, offlineDataUrl, dataHashWithSalt.getHash(), dataHashWithSalt.getSalt());

        String offlineIdentifiersUrl = ipfsService.saveJSONObject(identifiers);
        HashWithSalt identifiershashWithSalt = HashUtil.hashWithSalt(identifiers);
        IdentifiersDAO identifiersDAO = new IdentifiersDAO(identifier, offlineIdentifiersUrl, identifiershashWithSalt.getHash(), identifiershashWithSalt.getSalt());

        return fabricPatientRecordService.createPatientRecord(appUser.getId().toString(), patientRecordDAO, identifiersDAO);
    }

    public FhirUserDto getUserData(AppUser user) throws Exception {
        String offlineDataUrl = user.getData();
        if(offlineDataUrl == "" || offlineDataUrl == null){
            offlineDataUrl = fabricPatientRecordService.getMe(user.getId().toString());
        }else{
            offlineDataUrl = this.encryptionConfig.decryptDefaultData(user.getData());
        }
        
        PatientRecord patientRecord = getPatientRecord(offlineDataUrl);
        return fhirUserMapper.map(patientRecord.getPatient());
    }

    public String updateMyPatientRecord(AppUser user, FhirUserDto userDto) throws Exception {
        String offlineDataUrl = this.encryptionConfig.decryptDefaultData(user.getData());
        PatientRecord patientRecord = getPatientRecord(offlineDataUrl);
        Patient updatedPatient = fhirUserMapper.updatePatient(userDto, patientRecord.getPatient());
        patientRecord.patient = updatedPatient;
        PatientRecordDAO patientRecordDAO = savePatientRecord(patientRecord);
        fabricPatientRecordService.updateMyPatientRecord(user.getId().toString(), patientRecordDAO);
        return encryptionConfig.encryptDefaultData(patientRecordDAO.getOfflineDataUrl());
    }

    public PatientRecord getPatientRecord(String offlineDataUrl) throws JsonMappingException, JsonProcessingException{
        String encryptedPatientData = ipfsService.getJSONObject(offlineDataUrl);
        String patientData = encryptionConfig.decryptIPFSData(encryptedPatientData);
        PatientRecord patientRecord = parser.parsePatientRecord(patientData);
        return patientRecord;
    }

    public PatientRecordDAO savePatientRecord(PatientRecord patientRecord) throws NoSuchAlgorithmException, JsonMappingException, JsonProcessingException{
        String data = parser.convertPatientRecordToJson(patientRecord);
        String encryptedJsonPatient = encryptionConfig.encryptIPFSData(data);
        String offlineDataUrl = ipfsService.saveJSONObject(encryptedJsonPatient);
        HashWithSalt dataHashWithSalt = HashUtil.hashWithSalt(data);
        PatientRecordDAO patientRecordDAO = new PatientRecordDAO(null, offlineDataUrl, dataHashWithSalt.getHash(), dataHashWithSalt.getSalt());
        return patientRecordDAO;
    }

    public String updatePatientRecord(AppUser user, FhirUserDto userDto) throws Exception {
        String offlineDataUrl = this.encryptionConfig.decryptDefaultData(user.getData());
        PatientRecord patientRecord = getPatientRecord(offlineDataUrl);
        Patient updatedPatient = fhirUserMapper.updatePatient(userDto, patientRecord.getPatient());
        patientRecord.patient = updatedPatient;
        PatientRecordDAO patientRecordDAO = savePatientRecord(patientRecord);
        fabricPatientRecordService.updateMyPatientRecord(user.getId().toString(), patientRecordDAO);
        return encryptionConfig.encryptDefaultData(patientRecordDAO.getOfflineDataUrl());
    }

    public String updatePatientRecord(AppUser user, String patientId, PatientRecord updatedPatientRecord) throws Exception {
        PatientRecordDAO patientRecordDAO = savePatientRecord(updatedPatientRecord);
        fabricPatientRecordService.updatePatientRecord(user.getId().toString(),patientId, patientRecordDAO);
        return encryptionConfig.encryptDefaultData(patientRecordDAO.getOfflineDataUrl());
    }
}
