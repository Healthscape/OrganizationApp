package healthscape.com.healthscape.patient_records.mapper;

import healthscape.com.healthscape.encounter.mapper.EncounterMapper;
import healthscape.com.healthscape.fhir.dtos.FhirUserDto;
import healthscape.com.healthscape.fhir.mapper.FhirMapper;
import healthscape.com.healthscape.patient_records.dtos.*;
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

    private final FhirMapper fhirMapper;
    private final EncryptionConfig encryptionConfig;
    private final EncounterMapper encounterMapper;

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

    public PatientRecordDto mapToPatientRecord(Resource resource) {
        PatientRecordDto patientRecordDto = new PatientRecordDto();
            switch (resource.getResourceType()) {
                case Patient -> {
                    FhirUserDto userDto = this.fhirMapper.map((Patient) resource);
                    patientRecordDto.setUserDto(userDto);
                }
                case Encounter -> {
                    EncounterDto encounterDto = this.encounterMapper.mapToEncounterDto((Encounter) resource);
                    patientRecordDto.getEncounters().add(encounterDto);
                }
                case MedicationAdministration -> {
                    MedicationAdministrationDto medicationAdministrationDto = this.encounterMapper.mapToMedicationAdministrationDto((MedicationAdministration) resource);
                    patientRecordDto.getMedications().add(medicationAdministrationDto);
                }
                case ClinicalImpression -> {
                    ClinicalImpressionDto clinicalImpressionDto = this.encounterMapper.mapToClinicalImpressionDto((ClinicalImpression) resource);
                    patientRecordDto.getClinicalImpressions().add(clinicalImpressionDto);
                }
                case Condition -> {
                    ConditionDto conditionDto = this.encounterMapper.mapToConditionDto((Condition) resource);
                    patientRecordDto.getConditions().add(conditionDto);
                }
                case DocumentReference -> {
                    DocumentReferenceDto documentRefDto = this.encounterMapper.mapToDocumentReferenceDto((DocumentReference) resource);
                    patientRecordDto.getDocumentReferences().add(documentRefDto);
                }
                case AllergyIntolerance -> {
                    AllergyDto allergyDto = this.encounterMapper.mapToAllergyDto((AllergyIntolerance) resource);
                    patientRecordDto.getAllergies().add(allergyDto);
                }
                default -> {
                }
        }
        return patientRecordDto;
    }
}
