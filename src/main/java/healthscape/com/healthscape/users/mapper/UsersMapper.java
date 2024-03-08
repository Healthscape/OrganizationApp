package healthscape.com.healthscape.users.mapper;

import healthscape.com.healthscape.users.dto.UserDto;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.file.service.FileService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UsersMapper {

    private final ModelMapper modelMapper;
    private final FileService fileService;

    public UsersMapper(ModelMapper modelMapper, FileService fileService) {
        this.modelMapper = modelMapper;
        this.fileService = fileService;
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
        try{
            userDto.setImage(fileService.getImage(appUser.getImagePath()));
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return userDto;
    }
}
