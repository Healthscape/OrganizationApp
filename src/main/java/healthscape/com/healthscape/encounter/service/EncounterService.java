package healthscape.com.healthscape.encounter.service;

import healthscape.com.healthscape.encounter.dto.PatientRecordUpdateDto;
import healthscape.com.healthscape.encounter.dto.StartEncounterDto;
import healthscape.com.healthscape.fabric.dto.ChaincodePatientRecordDto;
import healthscape.com.healthscape.fabric.service.FabricAccessRequestService;
import healthscape.com.healthscape.fabric.service.FabricPatientRecordService;
import healthscape.com.healthscape.fhir.service.FhirPatientRecordService;
import healthscape.com.healthscape.patientRecords.mapper.PatientRecordChaincodeMapper;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.users.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Encounter;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EncounterService {


    private final UserService userService;
    private final FabricAccessRequestService fabricAccessRequestService;
    private final FabricPatientRecordService fabricPatientRecordService;
    private final PatientRecordChaincodeMapper patientRecordChaincodeMapper;
    private final FhirPatientRecordService fhirPatientRecordService;

    public PatientRecordUpdateDto startNewEncounter(String token, String encryptedPatientId, String chaincodeRecordId) throws Exception {
        AppUser user = userService.getUserFromToken(token);
        String patientRecordStr = this.fabricAccessRequestService.isAccessRequestApproved(user.getEmail(), encryptedPatientId, chaincodeRecordId);
        ChaincodePatientRecordDto patientRecord = patientRecordChaincodeMapper.mapToPatientRecordDto(patientRecordStr);

        if (patientRecord == null) {
            throw new Exception("Unauthorized access");
        }

        StartEncounterDto startEncounterDto = this.fhirPatientRecordService.updatePatientRecordWithEncounter(encryptedPatientId, patientRecord, user);
        this.fabricPatientRecordService.updatePatientRecord(user.getEmail(), startEncounterDto.getChaincodePatientRecordDto());
        return new PatientRecordUpdateDto(startEncounterDto.getEncounterId(), encryptedPatientId);
    }

    public void endEncounter(String token, PatientRecordUpdateDto patientRecordUpdateDto) throws Exception {
        AppUser user = userService.getUserFromToken(token);
        Encounter encounter = this.fhirPatientRecordService.getEncounter(patientRecordUpdateDto.getEncounterId());

        if (encounter.getStatus().equals(Encounter.EncounterStatus.FINISHED)) {
            throw new Exception("Encounter already finished.");
        }

        ChaincodePatientRecordDto updatedPatientRecord = this.fhirPatientRecordService.updatePatientRecordsEncounter(patientRecordUpdateDto, encounter);
        this.fabricPatientRecordService.updatePatientRecord(user.getEmail(), updatedPatientRecord);
    }


}
