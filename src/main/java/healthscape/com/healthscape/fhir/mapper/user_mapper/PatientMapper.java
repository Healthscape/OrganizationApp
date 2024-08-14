package healthscape.com.healthscape.fhir.mapper.user_mapper;

import healthscape.com.healthscape.fhir.dtos.FhirUserDto;
import healthscape.com.healthscape.file.service.FileService;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.util.EncryptionConfig;
import lombok.AllArgsConstructor;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.codesystems.V3MaritalStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class PatientMapper {

    private final FileService fileService;
    private final EncryptionConfig encryptionConfig;

    public FhirUserDto fhirPatientToFhirUserDto(Patient patient) {
        FhirUserDto fhirUserDto = new FhirUserDto();

        fhirUserDto.setIdentifier(this.encryptionConfig.defaultEncryptionUtil().decryptIfNotAlready(patient.getIdentifier().get(0).getValue()));

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
        try {
            fhirUserDto.setImage(patient.getPhoto().get(0).getData());
        } catch (Exception e) {
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

        patient.setId(UUID.randomUUID().toString());

        List<Identifier> identifiers = new ArrayList<>();
        Identifier identifier = new Identifier();
        identifier.setSystem("http://hl7.org/fhir/sid/us-ssn");
        identifier.setUse(Identifier.IdentifierUse.OFFICIAL);
        identifier.setValue(personalId);
        identifiers.add(identifier);
        patient.setIdentifier(identifiers);

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

    public Patient mapUpdatedToPatient(Patient patient, FhirUserDto updatedPatient) {
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

        if (updatedPatient.getImage().length != 0) {
            try {
                Attachment attachment = new Attachment();
                attachment.setData(updatedPatient.getImage());
                patient.setPhoto(List.of(attachment));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return patient;
    }
}
