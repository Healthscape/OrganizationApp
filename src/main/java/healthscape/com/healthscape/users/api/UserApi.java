package healthscape.com.healthscape.users.api;

import healthscape.com.healthscape.fhir.dtos.FhirUserDto;
import healthscape.com.healthscape.shared.ResponseJson;
import healthscape.com.healthscape.users.dto.PasswordDto;
import healthscape.com.healthscape.users.dto.RegisterDto;
import healthscape.com.healthscape.users.dto.RegisterPractitionerDto;
import healthscape.com.healthscape.users.dto.UserDto;
import healthscape.com.healthscape.users.mapper.UsersMapper;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.users.orchestrator.UserOrchestrator;
import healthscape.com.healthscape.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@CrossOrigin
public class UserApi {

    private final UserService userService;
    private final UsersMapper usersMapper;
    private final UserOrchestrator userOrchestrator;


    @GetMapping("/me/detailed")
    public ResponseEntity<?> getMe(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            FhirUserDto me = userOrchestrator.getMe(token);
            return ResponseEntity.ok().body(me);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseJson(400, e.getMessage()));
        }
    }

    @GetMapping("")
    @PreAuthorize("hasAuthority('get_all_users')")
    public ResponseEntity<List<UserDto>> getUsers() {
        List<UserDto> users = usersMapper.usersToUserDtos(userService.getUsers());
        return ResponseEntity.ok().body(users);
    }

    @PostMapping("/patient")
    public ResponseEntity<?> registerPatient(@RequestBody RegisterDto registerDto) {
        try {
            userOrchestrator.registerPatient(registerDto);
            return ResponseEntity.created(getLocationUri()).body(registerDto);
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

    @PutMapping(value = "", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> updateUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestPart("userDto") FhirUserDto userDto, @RequestPart MultipartFile newImage) {
        try {
            userDto.setImage(newImage.getBytes());
            userOrchestrator.updateUser(token, userDto);
            return ResponseEntity.ok(new ResponseJson(200, "OK"));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return ResponseEntity.ok(usersMapper.userToUserDto(userService.getUserFromToken(token)));
    }

    @PutMapping(value = "/info", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> changeInfo(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestPart("userDto") UserDto userDto, @RequestPart MultipartFile newImage) {
        try {
            AppUser user = userService.changeInfo(token, userDto, newImage);
            return ResponseEntity.ok(usersMapper.userToUserDto(user));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PutMapping("/email")
    public ResponseEntity<?> changeEmail(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestBody String email) {
        try {
            AppUser user = userService.changeEmail(token, email);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestBody PasswordDto passwordDto) {
        try {
            userService.changePassword(token, passwordDto);
            return ResponseEntity.ok(new ResponseJson(200, "OK"));
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
