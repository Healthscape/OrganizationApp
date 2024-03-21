package healthscape.com.healthscape.patientRecords.api;

import healthscape.com.healthscape.patientRecords.dtos.PatientRecordDto;
import healthscape.com.healthscape.patientRecords.dtos.PatientRecordPreview;
import healthscape.com.healthscape.patientRecords.service.PatientRecordOrchestratorService;
import healthscape.com.healthscape.shared.ResponseJson;
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

    private final PatientRecordOrchestratorService patientRecordOrchestratorService;

    @GetMapping(value = "", params = {"personalId"})
    @PreAuthorize("hasAuthority('find_record_with_personalId')")
    public ResponseEntity<?> findRecordWithPersonalId(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestParam String personalId) {
        try {
            PatientRecordPreview patientRecordPreview = this.patientRecordOrchestratorService.getPatientRecordPreview(token, personalId);
            return ResponseEntity.ok(patientRecordPreview);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/{patientId}")
    @PreAuthorize("hasAuthority('get_patient_record')")
    public ResponseEntity<?> getPatientRecord(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @PathVariable String patientId) {
        try {
            PatientRecordDto patientRecordDto = this.patientRecordOrchestratorService.getPatientRecord(token, patientId);
            return ResponseEntity.ok(patientRecordDto);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private ResponseEntity<?> handleException(Exception e) {
        return ResponseEntity.badRequest().body(new ResponseJson(400, e.getMessage()));
    }
}