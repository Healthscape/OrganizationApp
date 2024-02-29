package healthscape.com.healthscape.accessRequests.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import healthscape.com.healthscape.accessRequests.dto.AccessRequestDto;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AccessRequestMapper {

    public AccessRequestDto mapToAccessRequestDto(String accessRequestJson) throws JsonProcessingException {
        if (Objects.equals(accessRequestJson, "null")) {
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(accessRequestJson, AccessRequestDto.class);
    }
}
