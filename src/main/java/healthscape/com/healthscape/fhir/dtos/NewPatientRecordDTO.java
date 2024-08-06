package healthscape.com.healthscape.fhir.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewPatientRecordDTO {
    private String identifiers;
    private String data;
}
