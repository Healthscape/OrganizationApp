package healthscape.com.healthscape.security.api;

import healthscape.com.healthscape.security.model.JwtAuthenticationRequest;
import healthscape.com.healthscape.security.model.UserTokenState;
import healthscape.com.healthscape.security.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class AuthenticationApi {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<UserTokenState> createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest) {
        log.debug("POST RR - AL with payload: {}", authenticationRequest);
        try {
            log.debug("AL U: {}", authenticationRequest.getEmail());
            UserTokenState userTokenState = authenticationService.login(authenticationRequest);
            return ResponseEntity.ok(userTokenState);
        } catch (AuthenticationException e) {
            log.warn("AL FAILED U: {}", authenticationRequest.getEmail());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/refresh")
    public ResponseEntity<UserTokenState> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok().body(authenticationService.refreshToken(request, response));
    }
}
