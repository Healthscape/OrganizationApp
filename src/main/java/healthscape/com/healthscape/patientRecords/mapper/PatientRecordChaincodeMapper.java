package healthscape.com.healthscape.patientRecords.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import healthscape.com.healthscape.fabric.dto.ChaincodePatientRecordDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@AllArgsConstructor
public class PatientRecordChaincodeMapper {

    public ChaincodePatientRecordDto mapToPatientRecordDto(String patientRecordJson) throws JsonProcessingException {
        if (Objects.equals(patientRecordJson, "null")) {
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(patientRecordJson, ChaincodePatientRecordDto.class);
    }

}
