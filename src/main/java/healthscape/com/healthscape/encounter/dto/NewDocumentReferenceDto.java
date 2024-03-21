package healthscape.com.healthscape.encounter.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewDocumentReferenceDto {
    String contentType;
    String title;
    String data;
}
