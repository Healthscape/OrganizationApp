package healthscape.com.healthscape.users.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@JsonIgnoreProperties
public class UserDto {

    private String id;
    private String name;
    private String surname;
    private String email;
    private byte[] image;
    private String role;
    private Timestamp dateCreated;
}
