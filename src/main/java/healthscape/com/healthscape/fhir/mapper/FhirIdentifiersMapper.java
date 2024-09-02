package healthscape.com.healthscape.fhir.mapper;

import org.hl7.fhir.r4.model.Identifier;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import healthscape.com.healthscape.fhir.dtos.IdentifierDTO;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        List<String> jsonStringList = objectMapper.readValue(identifiers, new TypeReference<List<String>>() {});

        return jsonStringList.stream()
                .map(jsonString -> {
                    try {
                        IdentifierDTO dto = objectMapper.readValue(jsonString, IdentifierDTO.class);
                        Identifier identifier = new Identifier();
                        identifier.setUse(Identifier.IdentifierUse.fromCode(dto.getUse()));
                        identifier.setSystem(dto.getSystem());
                        identifier.setValue(dto.getValue());
                        return identifier;
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to deserialize JSON string to IdentifierDTO", e);
                    }
                })
                .collect(Collectors.toList());
    }
    
}
