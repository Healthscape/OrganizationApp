package healthscape.com.healthscape.patientRecords.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PatientPreview {
    private String name;
    private String surname;
    private String personalId;
    private Date birthDate;
    private String image;
    private String id;
}
