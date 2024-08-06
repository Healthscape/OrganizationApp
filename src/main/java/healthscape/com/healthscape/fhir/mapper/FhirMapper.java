package healthscape.com.healthscape.fhir.mapper;

import healthscape.com.healthscape.fhir.dtos.FhirUserDto;
import lombok.AllArgsConstructor;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FhirMapper {

    private final PractitionerMapper practitionerMapper;
    private final PatientMapper patientMapper;


    public FhirUserDto map(Practitioner practitioner) {
        return practitionerMapper.fhirPractitionerToFhirUserDto(practitioner);
    }

    public FhirUserDto map(Patient patient) {
        return patientMapper.fhirPatientToFhirUserDto(patient);
    }

    public Patient updatePatient(FhirUserDto userDto, Patient patient) {
        return patientMapper.mapUpdatedToPatient(patient, userDto);
    }

    public Practitioner updatePractitioner(FhirUserDto userDto, Practitioner practitioner) {
        return practitionerMapper.mapUpdatedToPractitioner(practitioner, userDto);
    }

}
