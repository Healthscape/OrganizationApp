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
    private String encryptedUserId;
    private String encryptedPersonalId;

    public ChaincodePatientRecordDto(String offlineDataUrl, String hashedData, String encryptedUserId, String encryptedPersonalId) {
        super(offlineDataUrl, hashedData);
        this.encryptedUserId = encryptedUserId;
        this.encryptedPersonalId = encryptedPersonalId;
    }
}
