package healthscape.com.healthscape.patientRecords.service;

import healthscape.com.healthscape.fabric.dto.ChaincodePatientRecordDto;
import healthscape.com.healthscape.fabric.dto.MyChaincodePatientRecordDto;
import healthscape.com.healthscape.fabric.dto.RegistrationChaincodeDto;
import healthscape.com.healthscape.fabric.service.FabricPatientRecordService;
import healthscape.com.healthscape.fhir.dtos.FhirUserDto;
import healthscape.com.healthscape.fhir.service.FhirPatientRecordService;
import healthscape.com.healthscape.fhir.service.FhirUserService;
import healthscape.com.healthscape.patientRecords.dtos.PatientRecordDto;
import healthscape.com.healthscape.patientRecords.dtos.PatientRecordPreview;
import healthscape.com.healthscape.patientRecords.mapper.PatientRecordChaincodeMapper;
import healthscape.com.healthscape.patientRecords.mapper.PatientRecordMapper;
import healthscape.com.healthscape.users.dto.RegisterDto;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.users.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PatientRecordOrchestratorService {

    private final UserService userService;
    private final FhirUserService fhirUserService;
    private final FabricPatientRecordService fabricPatientRecordService;
    private final PatientRecordMapper patientRecordMapper;
    private final PatientRecordChaincodeMapper patientRecordChaincodeMapper;
    private final FhirPatientRecordService fhirPatientRecordService;

    public void registerPatientsRecord(AppUser user, RegisterDto registerDto) throws Exception {
        RegistrationChaincodeDto registrationChaincodeDto = fhirUserService.registerPatient(user, registerDto.getIdentifier());
        ChaincodePatientRecordDto patientRecordDto = fhirPatientRecordService.createPatientRecordUpdateDto(registrationChaincodeDto.getRecordId(), user.getId().toString());
        if (!registrationChaincodeDto.isExisting()) {
            fabricPatientRecordService.createPatientRecord(user.getEmail(), patientRecordDto);
        } else {
            fabricPatientRecordService.updatePatientRecord(user.getEmail(), patientRecordDto);
        }
    }

    public PatientRecordPreview getPatientRecordPreview(String token, String personalId) throws Exception {
        AppUser appUser = userService.getUserFromToken(token);

        String hasPermission = fabricPatientRecordService.previewPatientRecord(appUser.getEmail());
        if (!Boolean.parseBoolean(hasPermission)) {
            throw new Exception("Unauthorized access");
        }
        Patient patient = fhirUserService.getPatientWithPersonalId(personalId);
        return patientRecordMapper.mapToPreview(patient);
    }

    public PatientRecordDto getPatientRecord(String token, String patientId) throws Exception {
        AppUser appUser = userService.getUserFromToken(token);
        String patientRecordStr = fabricPatientRecordService.getPatientRecord(appUser.getEmail(), patientId);
        Bundle patientRecordBundle = verifyDataIntegrity(patientRecordStr);
        return patientRecordMapper.mapToPatientRecord(patientRecordBundle);
    }

    public MyChaincodePatientRecordDto getMyPatientRecord(AppUser appUser) {
        try {
            String patientRecordStr = fabricPatientRecordService.getMyPatientRecord(appUser.getEmail());
            return patientRecordChaincodeMapper.mapToPatientRecordDto(patientRecordStr);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void updateMyPatientRecord(AppUser user, FhirUserDto userDto) throws Exception {
        String patientRecordStr = fabricPatientRecordService.getMyPatientRecord(user.getEmail());
        ChaincodePatientRecordDto chaincodePatientRecordDto = patientRecordChaincodeMapper.mapToPatientRecordDto(patientRecordStr);
        String recordId = fhirUserService.updatePatient(userDto, chaincodePatientRecordDto.getOfflineDataUrl());
        ChaincodePatientRecordDto updatedPatientRecordDto = this.fhirPatientRecordService.createPatientRecordUpdateDto(recordId, user.getId().toString());
        fabricPatientRecordService.updateMyPatientRecord(user.getEmail(), updatedPatientRecordDto);
    }

    public void verifyRecordIntegrity(String token) throws Exception {
        AppUser appUser = userService.getUserFromToken(token);
        String patientRecordStr = fabricPatientRecordService.getMyPatientRecord(appUser.getEmail());
        verifyDataIntegrity(patientRecordStr);
    }

    private Bundle verifyDataIntegrity(String patientRecordStr) throws Exception {
        ChaincodePatientRecordDto fabricRecord = patientRecordChaincodeMapper.mapToPatientRecordDto(patientRecordStr);
        Bundle patientRecordBundle = fhirPatientRecordService.getPatientRecord(fabricRecord.getOfflineDataUrl());
        String hashedData = fhirPatientRecordService.getPatientDataHash(patientRecordBundle);
        if(!Objects.equals(hashedData, fabricRecord.getHashedData())){
            throw new Exception("Patient Record is corrupted!");
        }
        return patientRecordBundle;
    }
}
