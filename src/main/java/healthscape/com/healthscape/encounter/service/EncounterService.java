package healthscape.com.healthscape.encounter.service;

import healthscape.com.healthscape.encounter.dto.NewEncounterDTO;
import healthscape.com.healthscape.fhir.service.FhirEncounterService;
import healthscape.com.healthscape.patient.service.PatientService;
import healthscape.com.healthscape.patient_records.dtos.PatientRecordDto;
import healthscape.com.healthscape.patient_records.mapper.PatientRecordMapper;
import healthscape.com.healthscape.patient_records.model.PatientRecord;
import healthscape.com.healthscape.practitioner.service.PractitionerService;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.users.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Practitioner;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class EncounterService {


    private final UserService userService;
    private final PatientService patientService;
    private final PractitionerService practitionerService;
    private final FhirEncounterService fhirEncounterService;
    private final PatientRecordMapper patientRecordMapper;

    public PatientRecordDto addNewEncounter(String token, NewEncounterDTO newEncounterDto) throws Exception {
        AppUser user = userService.getUserFromToken(token);
        PatientRecord patientRecord = patientService.getPatientRecord(newEncounterDto.getOfflineDataUrl());
        Practitioner practitioner = practitionerService.getPractitioner(user.getData());
        PatientRecord patientRecordUpdated = fhirEncounterService.updatePatientRecordWithEncounter(patientRecord, newEncounterDto, practitioner);

        String offlineDataUrl = patientService.updatePatientRecord(user, newEncounterDto.getPatientId(), patientRecordUpdated);
        AppUser patient = userService.getUserById(newEncounterDto.getPatientId());
        patient.setData(offlineDataUrl);
        PatientRecordDto patientRecordDto = patientRecordMapper.mapToPatientRecord(patientRecordUpdated);
        return patientRecordDto;
    }

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
