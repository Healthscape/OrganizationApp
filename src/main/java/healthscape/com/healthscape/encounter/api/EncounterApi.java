package healthscape.com.healthscape.encounter.api;

import healthscape.com.healthscape.encounter.dto.NewEncounterDTO;
import healthscape.com.healthscape.encounter.service.EncounterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/encounter", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@CrossOrigin
public class EncounterApi {

    public final EncounterService encounterService;

    @PostMapping("")
    @PreAuthorize("hasAuthority('new_encounter')")
    public ResponseEntity<?> addNewEncounter(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestBody NewEncounterDTO patientRecordUpdateDto) {
        try {
            return ResponseEntity.ok().body(encounterService.addNewEncounter(token, patientRecordUpdateDto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // @PostMapping("/start")
    // @PreAuthorize("hasAuthority('start_new_encounter')")
    // public ResponseEntity<?> startNewEncounter(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestBody String requestId) {
    //     try {
    //         return ResponseEntity.ok().body(encounterService.startNewEncounter(token, requestId));
    //     } catch (Exception e) {
    //         return ResponseEntity.badRequest().body(e.getMessage());
    //     }
    // }

    // @PostMapping("/end")
    // @PreAuthorize("hasAuthority('end_encounter')")
    // public ResponseEntity<?> endEncounter(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestBody PatientRecordUpdateDto patientRecordUpdateDto) {
    //     try {
    //         encounterService.endEncounter(token, patientRecordUpdateDto);
    //         return ResponseEntity.ok().body("");
    //     } catch (Exception e) {
    //         return ResponseEntity.badRequest().body(e.getMessage());
    //     }
    // }

    // @GetMapping("/medication-history/{requestId}")
    // @PreAuthorize("hasAuthority('get_medication_history')")
    // public ResponseEntity<?> getMedicationHistory(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @PathVariable String requestId) {
    //     try {
    //         return ResponseEntity.ok().body(encounterService.getMedicationAdministrationHistory(token, requestId));
    //     } catch (Exception e) {
    //         return ResponseEntity.badRequest().body(e.getMessage());
    //     }
    // }

    // @GetMapping("/condition-history/{requestId}")
    // @PreAuthorize("hasAuthority('get_condition_history')")
    // public ResponseEntity<?> getConditionHistory(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @PathVariable String requestId) {
    //     try {
    //         return ResponseEntity.ok().body(encounterService.getConditionHistory(token, requestId));
    //     } catch (Exception e) {
    //         return ResponseEntity.badRequest().body(e.getMessage());
    //     }
    // }

    // @GetMapping("/allergy-history/{requestId}")
    // @PreAuthorize("hasAuthority('get_allergy_history')")
    // public ResponseEntity<?> getAllergyHistory(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @PathVariable String requestId) {
    //     try {
    //         return ResponseEntity.ok().body(encounterService.getAllergyHistory(token, requestId));
    //     } catch (Exception e) {
    //         return ResponseEntity.badRequest().body(e.getMessage());
    //     }
    // }
}
