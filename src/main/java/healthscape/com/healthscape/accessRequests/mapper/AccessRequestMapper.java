package healthscape.com.healthscape.accessRequests.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import healthscape.com.healthscape.accessRequests.dto.AccessRequestDto;
import healthscape.com.healthscape.accessRequests.dto.ChaincodeAccessRequestDto;
import healthscape.com.healthscape.file.service.FileService;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.users.service.SpecialtyService;
import healthscape.com.healthscape.users.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
public class AccessRequestMapper {

    private final UserService userService;
    private final FileService fileService;
    private final SpecialtyService specialtyService;

    public AccessRequestDto mapToAccessRequestDto(String accessRequestJson) throws JsonProcessingException {
        if (Objects.equals(accessRequestJson, "null")) {
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        ChaincodeAccessRequestDto chaincodeAccessRequestDto = objectMapper.readValue(accessRequestJson, ChaincodeAccessRequestDto.class);
        AccessRequestDto requestDto = objectMapper.readValue(accessRequestJson, AccessRequestDto.class);
        return chaincodeToDto(chaincodeAccessRequestDto, requestDto);
    }

    private AccessRequestDto chaincodeToDto(ChaincodeAccessRequestDto chaincodeAccessRequestDto, AccessRequestDto requestDto) {
        AppUser patient = userService.getUserById(chaincodeAccessRequestDto.getPatientId());
        AppUser practitioner = userService.getUserById(chaincodeAccessRequestDto.getPractitionerId());
        requestDto.setPatient(patient.getName() + " " + patient.getSurname());
        requestDto.setPractitioner(practitioner.getName() + " " + practitioner.getSurname());
        requestDto.setSpecialty(this.specialtyService.getByCode(practitioner.getSpecialty()).getName());

        try{
            requestDto.setPatientImage(fileService.getImage(patient.getImagePath()));
            requestDto.setPractitionerImage(fileService.getImage(practitioner.getImagePath()));
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return requestDto;
    }

    public List<AccessRequestDto> mapToAccessRequestsDto(String accessRequestJson) throws JsonProcessingException {
        if (Objects.equals(accessRequestJson, "null")) {
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        ChaincodeAccessRequestDto[] objects = objectMapper.readValue(accessRequestJson, ChaincodeAccessRequestDto[].class);
        AccessRequestDto[] dtos = objectMapper.readValue(accessRequestJson, AccessRequestDto[].class);
        List<AccessRequestDto> requestDtos = new ArrayList<>();
        for (int i = 0; i < objects.length; i++) {
            AccessRequestDto requestDto = this.chaincodeToDto(objects[i], dtos[i]);
            requestDtos.add(requestDto);
        }
        return requestDtos;
    }

    public List<AccessRequestDto> mapAccessRequestHistory(String accessRequestStr) throws JSONException, JsonProcessingException {
        JSONArray jsonArray = new JSONArray(accessRequestStr);
        ObjectMapper objectMapper = new ObjectMapper();
        List<ChaincodeAccessRequestDto> chaincodeRequests = new ArrayList<>();
        List<AccessRequestDto> dtos = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject element = jsonArray.getJSONObject(i);
            JSONObject valueObj = element.getJSONObject("Value");
            ChaincodeAccessRequestDto object = objectMapper.readValue(valueObj.toString(), ChaincodeAccessRequestDto.class);
            AccessRequestDto dto = objectMapper.readValue(valueObj.toString(), AccessRequestDto.class);
            chaincodeRequests.add(object);
            dtos.add(dto);
        }
        List<AccessRequestDto> requestDtos = new ArrayList<>();
        for (int i = 0; i < chaincodeRequests.size(); i++) {
            AccessRequestDto requestDto = this.chaincodeToDto(chaincodeRequests.get(i), dtos.get(i));
            requestDtos.add(requestDto);
        }
        return requestDtos;
    }
}
