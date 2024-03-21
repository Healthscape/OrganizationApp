package healthscape.com.healthscape.patientRecords.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentReferenceDto {
    String encounterId;
    Date date;
    String practitioner;
    String patient;
    byte[] data;
    String contentType;
    String title;
}
