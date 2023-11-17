package healthscape.com.healthscape.security.model;

import lombok.Getter;

import java.util.List;

@Getter
public class UserTokenState {
    private String accessToken;
    private String refreshToken;
    private List<String> roles;
    private Long expiresIn;

    public UserTokenState() {
        this.refreshToken = null;
        this.accessToken = null;
        this.expiresIn = null;
    }

    public UserTokenState(String accessToken, String refreshToken, long expiresIn, List<String> roles) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.roles = roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
