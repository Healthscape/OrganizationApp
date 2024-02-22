package healthscape.com.healthscape.users.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RegisterDto {
    public String name;
    public String surname;
    public String email;
    public String password;
    public String identifier;
}
