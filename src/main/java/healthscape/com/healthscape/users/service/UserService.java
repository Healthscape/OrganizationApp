package healthscape.com.healthscape.users.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import healthscape.com.healthscape.security.util.TokenUtils;
import healthscape.com.healthscape.users.dto.RegisterDto;
import healthscape.com.healthscape.users.dto.UserDto;
import healthscape.com.healthscape.users.mapper.UsersMapper;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.users.repo.UserRepo;
import healthscape.com.healthscape.util.Config;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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
        token = token.split(" ")[1];
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

    public AppUser register(RegisterDto user, String roleName) {
        log.info("Register user {}", user.email);
        AppUser appUser = objectMapper.convertValue(user, AppUser.class);
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        appUser.setRole(roleService.getByName(roleName));
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
        AppUser appUser = new AppUser();
        appUser.setName("Admin");
        appUser.setSurname("Admin");
        appUser.setEmail(Config.ADMIN_IDENTITY_ID);
        appUser.setPassword(passwordEncoder.encode(Config.ADMIN_PASSWORD));
        appUser.setRole(roleService.getByName("ROLE_ADMIN"));
        AppUser admin = userRepo.save(appUser);
        Config.setAdminId(admin.getId().toString());
        return admin;
    }

    public List<AppUser> getUsers() {
        log.info("Get all users");
        return userRepo.findAll();
    }
}
