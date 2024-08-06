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
public class MyChaincodePatientRecordDto {
    private String offlineDataUrl;
    private HashWithSalt dataHashWithSalt;
}
