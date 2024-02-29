package healthscape.com.healthscape.accessRequests.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccessRequestModel {
    private String requestId;
    private String patientId;
    private String practitionerId;
    private Date lastUpdated;
    private Boolean reviewed;
    private String decisionType;
    private String decision;
    private String availableFrom;
    private String availableUntil;
    private String[] itemsAccess;
}
