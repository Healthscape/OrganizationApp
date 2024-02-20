package healthscape.com.healthscape.users.api;

import healthscape.com.healthscape.fabric.service.FabricUserService;
import healthscape.com.healthscape.fhir.service.FhirService;
import healthscape.com.healthscape.shared.ResponseJson;
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
        AppUser appUser = userService.register(user, "ROLE_REGULAR");
        try {
            // TODO: uncomment
//            fabricUserService.registerUser(appUser);
            fhirService.registerPatient(appUser);
        } catch (Exception e) {
            userService.deleteUser(appUser);
            return ResponseEntity.badRequest().body(new ResponseJson(400, e.getMessage()));
        }
        return ResponseEntity.created(URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().toUriString())).body(appUser);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return ResponseEntity.ok().body(usersMapper.userToUserDto(userService.getUserFromToken(token)));
    }
}
