package healthscape.com.healthscape.fhir.controller;

import ca.uhn.fhir.context.FhirContext;
import healthscape.com.healthscape.fhir.converter.PatientDocumentation;
import healthscape.com.healthscape.fhir.service.FhirService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ca.uhn.fhir.util.BundleBuilder;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import org.hl7.fhir.instance.model.api.IBase;
import org.hl7.fhir.r4.model.*;

import java.text.DecimalFormat;
import java.util.Collections;

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
}
