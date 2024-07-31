package healthscape.com.healthscape.fhir.mapper;

import healthscape.com.healthscape.fhir.dtos.FhirUserDto;
import healthscape.com.healthscape.file.service.FileService;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.users.model.Specialty;
import healthscape.com.healthscape.users.service.SpecialtyService;
import healthscape.com.healthscape.util.Config;
import healthscape.com.healthscape.util.EncryptionConfig;
import lombok.AllArgsConstructor;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class PractitionerMapper {

    private final FileService fileService;
    private final EncryptionConfig encryptionConfig;
    private final SpecialtyService specialtyService;

    public Practitioner appUserToFhirPractitioner(AppUser appUser, Specialty specialty) {
        Practitioner practitioner = new Practitioner();

        practitioner.setId(UUID.randomUUID().toString());

        List<Identifier> identifiers = new ArrayList<>();
        Identifier identifier = new Identifier();
        identifier.setSystem(Config.HEALTHSCAPE_URL);
        identifier.setUse(Identifier.IdentifierUse.OFFICIAL);
        identifier.setValue(this.encryptionConfig.defaultEncryptionUtil().encryptIfNotAlready(appUser.getId().toString()));
        identifiers.add(identifier);
        practitioner.setIdentifier(identifiers);

        practitioner.addName().addGiven(appUser.getName()).setFamily(appUser.getSurname());
        practitioner.addTelecom().setSystem(ContactPoint.ContactPointSystem.EMAIL).setValue(appUser.getEmail());

        practitioner.setGender(Enumerations.AdministrativeGender.UNKNOWN);

        try {
            Attachment attachment = new Attachment();
            attachment.setData(fileService.getImage(appUser.getImagePath()));
            attachment.setUrl(appUser.getImagePath());
            practitioner.setPhoto(List.of(attachment));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


        Coding coding = new Coding();
        coding.setSystem("http://snomed.info/sct");
        coding.setCode(specialty.getCode());
        coding.setDisplay(specialty.getName());

        practitioner.setQualification(List.of(new Practitioner.PractitionerQualificationComponent(new CodeableConcept(coding))));
        return practitioner;
    }

    public FhirUserDto fhirPractitionerToFhirUserDto(Practitioner user) {
        FhirUserDto fhirUserDto = new FhirUserDto();

        fhirUserDto.setName(user.getName().get(0).getGiven().get(0).getValue());
        fhirUserDto.setSurname(user.getName().get(0).getFamily());
        fhirUserDto.setGender(user.getGender().toString());
        fhirUserDto.setBirthDate(user.getBirthDate());
        CodeableConcept code = user.getQualification().get(0).getCode();
        String codeName = code.getCoding().get(0).getCode();
        fhirUserDto.setSpecialty(this.specialtyService.getByCode(codeName).getName());
        try {
            fhirUserDto.setImage(user.getPhoto().get(0).getData());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        for (ContactPoint point : user.getTelecom()) {
            if (point.getSystem().equals(ContactPoint.ContactPointSystem.PHONE)) {
                fhirUserDto.setPhone(point.getValue());
            }
            if (point.getSystem().equals(ContactPoint.ContactPointSystem.EMAIL)) {
                fhirUserDto.setEmail(point.getValue());
            }
        }
        return fhirUserDto;
    }

    public Practitioner mapUpdatedToPractitioner(Practitioner practitioner, FhirUserDto updatedPractitioner) {
        practitioner.getName().remove(0);
        practitioner.addName().addGiven(updatedPractitioner.getName()).setFamily(updatedPractitioner.getSurname());
        practitioner.addTelecom().setSystem(ContactPoint.ContactPointSystem.PHONE).setValue(updatedPractitioner.getPhone());

        if (updatedPractitioner.getGender() != null) {
            practitioner.setGender(Enumerations.AdministrativeGender.valueOf(updatedPractitioner.getGender()));
        }
        practitioner.setBirthDate(updatedPractitioner.getBirthDate());

        if (updatedPractitioner.getImage().length != 0) {
            try {
                Attachment attachment = new Attachment();
                attachment.setData(updatedPractitioner.getImage());
                practitioner.setPhoto(List.of(attachment));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        Specialty specialty = specialtyService.getByCode(updatedPractitioner.getSpecialty());
        Coding coding = new Coding();
        coding.setSystem("http://snomed.info/sct");
        coding.setCode(specialty.getCode());
        coding.setDisplay(specialty.getName());

        practitioner.setQualification(List.of(new Practitioner.PractitionerQualificationComponent(new CodeableConcept(coding))));
        return practitioner;
    }
}
