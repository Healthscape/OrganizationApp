package healthscape.com.healthscape.patient_records.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClinicalImpressionDto {
    String id;
    String encounterId;
    String patient;
    Date date;
    String status;
    String practitioner;
    String description;
    String summary;
}
