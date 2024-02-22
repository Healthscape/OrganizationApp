package healthscape.com.healthscape.users.dto;

import healthscape.com.healthscape.users.model.AppUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UserDto {

    private String name;
    private String surname;
    private String email;
    private byte[] image;
    private String role;
    private Timestamp dateCreated;

    public UserDto(AppUser user) {
        this.name = user.getName();
        this.surname = user.getSurname();
        this.email = user.getEmail();
        this.image = new byte[' '];
        this.role = user.getRole().getName();
        this.dateCreated = user.getDateCreated();
    }
}
