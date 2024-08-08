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
public class ConditionDto {
    String id;
    String encounterId;
    String patient;
    String practitioner;
    Date date;
    String code;
    String status;
    Date start;
    Date end;
}
