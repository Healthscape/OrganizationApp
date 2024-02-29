package healthscape.com.healthscape.records.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PatientRecordPreview {
    private String name;
    private String surname;
    private String personalId;
    private Date birthDate;
    private String photo;
    private String id;
}
