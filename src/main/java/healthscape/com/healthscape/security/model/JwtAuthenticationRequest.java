package healthscape.com.healthscape.security.model;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class JwtAuthenticationRequest {

    private String email;
    private String password;
}
