package healthscape.com.healthscape.admin.dtos;

import healthscape.com.healthscape.users.dto.RegisterDto;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class RegisterPractitionerDto extends RegisterDto {
    public String specialty;
}
