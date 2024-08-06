package healthscape.com.healthscape.fabric.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatientRecordDAO {
    private String recordId;
    private String hashedIdentifier;
    private String offlineDataUrl;
    private String hashedData;
    private String salt;
    private String lastUpdated;
    private String lastUpdatedTxId;
    
    public PatientRecordDAO(String hashedIdentifier,String offlineDataUrl,String hashedData,String salt){
        this.hashedIdentifier = hashedIdentifier;
        this.offlineDataUrl = offlineDataUrl;
        this.hashedData = hashedData;
        this.salt = salt;
    }
}
