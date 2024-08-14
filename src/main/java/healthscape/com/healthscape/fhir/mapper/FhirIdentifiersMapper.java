package healthscape.com.healthscape.fhir.mapper;

import org.hl7.fhir.r4.model.Identifier;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Component
public class FhirIdentifiersMapper {

    private final ObjectMapper objectMapper;
    private final FhirBasicMapper fhirBasicMapper;

    public String identifiersToJson(List<Identifier> identifiers) throws JsonMappingException, JsonProcessingException {
        List<String> parsedIds = new ArrayList<>();
        for (Identifier id : identifiers) {
            String stringId = fhirBasicMapper.toJSON(id, Identifier.class);
            parsedIds.add(stringId);
        }
        return objectMapper.writeValueAsString(parsedIds);
    }

    public List<Identifier> parseJSON(String identifiers) throws JsonMappingException, JsonProcessingException {
        return objectMapper.readValue(identifiers, new TypeReference<List<Identifier>>() {});
    }
    
}
