package healthscape.com.healthscape.users.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import healthscape.com.healthscape.users.dto.RegisterDto;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.users.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepo userRepo;
    private final RoleService roleService;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;

    public AppUser getUserByEmail(String email) {
        log.info("Fetching user {}", email);
        return userRepo.findByEmail(email);
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
}
