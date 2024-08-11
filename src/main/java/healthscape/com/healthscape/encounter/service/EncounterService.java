package healthscape.com.healthscape.encounter.service;

import healthscape.com.healthscape.encounter.dto.PatientRecordUpdateDto;
import healthscape.com.healthscape.encounter.dto.StartEncounterDto;
import healthscape.com.healthscape.fabric.dto.ChaincodePatientRecordDto;
import healthscape.com.healthscape.fabric.service.FabricPatientRecordService;
import healthscape.com.healthscape.fhir.service.FhirPatientRecordService;
import healthscape.com.healthscape.patient_records.dtos.AllergyDto;
import healthscape.com.healthscape.patient_records.dtos.ConditionDto;
import healthscape.com.healthscape.patient_records.dtos.MedicationAdministrationDto;
import healthscape.com.healthscape.patient_records.mapper.PatientRecordChaincodeMapper;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.users.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Encounter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class EncounterService {


    private final UserService userService;
    private final FabricPatientRecordService fabricPatientRecordService;
    private final PatientRecordChaincodeMapper patientRecordChaincodeMapper;
    private final FhirPatientRecordService fhirPatientRecordService;

    // public PatientRecordUpdateDto startNewEncounter(String token, String requestId) throws Exception {
    //     AppUser user = userService.getUserFromToken(token);
    //     String patientRecordStr = this.fabricAccessRequestService.isAccessRequestApproved(user.getEmail(), requestId);
    //     ChaincodePatientRecordDto patientRecord = patientRecordChaincodeMapper.mapToPatientRecordDto(patientRecordStr);

    //     if (patientRecord == null) {
    //         throw new Exception("Unauthorized access");
    //     }

    //     StartEncounterDto startEncounterDto = this.fhirPatientRecordService.updatePatientRecordWithEncounter(patientRecord.getHashedUserId(), patientRecord, user);
    //     this.fabricPatientRecordService.updatePatientRecord(user.getEmail(), startEncounterDto.getChaincodePatientRecordDto());
    //     return new PatientRecordUpdateDto(startEncounterDto.getEncounterId(), patientRecord.getHashedUserId());
    // }

    // public void endEncounter(String token, PatientRecordUpdateDto patientRecordUpdateDto) throws Exception {
    //     AppUser user = userService.getUserFromToken(token);
    //     Encounter encounter = this.fhirPatientRecordService.getEncounter(patientRecordUpdateDto.getEncounterId());

    //     if (encounter.getStatus().equals(Encounter.EncounterStatus.FINISHED)) {
    //         throw new Exception("Encounter already finished.");
    //     }

    //     ChaincodePatientRecordDto updatedPatientRecord = this.fhirPatientRecordService.updatePatientRecordsEncounter(patientRecordUpdateDto, encounter);
    //     this.fabricPatientRecordService.updatePatientRecord(user.getEmail(), updatedPatientRecord);
    // }

    // public List<MedicationAdministrationDto> getMedicationAdministrationHistory(String token, String requestId) throws Exception {
    //     AppUser user = userService.getUserFromToken(token);
    //     String patientRecordStr = this.fabricAccessRequestService.isAccessRequestApproved(user.getEmail(), requestId);
    //     ChaincodePatientRecordDto patientRecord = patientRecordChaincodeMapper.mapToPatientRecordDto(patientRecordStr);
    //     if(patientRecord != null){
    //         return this.fhirPatientRecordService.getMedicationAdministrationHistory(patientRecord.getHashedUserId());
    //     }
    //     return null;
    // }

    // public List<ConditionDto> getConditionHistory(String token, String requestId) throws Exception {
    //     AppUser user = userService.getUserFromToken(token);
    //     String patientRecordStr = this.fabricAccessRequestService.isAccessRequestApproved(user.getEmail(), requestId);
    //     ChaincodePatientRecordDto patientRecord = patientRecordChaincodeMapper.mapToPatientRecordDto(patientRecordStr);
    //     if(patientRecord != null){
    //         return this.fhirPatientRecordService.getConditionHistory(patientRecord.getHashedUserId());
    //     }
    //     return null;
    // }

    // public List<AllergyDto> getAllergyHistory(String token, String requestId) throws Exception {
    //     AppUser user = userService.getUserFromToken(token);
    //     String patientRecordStr = this.fabricAccessRequestService.isAccessRequestApproved(user.getEmail(), requestId);
    //     ChaincodePatientRecordDto patientRecord = patientRecordChaincodeMapper.mapToPatientRecordDto(patientRecordStr);
    //     if(patientRecord != null){
    //         return this.fhirPatientRecordService.geAllergyHistory(patientRecord.getHashedUserId());
    //     }
    //     return null;
    // }


}
