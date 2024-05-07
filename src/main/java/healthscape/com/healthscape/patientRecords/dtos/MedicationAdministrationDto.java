package healthscape.com.healthscape.patientRecords.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MedicationAdministrationDto {
    String id;
    String encounterId;
    String patient;
    String dosage;
    Date start;
    Date end;
    String status;
    String medication;
}
