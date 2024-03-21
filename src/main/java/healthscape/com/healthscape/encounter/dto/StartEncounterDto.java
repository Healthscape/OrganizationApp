package healthscape.com.healthscape.encounter.dto;

import healthscape.com.healthscape.fabric.dto.ChaincodePatientRecordDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class StartEncounterDto {
    private String encounterId;
    private ChaincodePatientRecordDto chaincodePatientRecordDto;
}
