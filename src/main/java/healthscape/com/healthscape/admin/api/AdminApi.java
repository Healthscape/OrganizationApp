package healthscape.com.healthscape.admin.api;

import healthscape.com.healthscape.admin.dtos.RegisterPractitionerDto;
import healthscape.com.healthscape.fabric.service.FabricUserService;
import healthscape.com.healthscape.fhir.service.FhirService;
import healthscape.com.healthscape.shared.ResponseJson;
import healthscape.com.healthscape.users.mapper.UsersMapper;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/admin", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class AdminApi {

    private final UserService userService;
    private final FabricUserService fabricUserService;
    private final FhirService fhirService;
    private final UsersMapper usersMapper;

    @PostMapping("/practitioner")
    public ResponseEntity<?> registerPractitioner(@RequestBody RegisterPractitionerDto user) {
        AppUser appUser = userService.register(user, "ROLE_PRACTITIONER");
        byte[] photo;
        try {
            // TODO: uncomment
            // fabricUserService.registerUser(appUser);
            photo = fhirService.registerPractitioner(appUser, user.specialty);
        } catch (Exception e) {
            userService.deleteUser(appUser);
            return ResponseEntity.badRequest().body(new ResponseJson(400, e.getMessage()));
        }
        return ResponseEntity.created(URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().toUriString())).body(usersMapper.userToUserDto(appUser, photo));
    }
}
