package healthscape.com.healthscape.records.api;

import healthscape.com.healthscape.records.service.PatientRecordService;
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

    @GetMapping("/{personalId}")
    @PreAuthorize("hasAuthority('find_record_with_personalId')")
    private ResponseEntity<?> findRecordWithPersonalId(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @PathVariable String personalId) {
        return ResponseEntity.ok(this.patientRecordService.findRecordWithPersonalId(token, personalId));
    }
}
