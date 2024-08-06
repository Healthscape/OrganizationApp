package healthscape.com.healthscape.fabric.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import healthscape.com.healthscape.util.HashWithSalt;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class ChaincodePatientRecordDto extends MyChaincodePatientRecordDto {
    private String hashedUserId;
    private String personalId;
    private HashWithSalt identifiersHashedWithSalt;
    private String offlineIdentifiersUrl;

    public ChaincodePatientRecordDto(String personalId, String hashedUserId, String offlineDataUrl, HashWithSalt dataHashedWithSalt, String offlineIdentifiersUrl, HashWithSalt identifiersHashedWithSalt) {
        super(offlineDataUrl, dataHashedWithSalt);
        this.hashedUserId = hashedUserId;
        this.personalId = personalId;
        this.identifiersHashedWithSalt = identifiersHashedWithSalt;
        this.offlineIdentifiersUrl = offlineIdentifiersUrl;
    }
}
