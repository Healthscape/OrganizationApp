package healthscape.com.healthscape.users.mapper;

import healthscape.com.healthscape.users.dto.UserDto;
import healthscape.com.healthscape.users.model.AppUser;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UsersMapper {

    private final ModelMapper modelMapper;

    public UsersMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

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
        return userDto;
    }

    public UserDto userToUserDto(AppUser appUser, byte[] photo) {
        UserDto userDto = new UserDto();
        modelMapper.map(appUser, userDto);
        userDto.setRole(appUser.getRole().getName());
        userDto.setImage(photo);
        return userDto;
    }
}
