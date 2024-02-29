package healthscape.com.healthscape.records.service;

import healthscape.com.healthscape.fabric.util.WalletUtil;
import healthscape.com.healthscape.fhir.service.FhirService;
import healthscape.com.healthscape.records.dtos.PatientRecordPreview;
import healthscape.com.healthscape.records.mapper.PatientRecordMapper;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.users.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PatientRecordService {

    private final FhirService fhirService;
    private final PatientRecordMapper patientRecordMapper;
    private final WalletUtil walletUtil;
    private final UserService userService;

    public PatientRecordPreview findRecordWithPersonalId(String token, String personalId) {
        AppUser appUser = userService.getUserFromToken(token);
        try {
            String role = walletUtil.getRoleFromIdentity(appUser.getId());
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
}
