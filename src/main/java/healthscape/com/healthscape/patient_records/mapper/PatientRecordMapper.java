package healthscape.com.healthscape.patient_records.mapper;

import healthscape.com.healthscape.fhir.dtos.FhirUserDto;
import healthscape.com.healthscape.fhir.mapper.FhirEncounterMapper;
import healthscape.com.healthscape.fhir.mapper.FhirUserMapper;
import healthscape.com.healthscape.patient_records.dtos.*;
import healthscape.com.healthscape.patient_records.model.PatientRecord;
import healthscape.com.healthscape.util.Config;
import healthscape.com.healthscape.util.EncryptionConfig;
import lombok.AllArgsConstructor;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

@Component
@AllArgsConstructor
public class PatientRecordMapper {

    private final FhirUserMapper fhirMapper;
    private final EncryptionConfig encryptionConfig;
    private final FhirEncounterMapper encounterMapper;

    public PatientRecordPreview mapToPreview(Patient patient) {
        String id = "";
        for (Identifier identifier : patient.getIdentifier()) {
            if (identifier.getSystem().equals(Config.HEALTHSCAPE_URL)) {
                id = identifier.getValue();
                break;
            }
        }
        String personalId = encryptionConfig.defaultEncryptionUtil().decrypt(patient.getIdentifier().get(0).getValue());
        String name = patient.getName().get(0).getGiven().get(0).getValue();
        String surname = patient.getName().get(0).getFamily();
        Date birthDate = patient.getBirthDate();
        String photo = Base64.getEncoder().encodeToString(patient.getPhoto().get(0).getData());
        return new PatientRecordPreview(name, surname, personalId, birthDate, photo, id);
    }

    public PatientRecordDto mapToPatientRecord(PatientRecord patientRecord) {
        PatientRecordDto patientRecordDto = new PatientRecordDto();

        FhirUserDto userDto = this.fhirMapper.map(patientRecord.getPatient());
        patientRecordDto.setUserDto(userDto);
        
        for(Encounter encounter: patientRecord.getEncounters()){
            EncounterDto encounterDto = this.encounterMapper.mapToEncounterDto(encounter);
            patientRecordDto.getEncounters().add(encounterDto);
        }
        
        for(MedicationAdministration medicationAdministration: patientRecord.getMedications()){
            MedicationAdministrationDto medicationAdministrationDto = this.encounterMapper.mapToMedicationAdministrationDto( medicationAdministration);
            patientRecordDto.getMedications().add(medicationAdministrationDto);
        }
        
        for(ClinicalImpression clinicalImpression: patientRecord.getImpressions()){
            ClinicalImpressionDto clinicalImpressionDto = this.encounterMapper.mapToClinicalImpressionDto( clinicalImpression);
            patientRecordDto.getClinicalImpressions().add(clinicalImpressionDto);
        }
        
        for(Condition condition: patientRecord.getConditions()){
            ConditionDto conditionDto = this.encounterMapper.mapToConditionDto(condition);
            patientRecordDto.getConditions().add(conditionDto);
        }
        
        for(DocumentReference documentReference: patientRecord.getDocumentReferences()){
            DocumentReferenceDto documentRefDto = this.encounterMapper.mapToDocumentReferenceDto(documentReference);
            patientRecordDto.getDocumentReferences().add(documentRefDto);
        }
        
        for(AllergyIntolerance allergyIntolerance: patientRecord.getAllergies()){
            AllergyDto allergyDto = this.encounterMapper.mapToAllergyDto(allergyIntolerance);
            patientRecordDto.getAllergies().add(allergyDto);
        }
        
        return patientRecordDto;
    }
}
