package healthscape.com.healthscape.users.orchestrator;

import healthscape.com.healthscape.fabric.dto.IdentifiersDTO;
import healthscape.com.healthscape.fabric.service.FabricUserService;
import healthscape.com.healthscape.fhir.dtos.FhirUserDto;
import healthscape.com.healthscape.patient.service.PatientService;
import healthscape.com.healthscape.practitioner.service.PractitionerService;
import healthscape.com.healthscape.users.dto.RegisterDto;
import healthscape.com.healthscape.users.dto.RegisterPractitionerDto;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.users.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserOrchestrator {

    private final UserService userService;
    private final FabricUserService fabricUserService;
    private final PractitionerService practitionerService;
    private final PatientService patientService;


    public AppUser registerPatient(RegisterDto registerDto) throws Exception {
        AppUser appUser = null;
        log.info("Registering patient with info {}", registerDto.getIdentifier());
        try {
            IdentifiersDTO identifiersDTO = patientService.patientExist(registerDto.identifier);
            appUser = userService.register(registerDto);
            fabricUserService.registerUser(appUser);
            if(identifiersDTO != null){
                patientService.addNewIdentifier(appUser.getId().toString(), identifiersDTO);
            }else{
                String offlineDateUrl = patientService.createNewPatient(appUser, registerDto.identifier);
                appUser.setData(offlineDateUrl);
                userService.updateUser(appUser);
            }
        } catch (Exception e) {
            if (appUser != null) {
                userService.deleteUser(appUser);
                fabricUserService.unregisterUser(appUser);
            }
            throw e;
        }

        return appUser;
    }

    public FhirUserDto getMeDetailed(String token) throws Exception {
        AppUser user = userService.getUserFromToken(token);
        FhirUserDto me = null;
        if (Objects.equals(user.getRole().getName(), "ROLE_PATIENT")) {
            me = patientService.getUserData(user);
        } else if (Objects.equals(user.getRole().getName(), "ROLE_PRACTITIONER")) {
            me = practitionerService.getUserData(user);
        }
        return me;
    }

    public void updateUser(String token, FhirUserDto userDto) throws Exception {
        AppUser appUser = userService.getUserFromToken(token);
        String offlineDateUrl = null;
        if (appUser.getRole().getName().equals("ROLE_PATIENT")) {
            offlineDateUrl = patientService.updateMyPatientRecord(appUser, userDto);
        } else if (appUser.getRole().getName().equals("ROLE_PRACTITIONER")) {
            offlineDateUrl = practitionerService.updatePractitioner(appUser, userDto);
        }
        appUser.setData(offlineDateUrl);
        userService.updateUser(appUser);
    }

    public AppUser registerPractitioner(RegisterPractitionerDto user) throws Exception {
        AppUser appUser = userService.registerPractitioner(user);
        try {
            fabricUserService.registerUser(appUser);
            String offlineDateUrl = practitionerService.createNewPractitioner(appUser, user.getSpecialty());
            appUser.setData(offlineDateUrl);
            return appUser;
        } catch (Exception e) {
            userService.deleteUser(appUser);
            throw e;
        }
    }
}
