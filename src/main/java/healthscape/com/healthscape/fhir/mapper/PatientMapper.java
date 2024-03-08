package healthscape.com.healthscape.fhir.mapper;

import healthscape.com.healthscape.fhir.dtos.FhirUserDto;
import healthscape.com.healthscape.file.service.FileService;
import healthscape.com.healthscape.users.model.AppUser;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.codesystems.V3MaritalStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class PatientMapper {

    private final FileService fileService;

    public PatientMapper(FileService fileService) {
        this.fileService = fileService;
    }

    public FhirUserDto fhirPatientToFhirUserDto(Patient patient) {
        FhirUserDto fhirUserDto = new FhirUserDto();

        fhirUserDto.setIdentifier(patient.getIdentifier().get(0).getValue());

        fhirUserDto.setName(patient.getName().get(0).getGiven().get(0).getValue());
        fhirUserDto.setSurname(patient.getName().get(0).getFamily());
        fhirUserDto.setGender(patient.getGender().toString());
        if (!patient.getMaritalStatus().getCoding().isEmpty()) {
            fhirUserDto.setMaritalStatus(patient.getMaritalStatus().getCoding().get(0).getCode());
        } else {
            fhirUserDto.setMaritalStatus("NULL");
        }
        fhirUserDto.setBirthDate(patient.getBirthDate());
        if (!patient.getAddress().isEmpty()) {
            Address address = patient.getAddress().get(0);
            fhirUserDto.setAddress(address.getLine().get(0) + ", " + address.getCity() + ", " + address.getPostalCode() + ", " + address.getCountry());
        }
        try{
            fhirUserDto.setImage(fileService.getImage(patient.getPhoto().get(0).getUrl()));
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        for (ContactPoint point : patient.getTelecom()) {
            if (point.getSystem().equals(ContactPoint.ContactPointSystem.PHONE)) {
                fhirUserDto.setPhone(point.getValue());
            }
            if (point.getSystem().equals(ContactPoint.ContactPointSystem.EMAIL)) {
                fhirUserDto.setEmail(point.getValue());
            }
        }
        return fhirUserDto;
    }

    public Patient appUserToFhirPatient(AppUser appUser, String personalId) {
        Patient patient = new Patient();

        patient.setId(appUser.getId().toString());

        Identifier identifier = new Identifier();
        identifier.setSystem("http://hl7.org/fhir/sid/us-ssn");
        identifier.setUse(Identifier.IdentifierUse.OFFICIAL);
        identifier.setValue(personalId);
        patient.setIdentifier(List.of(identifier));

        patient.addName().addGiven(appUser.getName()).setFamily(appUser.getSurname());
        patient.addTelecom().setSystem(ContactPoint.ContactPointSystem.EMAIL).setValue(appUser.getEmail());
        patient.addTelecom().setSystem(ContactPoint.ContactPointSystem.PHONE).setValue("");

        patient.setGender(Enumerations.AdministrativeGender.UNKNOWN);
        CodeableConcept codeableConcept = new CodeableConcept();
        codeableConcept.getCodingFirstRep().setCode(V3MaritalStatus.NULL.toCode());
        patient.setMaritalStatus(codeableConcept);
        Address address = new Address();
        StringType stringType = new StringType();
        stringType.setValue(" ");
        address.setLine(List.of(stringType));
        address.setCity(" ");
        address.setPostalCode(" ");
        address.setCountry(" ");
        patient.setAddress(List.of(address));

        try {
            Attachment attachment = new Attachment();
            attachment.setData(fileService.getImage(appUser.getImagePath()));
            attachment.setUrl(appUser.getImagePath());
            patient.setPhoto(List.of(attachment));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return patient;
    }
}
