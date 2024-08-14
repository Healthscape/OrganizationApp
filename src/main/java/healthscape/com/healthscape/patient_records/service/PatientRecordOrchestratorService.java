package healthscape.com.healthscape.patient_records.service;

import healthscape.com.healthscape.fabric.service.FabricPatientRecordService;
import healthscape.com.healthscape.patient.service.PatientService;
import healthscape.com.healthscape.patient_records.dtos.PatientRecordDto;
import healthscape.com.healthscape.patient_records.mapper.PatientRecordMapper;
import healthscape.com.healthscape.patient_records.model.PatientRecord;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.users.service.UserService;
import healthscape.com.healthscape.util.EncryptionConfig;
import healthscape.com.healthscape.util.HashUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientRecordOrchestratorService {

    private final UserService userService;
    private final FabricPatientRecordService fabricPatientRecordService;
    private final PatientRecordMapper patientRecordMapper;
    private final EncryptionConfig encryptionConfig;
    private final PatientService patientService;

    public PatientRecordDto getPatientRecord(String token, String userId) throws Exception {
        AppUser appUser = userService.getUserFromToken(token);
        AppUser patient = userService.getUserById(userId);
        String offlineDataUrl = fabricPatientRecordService.getPatientRecord(appUser.getId().toString(), HashUtil.hashData(patient.getId().toString()));
        patient.setData(this.encryptionConfig.encryptDefaultData(offlineDataUrl));
        
        PatientRecord patientRecord = patientService.getPatientRecord(offlineDataUrl);
        PatientRecordDto patientRecordDto = patientRecordMapper.mapToPatientRecord(patientRecord);
        patientRecordDto.setOfflineDataUrl(offlineDataUrl);
        return patientRecordDto;
    }

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
