package healthscape.com.healthscape.fhir.service;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import healthscape.com.healthscape.fhir.config.FhirConfig;
import healthscape.com.healthscape.fhir.dtos.FhirUserDto;
import healthscape.com.healthscape.fhir.mapper.FhirMapper;
import healthscape.com.healthscape.fhir.mapper.PatientMapper;
import healthscape.com.healthscape.fhir.mapper.PractitionerMapper;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.users.model.Specialty;
import healthscape.com.healthscape.users.service.SpecialtyService;
import healthscape.com.healthscape.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FhirService {

    private final IGenericClient fhirClient;
    private final PatientMapper patientMapper;
    private final PractitionerMapper practitionerMapper;
    private final SpecialtyService specialtyService;
    private final UserService userService;
    private final FhirMapper fhirMapper;
    private final FhirConfig fhirConfig;

    public void getMetadata() {
        CapabilityStatement conf = fhirClient.capabilities().ofType(CapabilityStatement.class).execute();
        System.out.println(conf.getDescriptionElement().getValue());
    }

    public Patient registerPatient(AppUser appUser, String personalId) {
        Patient patientData = getPatientWithPersonalId(personalId);
        if (patientData != null) {
            Patient patient = patientMapper.appUserToFhirPatient(appUser, personalId);
            MethodOutcome methodOutcome = this.fhirClient.update().resource(patient).execute();
            return (Patient) methodOutcome.getResource();
        } else {
            return null;
        }
    }

    public Patient getPatientWithPersonalId(String personalId) {
        Bundle bundle = this.fhirClient.search().forResource(Patient.class).where(Patient.IDENTIFIER.exactly().systemAndValues("http://hl7.org/fhir/sid/us-ssn", personalId)).returnBundle(Bundle.class).execute();
        System.out.println(fhirConfig.getFhirContext().newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle));
        for (Bundle.BundleEntryComponent e : bundle.getEntry()) {
            return (Patient) e.getResource();
        }
        return null;
    }

    public byte[] registerPractitioner(AppUser appUser, String specialtyCode) {
        Specialty specialty = this.specialtyService.getByCode(specialtyCode);
        Practitioner practitioner = practitionerMapper.appUserToFhirPractitioner(appUser, specialty);
        MethodOutcome methodOutcome = this.fhirClient.update().resource(practitioner).execute();
        return ((Practitioner) methodOutcome.getResource()).getPhoto().get(0).getData();
    }

    public Patient getPatient(String id) {
        return this.fhirClient.read().resource(Patient.class).withId(id).execute();
    }

    public Patient getPatientFromToken(String token) {
        return getPatient(userService.getUserFromToken(token).getId().toString());
    }

    public Practitioner getPractitioner(String id) {
        return this.fhirClient.read().resource(Practitioner.class).withId(id).execute();
    }

    public Practitioner getPractitionerFromToken(String token) {
        return getPractitioner(userService.getUserFromToken(token).getId().toString());
    }

    private Patient updatePatient(Patient patient, FhirUserDto updatedPatient) {
        patient.getName().remove(0);
        patient.addName().addGiven(updatedPatient.getName()).setFamily(updatedPatient.getSurname());
        patient.addTelecom().setSystem(ContactPoint.ContactPointSystem.PHONE).setValue(updatedPatient.getPhone());

        if (updatedPatient.getAddress() != null) {
            String[] addressList = updatedPatient.getAddress().split(", ");
            Address address = new Address();
            StringType stringType = new StringType();
            stringType.setValue(addressList[0]);
            address.setLine(List.of(stringType));
            address.setCity(addressList[1]);
            address.setPostalCode(addressList[2]);
            address.setCountry(addressList[3]);
            patient.setAddress(List.of(address));
        }

        if (updatedPatient.getGender() != null) {
            patient.setGender(Enumerations.AdministrativeGender.valueOf(updatedPatient.getGender()));
        }
        patient.setBirthDate(updatedPatient.getBirthDate());
        if (updatedPatient.getMaritalStatus() != null) {
            CodeableConcept codeableConcept = new CodeableConcept();
            codeableConcept.getCodingFirstRep().setCode(updatedPatient.getMaritalStatus());
            patient.setMaritalStatus(codeableConcept);
        }

        try {
            Attachment attachment = new Attachment();
            attachment.setData(Base64.getDecoder().decode(updatedPatient.getPhoto()));
            patient.setPhoto(List.of(attachment));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return patient;
    }

    public FhirUserDto getUserFromToken(String token) {
        AppUser user = userService.getUserFromToken(token);
        String userRole = user.getRole().getName();
        String userId = user.getId().toString();
        if (userRole.equals("ROLE_PRACTITIONER")) {
            return fhirMapper.map(this.getPractitioner(userId));
        } else if (userRole.equals("ROLE_PATIENT")) {
            return fhirMapper.map(this.getPatient(userId));
        }
        return new FhirUserDto();
    }

    public void changeEmail(String id, String email) {
        Patient patient = getPatient(id);
        for (ContactPoint telecom : patient.getTelecom()) {
            if (telecom.getSystem().equals(ContactPoint.ContactPointSystem.EMAIL)) {
                telecom.setValue(email);
            }
        }
        this.fhirClient.update().resource(patient).withId(patient.getId()).execute();
    }

    public void updateUser(String token, FhirUserDto userDto) throws Exception {
        Patient patient = getPatientFromToken(token);
        String identifier = patient.getIdentifier().get(0).getValue();
        if (!identifier.equals(userDto.getIdentifier())) {
            throw new Exception("Unauthorized access");
        }
        Patient updatePatient = updatePatient(patient, userDto);
        this.fhirClient.update().resource(updatePatient).withId(patient.getId()).execute();

    }
}
