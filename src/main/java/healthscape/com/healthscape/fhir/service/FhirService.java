package healthscape.com.healthscape.fhir.service;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
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

    public void getMetadata(){
        CapabilityStatement conf =
                fhirClient.capabilities().ofType(CapabilityStatement.class).execute();
        System.out.println(conf.getDescriptionElement().getValue());
    }

    public byte[] registerPatient(AppUser appUser, String ssn) {
        Patient patient = patientMapper.appUserToFhirPatient(appUser,ssn);
        MethodOutcome methodOutcome = this.fhirClient.update().resource(patient).execute();
        return ((Patient) methodOutcome.getResource()).getPhoto().get(0).getData();
    }

    public byte[] registerPractitioner(AppUser appUser, String specialtyCode) {
        Specialty specialty = this.specialtyService.getByCode(specialtyCode);
        Practitioner practitioner = practitionerMapper.appUserToFhirPractitioner(appUser, specialty);
        MethodOutcome methodOutcome = this.fhirClient.create().resource(practitioner).execute();
        return ((Practitioner) methodOutcome.getResource()).getPhoto().get(0).getData();
    }

    public Patient getPatient(String id) {
        return this.fhirClient.read().resource(Patient.class).withId(id).execute();
    }

    public Patient getPatientFromToken(String token){
        return getPatient(userService.getUserFromToken(token).getId().toString());
    }

    public Practitioner getPractitioner(String id) {
        return this.fhirClient.read().resource(Practitioner.class).withId(id).execute();
    }

    public Practitioner getPractitionerFromToken(String token){
        return getPractitioner(userService.getUserFromToken(token).getId().toString());
    }

    public Patient updatePatient(String token, FhirUserDto updatedPatient) {
        Patient patient = getPatientFromToken(token);

        patient.getName().remove(0);
        patient.addName().addGiven(updatedPatient.getName()).setFamily(updatedPatient.getSurname());
        patient.addTelecom().setSystem(ContactPoint.ContactPointSystem.PHONE).setValue(updatedPatient.getPhone());

        String[] addressList = updatedPatient.getAddress().split(", ");
        Address address = new Address();
        StringType stringType = new StringType();
        stringType.setValue(addressList[0]);
        address.setLine(List.of(stringType));
        address.setCity(addressList[1]);
        address.setPostalCode(addressList[2]);
        address.setCountry(addressList[3]);
        patient.setAddress(List.of(address));

        patient.setGender(Enumerations.AdministrativeGender.valueOf(updatedPatient.getGender()));
        patient.setBirthDate(updatedPatient.getBirthDate());
        CodeableConcept codeableConcept = new CodeableConcept();
        codeableConcept.getCodingFirstRep().setCode(updatedPatient.getMaritalStatus());
        patient.setMaritalStatus(codeableConcept);

        try {
            Attachment attachment = new Attachment();
            attachment.setData(updatedPatient.getPhoto());
            patient.setPhoto(List.of(attachment));
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        MethodOutcome methodOutcome = this.fhirClient.update().resource("Patient").withId(patient.getId()).execute();
        return (Patient) methodOutcome.getResource();
    }

    public FhirUserDto getUserFromToken(String token) {
        AppUser user = userService.getUserFromToken(token);
        String userRole = user.getRole().getName();
        String userId = user.getId().toString();
        if(userRole.equals("ROLE_PRACTITIONER")){
            return fhirMapper.map(this.getPractitioner(userId));
        }else if (userRole.equals("ROLE_PATIENT")){
            return fhirMapper.map(this.getPatient(userId));
        }
        return new FhirUserDto();
    }
}
