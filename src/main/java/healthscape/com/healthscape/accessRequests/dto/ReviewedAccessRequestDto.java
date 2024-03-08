package healthscape.com.healthscape.accessRequests.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReviewedAccessRequestDto {
    private String requestId;
    private String decision;
    private String availableFrom;
    private String availableUntil;
    private String[] itemsAccess;
}
