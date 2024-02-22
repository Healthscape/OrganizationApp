package healthscape.com.healthscape.fhir.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FhirUserDto {
    private String identifier;
    private String name;
    private String surname;
    private Date birthDate;
    private String gender;
    private String address;
    private String maritalStatus;
    private byte[] photo;
    private String phone;
    private String email;
    private String specialty;

}
