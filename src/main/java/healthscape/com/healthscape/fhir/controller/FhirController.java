package healthscape.com.healthscape.fhir.controller;

import healthscape.com.healthscape.fhir.dtos.FhirUserDto;
import healthscape.com.healthscape.fhir.service.FhirService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/fhir", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class FhirController {

    private final FhirService fhirService;

    @GetMapping("/metadata")
    public ResponseEntity<?> test() {
        this.fhirService.getMetadata();
        return ResponseEntity.ok().body("OK");

    }

    @GetMapping("/me")
    public ResponseEntity<FhirUserDto> getMe(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return ResponseEntity.ok().body(fhirService.getUserFromToken(token));
    }

    //    @PatchMapping("/patient")
    //    public ResponseEntity<FhirPatientDto> updatePatient(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestBody FhirPatientDto updatedPatient) {
    //        return ResponseEntity.ok().body(fhirMapper.map(fhirService.updatePatient(token, updatedPatient)));
    //    }
}
