package healthscape.com.healthscape.users.api;

import healthscape.com.healthscape.users.dto.SpecialtyDto;
import healthscape.com.healthscape.users.mapper.SpecialtyMapper;
import healthscape.com.healthscape.users.service.SpecialtyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/specialty", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class SpecialtyApi {

    private final SpecialtyService specialtyService;
    private final SpecialtyMapper specialtyMapper;

    @GetMapping("")
    public ResponseEntity<List<SpecialtyDto>> getUsers() {
        return ResponseEntity.ok().body(specialtyMapper.specialtiesToSpecialityDtos(specialtyService.getAllSpecialties()));
    }
}
