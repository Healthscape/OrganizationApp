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
}
