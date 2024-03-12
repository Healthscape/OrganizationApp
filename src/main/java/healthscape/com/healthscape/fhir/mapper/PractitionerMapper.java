package healthscape.com.healthscape.fhir.mapper;

import healthscape.com.healthscape.fhir.dtos.FhirUserDto;
import healthscape.com.healthscape.file.service.FileService;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.users.model.Specialty;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class PractitionerMapper {

    private final FileService fileService;

    public PractitionerMapper(FileService fileService) {
        this.fileService = fileService;
    }

    public Practitioner appUserToFhirPractitioner(AppUser appUser, Specialty specialty) {
        Practitioner practitioner = new Practitioner();

        practitioner.setId(UUID.randomUUID().toString());

        List<Identifier> identifiers = new ArrayList<>();
        Identifier identifier = new Identifier();
        identifier.setSystem("http://healthscape.com");
        identifier.setUse(Identifier.IdentifierUse.OFFICIAL);
        identifier.setValue(appUser.getId().toString());
        identifiers.add(identifier);
        practitioner.setIdentifier(identifiers);

        practitioner.addName().addGiven(appUser.getName()).setFamily(appUser.getSurname());
        practitioner.addTelecom().setSystem(ContactPoint.ContactPointSystem.EMAIL).setValue(appUser.getEmail());

        practitioner.setGender(Enumerations.AdministrativeGender.UNKNOWN);
        Address address = new Address();
        StringType stringType = new StringType();
        stringType.setValue(" ");
        address.setLine(List.of(stringType));
        address.setCity(" ");
        address.setPostalCode(" ");
        address.setCountry(" ");
        practitioner.setAddress(List.of(address));

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

        fhirUserDto.setIdentifier(user.getIdentifier().get(0).getValue());

        fhirUserDto.setName(user.getName().get(0).getGiven().get(0).getValue());
        fhirUserDto.setSurname(user.getName().get(0).getFamily());
        fhirUserDto.setGender(user.getGender().toString());
        try{
            fhirUserDto.setImage(fileService.getImage(user.getPhoto().get(0).getUrl()));
            fhirUserDto.setImagePath(user.getPhoto().get(0).getUrl());
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        for (ContactPoint point : user.getTelecom()) {
            if (point.getSystem().equals(ContactPoint.ContactPointSystem.PHONE)) {
                fhirUserDto.setPhone(point.getValue());
            }
        }
        return fhirUserDto;
    }
}
