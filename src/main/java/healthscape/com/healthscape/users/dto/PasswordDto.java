package healthscape.com.healthscape.users.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PasswordDto {

    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
