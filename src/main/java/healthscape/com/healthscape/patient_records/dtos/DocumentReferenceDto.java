package healthscape.com.healthscape.patient_records.dtos;

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
    String id;
    String encounterId;
    Date date;
    String practitioner;
    String specialty;
    String patient;
    String data;
    String contentType;
    String title;
}
