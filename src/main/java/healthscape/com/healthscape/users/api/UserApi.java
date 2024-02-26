package healthscape.com.healthscape.users.api;

import healthscape.com.healthscape.fabric.service.FabricUserService;
import healthscape.com.healthscape.fhir.dtos.FhirUserDto;
import healthscape.com.healthscape.fhir.service.FhirService;
import healthscape.com.healthscape.security.model.UserTokenState;
import healthscape.com.healthscape.security.service.AuthenticationService;
import healthscape.com.healthscape.shared.ResponseJson;
import healthscape.com.healthscape.users.dto.PasswordDto;
import healthscape.com.healthscape.users.dto.RegisterDto;
import healthscape.com.healthscape.users.dto.UserDto;
import healthscape.com.healthscape.users.mapper.UsersMapper;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class UserApi {

    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final FabricUserService fabricUserService;
    private final UsersMapper usersMapper;
    private final FhirService fhirService;

    @GetMapping("")
    @PreAuthorize("hasAuthority('get_all_users')")
    public ResponseEntity<List<UserDto>> getUsers() {
        return ResponseEntity.ok().body(usersMapper.usersToUserDtos(userService.getUsers()));
    }

    @PostMapping("")
    public ResponseEntity<?> registerUser(@RequestBody RegisterDto user) {
        AppUser appUser = userService.register(user, "ROLE_PATIENT");
        byte[] photo;
        try {
            // TODO: uncomment
            //           fabricUserService.registerUser(appUser);
            photo = fhirService.registerPatient(appUser, user.getIdentifier());
        } catch (Exception e) {
            userService.deleteUser(appUser);
            return ResponseEntity.badRequest().body(new ResponseJson(400, e.getMessage()));
        }
        return ResponseEntity.created(URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().toUriString())).body(usersMapper.userToUserDto(appUser, photo));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return ResponseEntity.ok().body(usersMapper.userToUserDto(userService.getUserFromToken(token)));
    }

    @PutMapping("/email")
    public ResponseEntity<?> changeEmail(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestBody String email) {
        UserTokenState tokens;
        try {
            AppUser user = userService.changeEmail(token, email);
            fhirService.changeEmail(user.getId().toString(), email);
            tokens = authenticationService.getAuthentication(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseJson(400, e.getMessage()));
        }

        return ResponseEntity.ok(tokens);
    }

    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestBody PasswordDto passwordDto) {
        try {
            userService.changePassword(token, passwordDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseJson(400, e.getMessage()));
        }

        return ResponseEntity.ok(new ResponseJson(200, "OK"));
    }

    @PutMapping("")
    public ResponseEntity<?> updateUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestBody FhirUserDto userDto) {
        try {
            fhirService.updateUser(token, userDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseJson(400, e.getMessage()));
        }

        return ResponseEntity.ok().body(ResponseEntity.ok().body(new ResponseJson(200, "OK")));
    }

}
