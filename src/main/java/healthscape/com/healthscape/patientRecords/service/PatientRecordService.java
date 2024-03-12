package healthscape.com.healthscape.patientRecords.service;

import healthscape.com.healthscape.accessRequests.dto.AccessRequestDto;
import healthscape.com.healthscape.accessRequests.service.AccessRequestService;
import healthscape.com.healthscape.fabric.dto.MyChaincodePatientRecordDto;
import healthscape.com.healthscape.fabric.dto.ChaincodePatientRecordDto;
import healthscape.com.healthscape.fabric.service.FabricPatientRecordService;
import healthscape.com.healthscape.fabric.util.WalletUtil;
import healthscape.com.healthscape.fhir.service.FhirService;
import healthscape.com.healthscape.patientRecords.dtos.PatientRecordDto;
import healthscape.com.healthscape.patientRecords.dtos.PatientRecordPreview;
import healthscape.com.healthscape.patientRecords.mapper.PatientRecordChaincodeMapper;
import healthscape.com.healthscape.patientRecords.mapper.PatientRecordMapper;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.users.service.UserService;
import healthscape.com.healthscape.util.EncryptionUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PatientRecordService {

    private final FhirService fhirService;
    private final PatientRecordMapper patientRecordMapper;
    private final PatientRecordChaincodeMapper patientRecordChaincodeMapper;
    private final WalletUtil walletUtil;
    private final UserService userService;
    private final FabricPatientRecordService fabricPatientRecordService;
    private final EncryptionUtil encryptionUtil;
    private final AccessRequestService accessRequestService;


    public PatientRecordPreview findRecordWithPersonalId(String token, String personalId) {
        AppUser appUser = userService.getUserFromToken(token);
        try {
            String role = walletUtil.getRoleFromIdentity(this.encryptionUtil.encrypt(appUser.getId().toString()));
            if (!role.equals(appUser.getRole().getName())) {
                throw new Exception("Unauthorized access");
            }
            Patient patient = fhirService.getPatientWithPersonalId(personalId);
            return patientRecordMapper.mapToPreview(patient);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public void createPatientRecord(AppUser appUser, ChaincodePatientRecordDto patientRecordDto) {
        try {
            String patientRecordStr = fabricPatientRecordService.createPatientRecord(appUser.getEmail(), patientRecordDto);
            System.out.println(patientRecordStr);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void updatePatientRecord(AppUser appUser, ChaincodePatientRecordDto patientRecordDto) {
        try {
            String patientRecordStr = fabricPatientRecordService.updatePatientRecord(appUser.getEmail(), patientRecordDto);
            System.out.println(patientRecordStr);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public PatientRecordDto getPatientRecord(String token, String patientId) {
        AppUser appUser = userService.getUserFromToken(token);
        try {
            String patientRecordStr = fabricPatientRecordService.getPatientRecord(appUser.getEmail(), patientId);
            ChaincodePatientRecordDto fabricRecord = patientRecordChaincodeMapper.mapToPatientRecordDto(patientRecordStr);
            Patient patient = fhirService.getPatient(fabricRecord.getOfflineDataUrl());
            return patientRecordMapper.mapToPatientRecord(patient);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
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

    public void updateMyPatientRecord(AppUser appUser, MyChaincodePatientRecordDto updatedMyPatientRecordDto) {
        try {
            String patientRecordStr = fabricPatientRecordService.updateMyPatientRecord(appUser.getEmail(), updatedMyPatientRecordDto);
            System.out.println(patientRecordStr);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public List<AccessRequestDto> getAllAvailablePatientRecords(String token) {
        return this.accessRequestService.getAllAvailableAccessRequests(token);
    }
}
