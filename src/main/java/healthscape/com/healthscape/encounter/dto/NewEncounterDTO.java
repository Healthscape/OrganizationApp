package healthscape.com.healthscape.encounter.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@NoArgsConstructor
@Setter
public class NewEncounterDTO {
    String offlineDataUrl;
    String patientId;
    String clinicalImpressionDescription;
    String clinicalImpressionSummary;
    List<NewDocumentReferenceDto> documents;
    List<NewMedicationDto> medications;
    List<NewConditionDto> conditions;
    List<NewAllergyDto> allergies;
    Date date;

}
