package healthscape.com.healthscape.fhir.service;

import org.hl7.fhir.r4.model.AllergyIntolerance;
import org.hl7.fhir.r4.model.ClinicalImpression;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Medication;
import org.hl7.fhir.r4.model.MedicationAdministration;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Reference;
import org.springframework.stereotype.Service;

import healthscape.com.healthscape.encounter.dto.NewAllergyDto;
import healthscape.com.healthscape.encounter.dto.NewConditionDto;
import healthscape.com.healthscape.encounter.dto.NewDocumentReferenceDto;
import healthscape.com.healthscape.encounter.dto.NewEncounterDTO;
import healthscape.com.healthscape.encounter.dto.NewMedicationDto;
import healthscape.com.healthscape.fhir.mapper.FhirEncounterMapper;
import healthscape.com.healthscape.patient_records.model.PatientRecord;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Service
public class FhirEncounterService {
    
    private final FhirEncounterMapper encounterMapper;


    public PatientRecord updatePatientRecordWithEncounter(PatientRecord oldPatientRecord, NewEncounterDTO newEncounterDto, Practitioner practitioner){

        // Encounter
        Encounter newEncounter = encounterMapper.mapToEncounter(oldPatientRecord.getPatient(), practitioner, newEncounterDto.getDate());
        oldPatientRecord.encounters.add(newEncounter);
        Reference encounterReference = new Reference(newEncounter);

        // Medication
        List<MedicationAdministration> newMedicationList = saveMedications(encounterReference, newEncounterDto.getMedications(), oldPatientRecord.getMedications());
        oldPatientRecord.medications = newMedicationList;

        // Conditions
        List<Condition> newConditions = saveConditions(encounterReference, newEncounterDto.getConditions(), oldPatientRecord.getConditions());
        oldPatientRecord.conditions = newConditions;

        // Impression
        ClinicalImpression newImpression = saveClinicalImpression(encounterReference, newEncounterDto, oldPatientRecord.getConditions());
        oldPatientRecord.impressions.add(newImpression);

        // Allergies
        List<AllergyIntolerance> newAllergyList = saveAllergies(encounterReference, newEncounterDto.getAllergies(), oldPatientRecord.getAllergies());
        oldPatientRecord.allergies = newAllergyList;

        // Documents
        List<DocumentReference> newDocumentList = saveDocuments(encounterReference, newEncounterDto.getDocuments(), oldPatientRecord.getDocumentReferences());
        oldPatientRecord.documentReferences = newDocumentList;

        return oldPatientRecord;
    }

    

    private List<MedicationAdministration> saveMedications(Reference encounterRef, List<NewMedicationDto> medicationDtos, List<MedicationAdministration> oldMedications) {
        if(medicationDtos == null){
            return oldMedications;
        }
        for (NewMedicationDto newMedicationDto : medicationDtos) {
            if (newMedicationDto.getId() != null) {
                oldMedications = this.encounterMapper.mapOldToNewMedicationAdministration(oldMedications, newMedicationDto);
            } else {
                Medication medication = this.encounterMapper.mapToMedication(newMedicationDto.getMedication());
                Reference medicationRef = new Reference(medication);
                medicationRef.setReference("Medication/" + medication.getId());
                medicationRef.setDisplay(newMedicationDto.getMedication());
        
                MedicationAdministration medicationAdministration = this.encounterMapper.mapToMedicationAdministration(((Encounter)encounterRef.getResource()).getSubject(), newMedicationDto.getDosage());
                medicationAdministration.setMedication(medicationRef);
                medicationAdministration.setContext(encounterRef);
                oldMedications.add(medicationAdministration);
            }
        }

        return oldMedications;
    }

    private ClinicalImpression saveClinicalImpression(Reference encounterRef, NewEncounterDTO newEncounterDTO, List<Condition> oldConditions){
        ClinicalImpression clinicalImpression = this.encounterMapper.mapToClinicalImpression(encounterRef, newEncounterDTO);
        return clinicalImpression;
    }


    private List<Condition> saveConditions(Reference encounterRef, List<NewConditionDto> newConditions, List<Condition> oldConditions) {
        if(newConditions == null){
            return null;
        }
        for (NewConditionDto newConditionDto : newConditions) {
            if (newConditionDto.getId() != null) {
                oldConditions = this.encounterMapper.mapOldToNewCondition(oldConditions, newConditionDto);
            } else {
                Condition condition = this.encounterMapper.mapToCondition(encounterRef, newConditionDto.getCode());
                oldConditions.add(condition);
            }
        }
        return oldConditions;
    }

    private List<AllergyIntolerance> saveAllergies(Reference encounterRef, List<NewAllergyDto> newAllergyIntolerances, List<AllergyIntolerance> oldAllergyIntolerances) {
        if(newAllergyIntolerances==null){
            return oldAllergyIntolerances;
        }
        for (NewAllergyDto newAllergyDto : newAllergyIntolerances) {
            if (newAllergyDto.getId() != null) {
                oldAllergyIntolerances = this.encounterMapper.mapOldToNewAllergies(oldAllergyIntolerances, newAllergyDto);
            } else {
                oldAllergyIntolerances.add(this.encounterMapper.mapToAllergyIntolerance(encounterRef, newAllergyDto));
            }
        }
        return oldAllergyIntolerances;
    }

    private List<DocumentReference> saveDocuments(Reference encounterRef, List<NewDocumentReferenceDto> newDocuments, List<DocumentReference> oldDocuments) {
        if(newDocuments==null){
            return oldDocuments;
        }
        for (NewDocumentReferenceDto newDocumentReferenceDto : newDocuments) {
            oldDocuments.add(this.encounterMapper.mapToDocumentReference(encounterRef, newDocumentReferenceDto));
        }
        return oldDocuments;
    }

}
