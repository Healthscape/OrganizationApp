package healthscape.com.healthscape.fhir.service;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import healthscape.com.healthscape.fhir.mapper.PatientMapper;
import healthscape.com.healthscape.users.model.AppUser;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.CapabilityStatement;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FhirService {

    private final IGenericClient fhirClient;
    private final PatientMapper patientMapper;

    public void getMetadata(){
        CapabilityStatement conf =
                fhirClient.capabilities().ofType(CapabilityStatement.class).execute();
        System.out.println(conf.getDescriptionElement().getValue());
    }

    public void registerPatient(AppUser appUser) {
        this.fhirClient.create().resource(patientMapper.appUserToFhirPatient(appUser)).execute();
    }
}
