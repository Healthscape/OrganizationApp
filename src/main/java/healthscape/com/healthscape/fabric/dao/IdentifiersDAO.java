package healthscape.com.healthscape.fabric.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IdentifiersDAO {
    private String identifiersId;
    private String hashedIdentifier;
    private String offlineIdentifierUrl;
    private String hashedIdentifiers;
    private String salt;
    private String lastUpdated;
    private String lastUpdatedTxId;

    public IdentifiersDAO(String identifiersId, String hashedIdentifier, String offlineIdentifierUrl, String hashedIdentifiers, String salt){
        this.identifiersId = identifiersId;
        this.hashedIdentifier = hashedIdentifier;
        this.offlineIdentifierUrl = offlineIdentifierUrl;
        this.hashedIdentifiers = hashedIdentifiers;
        this.salt = salt;
    }
    public IdentifiersDAO(String hashedIdentifier, String offlineIdentifierUrl, String hashedIdentifiers, String salt){
        this.hashedIdentifier = hashedIdentifier;
        this.offlineIdentifierUrl = offlineIdentifierUrl;
        this.hashedIdentifiers = hashedIdentifiers;
        this.salt = salt;
    }
}
