package healthscape.com.healthscape.patient_records.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EncounterDto {
    String id;
    String status;
    String patient;
    String practitioner;
    Date start;
    Date end;
    String specialty;
}
