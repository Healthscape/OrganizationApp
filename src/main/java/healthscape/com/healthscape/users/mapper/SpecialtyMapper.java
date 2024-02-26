package healthscape.com.healthscape.users.mapper;

import healthscape.com.healthscape.users.dto.SpecialtyDto;
import healthscape.com.healthscape.users.model.Specialty;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SpecialtyMapper {

    private final ModelMapper modelMapper;

    public SpecialtyMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public List<SpecialtyDto> specialtiesToSpecialityDtos(List<Specialty> specialties) {
        List<SpecialtyDto> specialtyDtos = new ArrayList<>();
        for (Specialty specialty : specialties) {
            specialtyDtos.add(specialtyToSpecialtyDto(specialty));
        }
        return specialtyDtos;
    }

    public SpecialtyDto specialtyToSpecialtyDto(Specialty specialty) {
        SpecialtyDto specialtyDto = new SpecialtyDto();
        modelMapper.map(specialty, specialtyDto);
        return specialtyDto;
    }
}
