package healthscape.com.healthscape.fhir.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IdentifierDTO {
    private String use;
    private String system;
    private String value;
    
}
