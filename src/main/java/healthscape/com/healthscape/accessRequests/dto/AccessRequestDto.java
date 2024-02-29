package healthscape.com.healthscape.accessRequests.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessRequestDto {
    private String requestId;
    private Date lastUpdated;
    private Boolean reviewed;
    private String decisionType;
    private String decision;
    private String availableFrom;
    private String availableUntil;
    private String[] itemsAccess;
}
