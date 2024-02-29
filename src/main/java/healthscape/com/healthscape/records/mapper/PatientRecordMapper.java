package healthscape.com.healthscape.records.mapper;

import healthscape.com.healthscape.records.dtos.PatientRecordPreview;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

@Component
public class PatientRecordMapper {

    public PatientRecordPreview mapToPreview(Patient patient) {
        String id = patient.getId();
        String personalId = patient.getIdentifier().get(0).getValue();
        String name = patient.getName().get(0).getGiven().get(0).getValue();
        String surname = patient.getName().get(0).getFamily();
        Date birthDate = patient.getBirthDate();
        String photo = Base64.getEncoder().encodeToString(patient.getPhoto().get(0).getData());
        return new PatientRecordPreview(name, surname, personalId, birthDate, photo, id);
    }
}
