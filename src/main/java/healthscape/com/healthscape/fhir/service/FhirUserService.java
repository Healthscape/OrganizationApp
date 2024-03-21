package healthscape.com.healthscape.fhir.service;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import healthscape.com.healthscape.fabric.dto.RegistrationChaincodeDto;
import healthscape.com.healthscape.fhir.config.FhirConfig;
import healthscape.com.healthscape.fhir.dtos.FhirUserDto;
import healthscape.com.healthscape.fhir.mapper.PatientMapper;
import healthscape.com.healthscape.fhir.mapper.PractitionerMapper;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.users.model.Specialty;
import healthscape.com.healthscape.users.service.SpecialtyService;
import healthscape.com.healthscape.util.Config;
import healthscape.com.healthscape.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FhirUserService {

    private final IGenericClient fhirClient;
    private final PatientMapper patientMapper;
    private final PractitionerMapper practitionerMapper;
    private final SpecialtyService specialtyService;
    private final FhirConfig fhirConfig;
    private final EncryptionUtil encryptionUtil;

    public RegistrationChaincodeDto registerPatient(AppUser appUser, String personalId) throws Exception {
        Patient patientData = getPatientWithPersonalId(personalId);
        if (patientData == null) {
            String recordId = createNewPatient(appUser, personalId);
            return new RegistrationChaincodeDto(recordId, false);
        } else {
            String recordId = addHealthscapeId(appUser, patientData);
            return new RegistrationChaincodeDto(recordId, true);
        }
    }


    public Patient getPatientWithPersonalId(String personalId) {
        String encryptedId = encryptionUtil.encryptIfNotAlready(personalId);
        Bundle bundle = this.fhirClient.search().forResource(Patient.class).where(Patient.IDENTIFIER.exactly().systemAndValues("http://hl7.org/fhir/sid/us-ssn", encryptedId)).returnBundle(Bundle.class).execute();
        System.out.println(fhirConfig.getFhirContext().newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle));
        for (Bundle.BundleEntryComponent e : bundle.getEntry()) {
            return (Patient) e.getResource();
        }
        return null;
    }

    public void registerPractitioner(AppUser appUser, String specialtyCode) {
        Specialty specialty = this.specialtyService.getByCode(specialtyCode);
        Practitioner practitioner = practitionerMapper.appUserToFhirPractitioner(appUser, specialty);
        this.fhirClient.update().resource(practitioner).execute();
    }

    public Patient getPatient(String id) {
        String decryptedId = this.encryptionUtil.decryptIfNotAlready(id);
        return this.fhirClient.read().resource(Patient.class).withId(decryptedId).execute();
    }

    public Practitioner getPractitioner(String id) {
        String encryptedId = encryptionUtil.encryptIfNotAlready(id);
        Bundle bundle = this.fhirClient.search().forResource(Practitioner.class).where(Practitioner.IDENTIFIER.exactly().systemAndValues(Config.HEALTHSCAPE_URL, encryptedId)).returnBundle(Bundle.class).execute();
        System.out.println(fhirConfig.getFhirContext().newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle));
        for (Bundle.BundleEntryComponent e : bundle.getEntry()) {
            return (Practitioner) e.getResource();
        }
        return null;
    }

    public String updatePatient(FhirUserDto userDto, String offlineDataUrl) {
        Patient patient = getPatient(offlineDataUrl);
        Patient updatePatient = this.patientMapper.mapUpdatedToPatient(patient, userDto);
        MethodOutcome methodOutcome = this.fhirClient.update().resource(updatePatient).execute();
        return methodOutcome.getResource().getIdElement().getIdPart();
    }

    public void updatePractitioner(AppUser user, FhirUserDto userDto) {
        Practitioner practitioner = getPractitioner(user.getId().toString());
        Practitioner updatePractitioner = this.practitionerMapper.mapUpdatedToPractitioner(practitioner, userDto);
        this.fhirClient.update().resource(updatePractitioner).execute();
    }

    private String addHealthscapeId(AppUser appUser, Patient patient) throws Exception {
        for (Identifier id : patient.getIdentifier()) {
            if (id.getSystem().equals(Config.HEALTHSCAPE_URL)) {
                throw new Exception("User is already registered at Healthscape!");
            }
        }
        String userId = appUser.getId().toString();
        Patient updatedPatient = patientMapper.createHealthscapeId(patient, userId);

        MethodOutcome methodOutcome = this.fhirClient.update().resource(updatedPatient).execute();
        return methodOutcome.getResource().getIdElement().getIdPart();
    }

    private String createNewPatient(AppUser appUser, String personalId) {
        String userId = appUser.getId().toString();
        Patient patient = patientMapper.appUserToFhirPatient(appUser, personalId, userId);
        MethodOutcome methodOutcome = this.fhirClient.update().resource(patient).execute();
        return methodOutcome.getResource().getIdElement().getIdPart();
    }
}
