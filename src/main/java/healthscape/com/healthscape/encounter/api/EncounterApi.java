package healthscape.com.healthscape.encounter.api;

import healthscape.com.healthscape.encounter.dto.PatientRecordUpdateDto;
import healthscape.com.healthscape.encounter.service.EncounterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/encounter", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class EncounterApi {

    public final EncounterService encounterService;

    @PostMapping("/start/{recordId}/{patientId}")
    @PreAuthorize("hasAuthority('start_new_encounter')")
    public ResponseEntity<?> startNewEncounter(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @PathVariable String recordId, @PathVariable String patientId) {
        PatientRecordUpdateDto patientRecordUpdateDto = null;
        try {
            patientRecordUpdateDto = encounterService.startNewEncounter(token, patientId, recordId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().body(patientRecordUpdateDto);
    }

    @PostMapping("/end")
    @PreAuthorize("hasAuthority('end_encounter')")
    public ResponseEntity<?> endEncounter(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestBody PatientRecordUpdateDto patientRecordUpdateDto) {
        try {
            encounterService.endEncounter(token, patientRecordUpdateDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().body("");
    }
}