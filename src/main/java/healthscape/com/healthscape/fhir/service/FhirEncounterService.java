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
import java.util.ArrayList;

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
        List<ClinicalImpression.ClinicalImpressionFindingComponent> conditions = saveConditions(encounterRef, newEncounterDTO.getConditions(), oldConditions);
        ClinicalImpression clinicalImpression = this.encounterMapper.mapToClinicalImpression(encounterRef, newEncounterDTO);
        if(conditions!=null) {
            clinicalImpression.setFinding(conditions);
        }
        return clinicalImpression;
    }


    private List<ClinicalImpression.ClinicalImpressionFindingComponent> saveConditions(Reference encounterRef, List<NewConditionDto> newConditions, List<Condition> oldConditions) {
        List<ClinicalImpression.ClinicalImpressionFindingComponent> findingComponents = new ArrayList<>();
        if(newConditions == null){
            return null;
        }
        for (NewConditionDto newConditionDto : newConditions) {
            if (newConditionDto.getId() != null) {
                oldConditions = this.encounterMapper.mapOldToNewCondition(oldConditions, newConditionDto);
            } else {
                Condition condition = this.encounterMapper.mapToCondition(encounterRef, newConditionDto.getText());
                Reference conditionRef = new Reference(condition);
                conditionRef.setReference("Condition/" + condition.getIdElement().getIdPart());
                conditionRef.setDisplay(newConditionDto.getText());
                ClinicalImpression.ClinicalImpressionFindingComponent clinicalImpressionFindingComponent = new ClinicalImpression.ClinicalImpressionFindingComponent();
                clinicalImpressionFindingComponent.setItemReference(conditionRef);
                findingComponents.add(clinicalImpressionFindingComponent);
            }
        }
        return findingComponents;
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

    // public List<ConditionDto> getConditionHistory(String patientId) {
    //     Bundle response = this.fhirClient.search()
    //             .forResource(Condition.class)
    //             .where(Patient.IDENTIFIER.exactly().systemAndValues(Config.HEALTHSCAPE_URL, patientId))
    //             .returnBundle(Bundle.class)
    //             .execute();
    //     List<ConditionDto> conditionDtos = new ArrayList<>();
    //     for (Bundle.BundleEntryComponent entry : response.getEntry()) {
    //         if (entry.getResource() instanceof Condition condition) {
    //             ConditionDto conditionDto = this.encounterMapper.mapToConditionDto(condition);
    //             conditionDtos.add(conditionDto);
    //         }
    //     }
    //     return conditionDtos;
    // }

    // public List<AllergyDto> geAllergyHistory(String patientId) {
    //     Bundle response = this.fhirClient.search()
    //             .forResource(AllergyIntolerance.class)
    //             .where(Patient.IDENTIFIER.exactly().systemAndValues(Config.HEALTHSCAPE_URL, patientId))
    //             .returnBundle(Bundle.class)
    //             .execute();
    //     List<AllergyDto> allergyDtos = new ArrayList<>();
    //     for (Bundle.BundleEntryComponent entry : response.getEntry()) {
    //         if (entry.getResource() instanceof AllergyIntolerance allergy) {
    //             AllergyDto allergyDto = this.encounterMapper.mapToAllergyDto(allergy);
    //             allergyDtos.add(allergyDto);
    //         }
    //     }
    //     return allergyDtos;
    // }
}
