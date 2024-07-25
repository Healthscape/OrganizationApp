package healthscape.com.healthscape.users.orchestrator;

import healthscape.com.healthscape.fabric.dto.MyChaincodePatientRecordDto;
import healthscape.com.healthscape.fabric.service.FabricUserService;
import healthscape.com.healthscape.fhir.dtos.FhirUserDto;
import healthscape.com.healthscape.fhir.mapper.FhirMapper;
import healthscape.com.healthscape.fhir.service.FhirUserService;
import healthscape.com.healthscape.patientRecords.service.PatientRecordOrchestratorService;
import healthscape.com.healthscape.users.dto.RegisterDto;
import healthscape.com.healthscape.users.dto.RegisterPractitionerDto;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.users.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class UserOrchestrator {

    private final UserService userService;
    private final FabricUserService fabricUserService;
    private final FhirUserService fhirUserService;
    private final PatientRecordOrchestratorService patientRecordOrchestratorService;
    private final FhirMapper fhirMapper;

    public void registerPatient(RegisterDto registerDto) throws Exception {
        AppUser appUser = null;
        try {
            appUser = userService.register(registerDto);
            fabricUserService.registerUser(appUser);
            patientRecordOrchestratorService.registerPatientsRecord(appUser, registerDto);
        } catch (Exception e) {
            if (appUser != null) {
                userService.deleteUser(appUser);
                fabricUserService.unregisterUser(appUser);
            }
            throw e;
        }
    }

    public void updateUser(String token, FhirUserDto userDto) throws Exception {
        AppUser user = userService.getUserFromToken(token);
        if (user.getRole().getName().equals("ROLE_PATIENT")) {
            patientRecordOrchestratorService.updateMyPatientRecord(user, userDto);
        } else if (user.getRole().getName().equals("ROLE_PRACTITIONER")) {
            fhirUserService.updatePractitioner(user, userDto);
        }
    }

    public AppUser registerPractitioner(RegisterPractitionerDto user) throws Exception {
        AppUser appUser = userService.registerPractitioner(user);
        try {
            fhirUserService.registerPractitioner(appUser, user.getSpecialty());
            fabricUserService.registerUser(appUser);
            return appUser;
        } catch (Exception e) {
            userService.deleteUser(appUser);
            throw e;
        }
    }

    public FhirUserDto getMe(String token) {
        AppUser user = userService.getUserFromToken(token);
        FhirUserDto me = null;
        if (Objects.equals(user.getRole().getName(), "ROLE_PATIENT")) {
            MyChaincodePatientRecordDto myPatientRecordDto = patientRecordOrchestratorService.getMyPatientRecord(user);
            me = fhirMapper.map(fhirUserService.getPatient(myPatientRecordDto.getOfflineDataUrl()));
        } else if (Objects.equals(user.getRole().getName(), "ROLE_PRACTITIONER")) {
            me = fhirMapper.map(fhirUserService.getPractitioner(user.getId().toString()));
        }
        return me;
    }
}
