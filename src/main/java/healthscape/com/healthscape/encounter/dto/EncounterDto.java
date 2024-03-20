package healthscape.com.healthscape.encounter.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@NoArgsConstructor
@Setter
public class EncounterDto {
    String encounterId;
    String recordId;
    String patientId;
    String clinicalImpressionDescription;
    String clinicalImpressionSummary;
    List<DocumentReferenceDto> documents;
    List<MedicationDto> medications;
    List<ConditionDto> conditions;
    Date date;

    public EncounterDto(String encounterId, String recordId, String patientId){
        this.encounterId = encounterId;
        this.recordId = recordId;
        this.patientId = patientId;
    }

}
