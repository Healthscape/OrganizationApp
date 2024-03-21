package healthscape.com.healthscape.patientRecords.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import healthscape.com.healthscape.fhir.dtos.FhirUserDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientRecordDto {
    FhirUserDto userDto;
    List<EncounterDto> encounters;
    List<MedicationAdministrationDto> medications;
    List<ClinicalImpressionDto> clinicalImpressions;
    List<ConditionDto> conditions;
    List<DocumentReferenceDto> documentReferences;

    public PatientRecordDto(){
        this.encounters = new ArrayList<>();
        this.medications = new ArrayList<>();
        this.clinicalImpressions = new ArrayList<>();
        this.conditions = new ArrayList<>();
        this.documentReferences = new ArrayList<>();
    }
}
