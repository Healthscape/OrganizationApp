package healthscape.com.healthscape.users.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class RegisterDto {
    public String name;
    public String surname;
    public String email;
    public String password;
}
