package healthscape.com.healthscape.patient_records.mapper;

import healthscape.com.healthscape.fhir.dtos.FhirUserDto;
import healthscape.com.healthscape.fhir.mapper.FhirEncounterMapper;
import healthscape.com.healthscape.fhir.mapper.FhirUserMapper;
import healthscape.com.healthscape.patient_records.dtos.*;
import healthscape.com.healthscape.patient_records.model.PatientRecord;
import lombok.AllArgsConstructor;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PatientRecordMapper {

    private final FhirUserMapper fhirMapper;
    private final FhirEncounterMapper encounterMapper;

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
