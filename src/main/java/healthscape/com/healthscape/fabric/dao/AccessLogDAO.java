package healthscape.com.healthscape.fabric.dao;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccessLogDAO {
    @JsonProperty("accessorId")
    private String accessorId;
    @JsonProperty("accessorName")
    private String accessorName;
    @JsonProperty("accessorRole")
    private String accessorRole;
    @JsonProperty("accessorOrg")
    private String accessorOrg;
    @JsonProperty("action")
    private String action;
    @JsonProperty("docType")
    private String docType;
    @JsonProperty("id")
    private String id;
    @JsonProperty("recordId")
    private String recordId;
    @JsonProperty("timestamp")
    private String timestamp;
    @JsonProperty("txId")
    private String txId;
}
