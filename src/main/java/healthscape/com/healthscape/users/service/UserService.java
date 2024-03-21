package healthscape.com.healthscape.users.service;

import healthscape.com.healthscape.fhir.dtos.FhirUserDto;
import healthscape.com.healthscape.file.service.FileService;
import healthscape.com.healthscape.security.util.TokenUtils;
import healthscape.com.healthscape.users.dto.PasswordDto;
import healthscape.com.healthscape.users.dto.RegisterDto;
import healthscape.com.healthscape.users.dto.RegisterPractitionerDto;
import healthscape.com.healthscape.users.dto.UserDto;
import healthscape.com.healthscape.users.mapper.UsersMapper;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.users.repo.UserRepo;
import healthscape.com.healthscape.util.Config;
import healthscape.com.healthscape.util.EncryptionUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepo userRepo;
    private final UsersMapper usersMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenUtils tokenUtils;
    private final EncryptionUtil encryptionUtil;
    private final FileService fileService;

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
        return userRepo.findAppUserByRole_Name(role);
    }

    public AppUser register(RegisterDto user) {
        log.info("Register user {}", user.email);
        AppUser appUser = usersMapper.registerDtoToAppUser(user, "ROLE_PATIENT");
        userRepo.save(appUser);
        return appUser;
    }

    public AppUser registerPractitioner(RegisterPractitionerDto user) {
        log.info("Register user {}", user.email);
        AppUser appUser = usersMapper.registerDtoToAppUser(user, "ROLE_PRACTITIONER");
        appUser.setSpecialty(user.getSpecialty());
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
        AppUser admin = userRepo.save(usersMapper.mapToAdmin());
        Config.setAdminId(admin.getId().toString());
        return admin;
    }

    public List<AppUser> getUsers() {
        log.info("Get all users");
        return userRepo.findAll();
    }

    public void changePassword(String token, PasswordDto passwordDto) throws Exception {
        AppUser user = getUserFromToken(token);
        if (!passwordEncoder.matches(passwordDto.getOldPassword(), user.getPassword())) {
            throw new Exception("Entered old password does not match with password in system.");
        }

        if (!passwordDto.getNewPassword().equals(passwordDto.getConfirmPassword())) {
            throw new Exception("Entered new password and confirm password does not match.");

        }

        user.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));
        userRepo.save(user);
    }

    public AppUser changeInfo(String token, UserDto userDto, MultipartFile image) throws IOException {
        AppUser user = getUserFromToken(token);
        if (!image.isEmpty()) {
            String filename = fileService.saveImageToStorage(image);
            user.setImagePath(filename);

        }
        user.setName(userDto.getName());
        user.setSurname(userDto.getSurname());
        userRepo.save(user);
        return user;
    }

    public AppUser changeEmail(String token, String email) throws Exception {
        AppUser user = getUserFromToken(token);
        if (userRepo.findByEmail(email) != null) {
            throw new Exception("Email already exists.");
        }

        user.setEmail(email);
        user = userRepo.save(user);
        return user;
    }

    public AppUser getUserById(String encryptedUserId) {
        String userId = this.encryptionUtil.decryptIfNotAlready(encryptedUserId);
        Optional<AppUser> user = userRepo.findById(UUID.fromString(userId));
        return user.orElse(null);
    }

    public void updateUser(AppUser user, FhirUserDto userDto, String imagePath) {
        AppUser updatedUser = this.usersMapper.updateUser(user, userDto, imagePath);
        userRepo.save(updatedUser);
    }
}
