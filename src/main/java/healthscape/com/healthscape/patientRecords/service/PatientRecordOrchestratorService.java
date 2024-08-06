package healthscape.com.healthscape.patientRecords.service;

import healthscape.com.healthscape.fabric.dao.IdentifiersDAO;
import healthscape.com.healthscape.fabric.dto.ChaincodePatientRecordDto;
import healthscape.com.healthscape.fabric.dto.MyChaincodePatientRecordDto;
import healthscape.com.healthscape.fabric.dto.RegistrationChaincodeDto;
import healthscape.com.healthscape.fabric.service.FabricAdminService;
import healthscape.com.healthscape.fabric.service.FabricPatientRecordService;
import healthscape.com.healthscape.fhir.dtos.FhirUserDto;
import healthscape.com.healthscape.fhir.dtos.NewPatientRecordDTO;
import healthscape.com.healthscape.fhir.service.FhirPatientRecordService;
import healthscape.com.healthscape.fhir.service.FhirMapperService;
import healthscape.com.healthscape.ipfs.IPFSService;
import healthscape.com.healthscape.patientRecords.dtos.PatientRecordDto;
import healthscape.com.healthscape.patientRecords.dtos.PatientRecordPreview;
import healthscape.com.healthscape.patientRecords.mapper.PatientRecordChaincodeMapper;
import healthscape.com.healthscape.patientRecords.mapper.PatientRecordMapper;
import healthscape.com.healthscape.users.dto.RegisterDto;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.users.service.UserService;
import healthscape.com.healthscape.util.Config;
import healthscape.com.healthscape.util.EncryptionConfig;
import healthscape.com.healthscape.util.HashUtil;
import healthscape.com.healthscape.util.HashWithSalt;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientRecordOrchestratorService {

    private final UserService userService;
    private final FhirMapperService fhirUserService;
    private final FabricPatientRecordService fabricPatientRecordService;
    private final FabricAdminService fabricAdminService;
    private final PatientRecordMapper patientRecordMapper;
    private final PatientRecordChaincodeMapper patientRecordChaincodeMapper;
    private final FhirPatientRecordService fhirPatientRecordService;
    private final IPFSService ipfsService;
    private final EncryptionConfig encryptionConfig;

    // public void registerPatientsRecord(AppUser user, RegisterDto registerDto) throws Exception {
    //     NewPatientRecordDTO patientData = fhirUserService.createNewPatient(user, registerDto.identifier);
    //     String encryptedJsonPatient = encryptionConfig.encryptIPFSData(patientData.getData());

    //     String dataCID = ipfsService.saveJSONObject(patientData.getData());
    //     HashWithSalt dataHashWithSalt = HashUtil.hashWithSalt(patientData.getData());

    //     String identifiersCID = ipfsService.saveJSONObject(patientData.getIdentifiers());
    //     HashWithSalt identifiershashWithSalt = HashUtil.hashWithSalt(patientData.getIdentifiers());

    //     String hashedUserId = HashUtil.hashData(user.getId().toString());

    //     ChaincodePatientRecordDto chaincodeDto = new ChaincodePatientRecordDto(registerDto.identifier, hashedUserId, dataCID, dataHashWithSalt, identifiersCID, identifiershashWithSalt);
    //     fabricPatientRecordService.createPatientRecord(user.getId().toString(), chaincodeDto);
    // }

    // public PatientRecordPreview getPatientRecordPreview(String token, String personalId) throws Exception {
    //     AppUser appUser = userService.getUserFromToken(token);

    //     String hasPermission = fabricPatientRecordService.previewPatientRecord(appUser.getEmail());
    //     if (!Boolean.parseBoolean(hasPermission)) {
    //         throw new Exception("Unauthorized access");
    //     }
    //     Patient patient = fhirUserService.getPatientWithPersonalId(personalId);
    //     return patientRecordMapper.mapToPreview(patient);
    // }
    
    // public void registerPatientsRecord(AppUser user, RegisterDto registerDto) throws Exception {
    //     String encryptedPersonalId = this.encryptionConfig.defaultEncryptionUtil().encryptIfNotAlready(registerDto.identifier);
    //     String dataUrl = fabricPatientRecordService.getPatientRecord(Config.ADMIN_ID, encryptedPersonalId);
    //     Patient patient = null;
    //     if(dataUrl == null || dataUrl.equals("")){
    //         patient = fhirUserService.createNewPatient(user, encryptedPersonalId);
    //         String jsonPatient = fhirUserService.patientToJson(patient);
    //         String ipfsCID = ipfsService.saveJSONObject(jsonPatient);
            
    //         String hashedData = HashUtil.hashData(jsonPatient);
    //         String offlineDataUrl = this.encryptionConfig.defaultEncryptionUtil().encryptIfNotAlready(ipfsCID);
    //         String encryptedUserId = this.encryptionConfig.defaultEncryptionUtil().encryptIfNotAlready(user.getId().toString());
    //         ChaincodePatientRecordDto chaincodePatientRecordDto = new ChaincodePatientRecordDto(offlineDataUrl, hashedData, encryptedUserId, encryptedPersonalId);
    //         fabricPatientRecordService.createPatientRecord(user.getEmail(), chaincodePatientRecordDto);
    //     }else {
    //         String ipfsPatient = ipfsService.getJSONObject(dataUrl).toString();
    //         patient = fhirUserService.jsonToPatient(ipfsPatient);
    //         fhirUserService.addHealthscapeId(user, patient);
    //     }
    // // }

    // public PatientRecordDto getPatientRecord(String token, String recordId) throws Exception {
    //     AppUser appUser = userService.getUserFromToken(token);
    //     String patientRecordStr = fabricPatientRecordService.getPatientRecord(appUser.getEmail(), recordId);
    //     Bundle patientRecordBundle = verifyDataIntegrity(patientRecordStr);
    //     return patientRecordMapper.mapToPatientRecord(patientRecordBundle);
    // }

    // public MyChaincodePatientRecordDto getMyPatientRecord(AppUser appUser) {
    //     try {
    //         String patientRecordStr = fabricPatientRecordService.getMyPatientRecord(appUser.getEmail());
    //         return patientRecordChaincodeMapper.mapToPatientRecordDto(patientRecordStr);
    //     } catch (Exception e) {
    //         System.out.println(e.getMessage());
    //     }
    //     return null;
    // }

    // public void verifyRecordIntegrity(String token) throws Exception {
    //     AppUser appUser = userService.getUserFromToken(token);
    //     String patientRecordStr = fabricPatientRecordService.getMyPatientRecord(appUser.getEmail());
    //     verifyDataIntegrity(patientRecordStr);
    // }

    // private Bundle verifyDataIntegrity(String patientRecordStr) throws Exception {
    //     ChaincodePatientRecordDto fabricRecord = patientRecordChaincodeMapper.mapToPatientRecordDto(patientRecordStr);
    //     Bundle patientRecordBundle = fhirPatientRecordService.getPatientRecord(fabricRecord.getOfflineDataUrl());
    //     String hashedData = fhirPatientRecordService.getPatientDataHash(patientRecordBundle);
    //     if(!Objects.equals(hashedData, fabricRecord.getHashedData())){
    //         throw new Exception("Patient Record is corrupted!");
    //     }
    //     return patientRecordBundle;
    // }
}
