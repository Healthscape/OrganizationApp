package healthscape.com.healthscape.users.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RegisterPractitionerDto extends RegisterDto {
    public String specialty;
}
