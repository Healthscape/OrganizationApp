package healthscape.com.healthscape.patientRecords.mapper;

import healthscape.com.healthscape.fhir.mapper.FhirMapper;
import healthscape.com.healthscape.patientRecords.dtos.PatientRecordDto;
import healthscape.com.healthscape.patientRecords.dtos.PatientRecordPreview;
import healthscape.com.healthscape.util.EncryptionUtil;
import lombok.AllArgsConstructor;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

@Component
@AllArgsConstructor
public class PatientRecordMapper {

    private final FhirMapper fhirMapper;
    private final EncryptionUtil encryptionUtil;

    public PatientRecordPreview mapToPreview(Patient patient) {
        String id = "";
        for(Identifier identifier: patient.getIdentifier()){
            if(identifier.getSystem().equals("http://healthscape.com")){
                id = identifier.getValue();
                break;
            }
        }
        String personalId = encryptionUtil.decrypt(patient.getIdentifier().get(0).getValue());
        String name = patient.getName().get(0).getGiven().get(0).getValue();
        String surname = patient.getName().get(0).getFamily();
        Date birthDate = patient.getBirthDate();
        String photo = Base64.getEncoder().encodeToString(patient.getPhoto().get(0).getData());
        return new PatientRecordPreview(name, surname, personalId, birthDate, photo, id);
    }

    public PatientRecordDto mapToPatientRecord(Patient patient) {
        return new PatientRecordDto();
    }
}
