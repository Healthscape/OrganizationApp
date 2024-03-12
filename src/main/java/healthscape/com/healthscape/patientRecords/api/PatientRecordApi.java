package healthscape.com.healthscape.patientRecords.api;

import healthscape.com.healthscape.patientRecords.service.PatientRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/records", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
@RestController
public class PatientRecordApi {

    private final PatientRecordService patientRecordService;

    @GetMapping(value = "", params = {"personalId"})
    @PreAuthorize("hasAuthority('find_record_with_personalId')")
    public ResponseEntity<?> findRecordWithPersonalId(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestParam String personalId) {
        return ResponseEntity.ok(this.patientRecordService.findRecordWithPersonalId(token, personalId));
    }

    @GetMapping("/{patientId}")
    @PreAuthorize("hasAuthority('get_patient_record')")
    public ResponseEntity<?> getPatientRecord(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @PathVariable String patientId) {
        return ResponseEntity.ok(this.patientRecordService.getPatientRecord(token, patientId));
    }

    @GetMapping(value = "")
    @PreAuthorize("hasAuthority('get_all_available_patient_record')")
    public ResponseEntity<?> getAllAvailablePatientRecords(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return ResponseEntity.ok(this.patientRecordService.getAllAvailablePatientRecords(token));
    }
}
