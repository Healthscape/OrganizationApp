package healthscape.com.healthscape.fhir.mapper;

import healthscape.com.healthscape.fhir.dtos.FhirUserDto;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.users.model.Specialty;
import org.apache.commons.compress.utils.IOUtils;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Component
public class PractitionerMapper {

    public Practitioner appUserToFhirPractitioner(AppUser appUser, Specialty specialty) {
        Practitioner practitioner = new Practitioner();

        practitioner.setId(appUser.getId().toString());

        practitioner.addName().addGiven(appUser.getName()).setFamily(appUser.getSurname());
        practitioner.addTelecom().setSystem(ContactPoint.ContactPointSystem.EMAIL).setValue(appUser.getEmail());

        try {
            InputStream in = getClass()
                    .getResourceAsStream("/images/practitioner-default.png");
            byte[] byteArray = IOUtils.toByteArray(in);
            in.close();
            Attachment attachment = new Attachment();
            attachment.setData(byteArray);
            practitioner.setPhoto(List.of(attachment));
        }catch (Exception e){
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
        fhirUserDto.setPhoto(Arrays.toString(user.getPhoto().get(0).getData()));
        for(ContactPoint point: user.getTelecom()){
            if(point.getSystem().equals(ContactPoint.ContactPointSystem.PHONE)) {
                fhirUserDto.setPhone(point.getValue());
            }
        }
        return fhirUserDto;
    }
}
