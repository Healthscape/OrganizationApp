package healthscape.com.healthscape.fhir.controller;

import healthscape.com.healthscape.fabric.dto.MyChaincodePatientRecordDto;
import healthscape.com.healthscape.fhir.dtos.FhirUserDto;
import healthscape.com.healthscape.fhir.mapper.FhirMapper;
import healthscape.com.healthscape.fhir.service.FhirService;
import healthscape.com.healthscape.patientRecords.service.PatientRecordService;
import healthscape.com.healthscape.shared.ResponseJson;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping(value = "/fhir", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class FhirController {

    private final FhirService fhirService;
    private final UserService userService;
    private final PatientRecordService patientRecordService;
    private final FhirMapper fhirMapper;

    @GetMapping("/metadata")
    public ResponseEntity<?> test() {
        this.fhirService.getPatientDataHash("1");
        return ResponseEntity.ok().body("OK");

    }

    @GetMapping("/me")
    public ResponseEntity<?> getMe(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        FhirUserDto me = null;
        try{
            AppUser user = userService.getUserFromToken(token);
            if(Objects.equals(user.getRole().getName(), "ROLE_PATIENT")){
                MyChaincodePatientRecordDto myPatientRecordDto = patientRecordService.getMyPatientRecord(user);
                me = this.fhirMapper.map(fhirService.getPatient(myPatientRecordDto.getOfflineDataUrl()));
            }else if(Objects.equals(user.getRole().getName(), "ROLE_PRACTITIONER")){
                me = this.fhirMapper.map(fhirService.getPractitioner(user.getId().toString()));
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body(new ResponseJson(400, e.getMessage()));
        }
        return ResponseEntity.ok().body(me);
    }
}
