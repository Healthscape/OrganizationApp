package healthscape.com.healthscape.users.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import healthscape.com.healthscape.security.util.TokenUtils;
import healthscape.com.healthscape.users.dto.RegisterDto;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.users.repo.UserRepo;
import healthscape.com.healthscape.util.Config;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepo userRepo;
    private final RoleService roleService;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenUtils tokenUtils;

    public AppUser getUserFromToken(String token) {
        log.info("Fetching user from token: {}", token);
        String email = tokenUtils.getEmailFromToken(token);
        return userRepo.findByEmail(email);
    }

    public AppUser getUserByEmail(String email) {
        log.info("Fetching user {}", email);
        return userRepo.findByEmail(email);
    }

    public AppUser getUserByRole(String role) {
        log.info("Fetching user with role {}", role);
        return userRepo.findAppUserByRole(roleService.getByName(role));
    }

    public AppUser register(RegisterDto user) {
        log.info("Register user {}", user.email);
        AppUser appUser = objectMapper.convertValue(user, AppUser.class);
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        appUser.setRole(roleService.getByName("ROLE_REGULAR"));
        userRepo.save(appUser);
        return appUser;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("LUU - email: {}", email);
        AppUser user = userRepo.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("No user found with email '%s'.", email));
        } else {
            return user;
        }
    }

    public void deleteUser(AppUser user) {
        log.debug("DU - email: {}", user.getEmail());
        userRepo.delete(user);
    }

    public AppUser registerAdmin() {
        log.info("Register user admin");
        RegisterDto user = new RegisterDto("Admin", "Admin", Config.ADMIN_IDENTITY_ID, Config.ADMIN_PASSWORD);
        AppUser appUser = objectMapper.convertValue(user, AppUser.class);
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        appUser.setRole(roleService.getByName("ROLE_ADMIN"));
        AppUser admin = userRepo.save(appUser);
        Config.setAdminId(admin.getId().toString());
        return admin;
    }
}
