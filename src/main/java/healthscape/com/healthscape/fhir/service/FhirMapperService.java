package healthscape.com.healthscape.fhir.service;

import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import healthscape.com.healthscape.fhir.config.FhirConfig;
import healthscape.com.healthscape.fhir.dtos.FhirUserDto;
import healthscape.com.healthscape.fhir.dtos.NewPatientRecordDTO;
import healthscape.com.healthscape.fhir.mapper.PatientMapper;
import healthscape.com.healthscape.fhir.mapper.PractitionerMapper;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.users.model.Specialty;
import healthscape.com.healthscape.users.service.SpecialtyService;
import healthscape.com.healthscape.util.Config;
import healthscape.com.healthscape.util.EncryptionConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Resource;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class FhirMapperService {

    // private final IGenericClient fhirClient;
    private final PatientMapper patientMapper;
    private final PractitionerMapper practitionerMapper;
    private final SpecialtyService specialtyService;
    private final FhirConfig fhirConfig;
    private final EncryptionConfig encryptionConfig;
    private final ObjectMapper objectMapper;

    // public Patient getPatientWithPersonalId(String personalId) {
    //     String encryptedId = encryptionConfig.defaultEncryptionUtil().encryptIfNotAlready(personalId);
    //     Bundle bundle = this.fhirClient.search().forResource(Patient.class)
    //             .where(Patient.IDENTIFIER.exactly().systemAndValues("http://hl7.org/fhir/sid/us-ssn", encryptedId))
    //             .returnBundle(Bundle.class).execute();
    //     System.out.println(
    //             fhirConfig.getFhirContext().newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle));
    //     for (Bundle.BundleEntryComponent e : bundle.getEntry()) {
    //         return (Patient) e.getResource();
    //     }
    //     return null;
    // }

    // public Patient getPatient(String id) {
    //     String decryptedId = this.encryptionConfig.defaultEncryptionUtil().decryptIfNotAlready(id);
    //     return this.fhirClient.read().resource(Patient.class).withId(decryptedId).execute();
    // }

    // public Practitioner getPractitioner(String id) {
    //     String encryptedId = encryptionConfig.defaultEncryptionUtil().encryptIfNotAlready(id);
    //     Bundle bundle = this.fhirClient.search().forResource(Practitioner.class)
    //             .where(Practitioner.IDENTIFIER.exactly().systemAndValues(Config.HEALTHSCAPE_URL, encryptedId))
    //             .returnBundle(Bundle.class).execute();
    //     System.out.println(
    //             fhirConfig.getFhirContext().newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle));
    //     for (Bundle.BundleEntryComponent e : bundle.getEntry()) {
    //         return (Practitioner) e.getResource();
    //     }
    //     return null;
    // }

    // public String updatePatient(FhirUserDto userDto, String offlineDataUrl) {
    //     Patient patient = getPatient(offlineDataUrl);
    //     Patient updatePatient = this.patientMapper.mapUpdatedToPatient(patient, userDto);
    //     MethodOutcome methodOutcome = this.fhirClient.update().resource(updatePatient).execute();
    //     return methodOutcome.getResource().getIdElement().getIdPart();
    // }

    // public void updatePractitioner(AppUser user, FhirUserDto userDto) {
    //     Practitioner practitioner = getPractitioner(user.getId().toString());
    //     Practitioner updatePractitioner = this.practitionerMapper.mapUpdatedToPractitioner(practitioner, userDto);
    //     this.fhirClient.update().resource(updatePractitioner).execute();
    // }

    public NewPatientRecordDTO createNewPatient(AppUser appUser, String personalId)
            throws JsonProcessingException, DataFormatException {
        String userId = appUser.getId().toString();
        Patient patient = patientMapper.appUserToFhirPatient(appUser, personalId);
        List<Identifier> identifiers = patientMapper.appUserToFhirIdentifiers(appUser, personalId, userId);
        IParser parser = fhirConfig.getFhirContext().newJsonParser();
        return new NewPatientRecordDTO(identifiersToJson(identifiers), parser.encodeResourceToString(patient));
    }


    public String createNewPractitioner(AppUser appUser, String specialtyCode) {
        Specialty specialty = this.specialtyService.getByCode(specialtyCode);
        Practitioner practitioner = practitionerMapper.appUserToFhirPractitioner(appUser, specialty);
        return fhirConfig.getFhirContext().newJsonParser().encodeResourceToString(practitioner);
    }

    // private String patientToJson(Patient patient) {
    //     IParser parser = fhirConfig.getFhirContext().newJsonParser();
    //     String jsonPatient = parser.encodeResourceToString(patient);
    //     return jsonPatient;
    // }

    // private Patient jsonToPatient(String jsonPatient) {
    //     IParser parser = fhirConfig.getFhirContext().newJsonParser();
    //     Patient parsed = parser.parseResource(Patient.class, jsonPatient);
    //     return parsed;
    // }

    // private List<Identifier> jsonToIdentifiers(String jsonIdentifiers)
    //         throws JsonMappingException, JsonProcessingException {
    //     IParser parser = fhirConfig.getFhirContext().newJsonParser();
    //     List<String> stringIdentifiers = objectMapper.readValue(jsonIdentifiers, new TypeReference<List<String>>() {
    //     });
    //     List<Identifier> identifiers = new ArrayList<>();
    //     for (String id : stringIdentifiers) {
    //         identifiers.add((Identifier) parser.parseResource(id));
    //     }
    //     return identifiers;
    // }

    private String identifiersToJson(List<Identifier> identifiers) throws JsonMappingException, JsonProcessingException {
        IParser parser = fhirConfig.getFhirContext().newJsonParser();
        List<String> parsedIds = new ArrayList<>();
        for (Identifier id : identifiers) {
            String stringId = parser.encodeToString(id);
            parsedIds.add(stringId);
        }
        return objectMapper.writeValueAsString(parsedIds);
    }

    public <T extends Resource> T parseJSON(String data, Class<T> resourceClass) {
        IParser parser = fhirConfig.getFhirContext().newJsonParser();
        return parser.parseResource(resourceClass, data);
    }

    public <T extends Resource> String toJSON(T resource, Class<T> resourceClass) {
        IParser parser = fhirConfig.getFhirContext().newJsonParser();
        return parser.encodeResourceToString(resource);
    }
}
