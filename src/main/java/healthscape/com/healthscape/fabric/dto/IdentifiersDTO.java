package healthscape.com.healthscape.fabric.dto;

import java.util.List;

import org.hl7.fhir.r4.model.Identifier;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IdentifiersDTO {
    private String identifiersId;
    private String hashedIdentifier;
    private List<Identifier> identifiers;
}
