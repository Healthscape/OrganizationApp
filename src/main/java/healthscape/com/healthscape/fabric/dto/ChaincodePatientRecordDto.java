package healthscape.com.healthscape.fabric.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class ChaincodePatientRecordDto extends MyChaincodePatientRecordDto {
    private String userId;

    public ChaincodePatientRecordDto(String offlineDataUrl, String hashedData, String userId, boolean existing) {
        super(offlineDataUrl, hashedData, existing);
        this.userId = userId;
    }
}
