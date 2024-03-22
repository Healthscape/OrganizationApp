package healthscape.com.healthscape.patientRecords.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AllergyDto {
    String encounterId;
    String patient;
    String practitioner;
    Date date;
    String code;
    String status;
    Date start;
    Date end;
}
