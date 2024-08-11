package healthscape.com.healthscape.users.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import healthscape.com.healthscape.fhir.dtos.FhirUserDto;
import healthscape.com.healthscape.file.service.FileService;
import healthscape.com.healthscape.users.dto.RegisterDto;
import healthscape.com.healthscape.users.dto.UserDto;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.users.service.RoleService;
import healthscape.com.healthscape.util.Config;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class UsersMapper {

    private final RoleService roleService;
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final FileService fileService;

    public List<UserDto> usersToUserDtos(List<AppUser> users) {
        List<UserDto> userDtos = new ArrayList<>();
        for (AppUser user : users) {
            userDtos.add(userToUserDto(user));
        }
        return userDtos;
    }

    public UserDto userToUserDto(AppUser appUser) {
        UserDto userDto = new UserDto();
        modelMapper.map(appUser, userDto);
        userDto.setRole(appUser.getRole().getName());
        userDto.setId(appUser.getId().toString());
        try {
            userDto.setImage(fileService.getImage(appUser.getImagePath()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return userDto;
    }

    public AppUser registerDtoToAppUser(RegisterDto user, String roleName) {
        AppUser appUser = objectMapper.convertValue(user, AppUser.class);
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        appUser.setRole(roleService.getByName(roleName));
        if (roleName.equals("ROLE_PRACTITIONER")) {
            appUser.setImagePath("practitioner-default.png");
        } else if (roleName.equals("ROLE_PATIENT")) {
            appUser.setImagePath("patient-default.png");
        }
        return appUser;
    }

    public AppUser mapToAdmin() {
        AppUser appUser = new AppUser();
        appUser.setName("Admin");
        appUser.setSurname("Admin");
        appUser.setEmail(Config.ADMIN_IDENTITY_ID);
        appUser.setPassword(passwordEncoder.encode(Config.ADMIN_PASSWORD));
        appUser.setRole(roleService.getByName("ROLE_ADMIN"));
        appUser.setImagePath("admin-default.png");
        return appUser;
    }

    public AppUser updateUser(AppUser user, FhirUserDto userDto, String imagePath) {
        user.setName(userDto.getName());
        user.setSurname(userDto.getSurname());
        user.setImagePath(imagePath);
        return user;
    }
}
