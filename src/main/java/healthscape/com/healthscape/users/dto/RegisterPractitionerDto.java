package healthscape.com.healthscape.users.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class RegisterPractitionerDto extends RegisterDto {
    public String specialty;

    public RegisterPractitionerDto(String name, String surname, String email, String password, String specialty){
        super(name,surname,email,password, "");
        this.specialty = specialty;
    }
}
