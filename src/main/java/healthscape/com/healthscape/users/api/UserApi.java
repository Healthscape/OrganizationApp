package healthscape.com.healthscape.users.api;

import healthscape.com.healthscape.fabric.service.FabricUserService;
import healthscape.com.healthscape.shared.ResponseJson;
import healthscape.com.healthscape.users.dto.RegisterDto;
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
@RequestMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class UserApi {

    private final UserService userService;
    private final FabricUserService fabricUserService;

    @PostMapping("")
    public ResponseEntity<?> register(@RequestBody RegisterDto user) {
        AppUser appUser = userService.register(user);
        try {
            fabricUserService.registerUser(appUser);
        } catch (Exception e) {
            userService.deleteUser(appUser);
            return ResponseEntity.badRequest().body(new ResponseJson(400, e.getMessage()));
        }
        return ResponseEntity.created(URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().toUriString())).body(appUser);
    }
}
