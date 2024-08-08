package healthscape.com.healthscape.users.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.MediaType;

import healthscape.com.healthscape.shared.ResponseJson;
import healthscape.com.healthscape.users.dto.RegisterDto;
import healthscape.com.healthscape.users.dto.RegisterPractitionerDto;
import healthscape.com.healthscape.users.mapper.UsersMapper;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.users.orchestrator.UserOrchestrator;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

import java.net.URI;

@RestController
@RequestMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@CrossOrigin
public class RegisterApi {
    
    private final UserOrchestrator userOrchestrator;
    private final UsersMapper usersMapper;

    @PostMapping("/patient")
    public ResponseEntity<?> registerPatient(@RequestBody RegisterDto registerDto) {
        try {
            AppUser user = userOrchestrator.registerPatient(registerDto);
            return ResponseEntity.created(getLocationUri()).body(usersMapper.userToUserDto(user));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PostMapping("/practitioner")
    @PreAuthorize("hasAuthority('register_practitioner')")
    public ResponseEntity<?> registerPractitioner(@RequestBody RegisterPractitionerDto registerDto) {
        try {
            AppUser user = userOrchestrator.registerPractitioner(registerDto);
            return ResponseEntity.created(getLocationUri()).body(usersMapper.userToUserDto(user));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private ResponseEntity<?> handleException(Exception e) {
        return ResponseEntity.badRequest().body(new ResponseJson(400, e.getMessage()));
    }

    private URI getLocationUri() {
        return URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().toUriString());
    }
}
