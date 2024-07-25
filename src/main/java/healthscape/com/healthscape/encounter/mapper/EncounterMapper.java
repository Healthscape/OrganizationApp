package healthscape.com.healthscape.encounter.mapper;

import healthscape.com.healthscape.encounter.dto.*;
import healthscape.com.healthscape.patientRecords.dtos.*;
import lombok.AllArgsConstructor;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.AllergyIntolerance.AllergyIntoleranceCriticality;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
@AllArgsConstructor
public class EncounterMapper {

    public Encounter mapToEncounter(Patient patient, Practitioner practitioner) {
        Encounter encounter = new Encounter();
        encounter.setStatus(Encounter.EncounterStatus.INPROGRESS);
        Reference patientRef = new Reference(patient);
        patientRef = patientRef.setReference("Patient/" + patient.getIdElement().getIdPart());
        patientRef = patientRef.setDisplay(patient.getName().get(0).getGivenAsSingleString() + " " + patient.getName().get(0).getFamily());
        encounter.setSubject(patientRef);
        List<Encounter.EncounterParticipantComponent> participant = new ArrayList<>();
        Encounter.EncounterParticipantComponent encounterParticipantComponent = new Encounter.EncounterParticipantComponent();
        Reference practitionerRef = new Reference(practitioner);
        practitionerRef.setReference("Practitioner/" + practitioner.getIdElement().getIdPart());
        String display =  practitioner.getQualification().get(0).getCode().getCoding().get(0).getDisplay() + " - "+ practitioner.getName().get(0).getGivenAsSingleString() + " "+ practitioner.getName().get(0).getFamily();
        practitionerRef.setDisplay(display);
        encounterParticipantComponent.setIndividual(practitionerRef);
        participant.add(encounterParticipantComponent);
        encounter.setParticipant(participant);
        encounter.setPeriod(new Period().setStart(new Date()));
        return encounter;
    }

    public EncounterDto mapToEncounterDto(Encounter encounter) {
        EncounterDto encounterDto = new EncounterDto();
        encounterDto.setId(encounter.getIdElement().getIdPart());
        encounterDto.setStatus(encounter.getStatus().toCode());
        encounterDto.setPatient(encounter.getSubject().getDisplay());
        String display = encounter.getParticipant().get(0).getIndividual().getDisplay();
        String[] displayStrs = display.split(" - ");
        encounterDto.setSpecialty(displayStrs[0]);
        encounterDto.setPractitioner(displayStrs[1]);
        encounterDto.setStart(encounter.getPeriod().getStart());
        encounterDto.setEnd(encounter.getPeriod().getEnd());
        return encounterDto;
    }

    public ClinicalImpression mapToClinicalImpression(Reference encounterRef, Encounter encounter, PatientRecordUpdateDto patientRecordUpdateDto) {
        ClinicalImpression clinicalImpression = new ClinicalImpression();
        clinicalImpression.setEncounter(encounterRef);
        clinicalImpression.setSubject(encounter.getSubject());
        clinicalImpression.setDate(patientRecordUpdateDto.getDate());
        clinicalImpression.setStatus(ClinicalImpression.ClinicalImpressionStatus.COMPLETED);
        clinicalImpression.setAssessor(encounter.getParticipant().get(0).getIndividual());
        clinicalImpression.setDescription(patientRecordUpdateDto.getClinicalImpressionDescription());
        clinicalImpression.setSummary(patientRecordUpdateDto.getClinicalImpressionSummary());
        return clinicalImpression;
    }

    public ClinicalImpressionDto mapToClinicalImpressionDto(ClinicalImpression resource) {
        ClinicalImpressionDto clinicalImpressionDto = new ClinicalImpressionDto();
        clinicalImpressionDto.setId(resource.getIdElement().getIdPart());
        clinicalImpressionDto.setEncounterId(resource.getEncounter().getReference());
        clinicalImpressionDto.setPatient(resource.getSubject().getDisplay());
        clinicalImpressionDto.setDate(resource.getDate());
        clinicalImpressionDto.setStatus(resource.getStatus().toCode());
        clinicalImpressionDto.setPractitioner(resource.getAssessor().getDisplay());
        clinicalImpressionDto.setDescription(resource.getDescription());
        clinicalImpressionDto.setSummary(resource.getSummary());
        return clinicalImpressionDto;
    }

    public MedicationAdministration mapToMedicationAdministration(Reference encounterRef, Encounter encounter, NewMedicationDto newMedicationDto, PatientRecordUpdateDto patientRecordUpdateDto, Reference medicationRef) {
        MedicationAdministration medicationAdministration = new MedicationAdministration();
        medicationAdministration.setMedication(medicationRef);
        medicationAdministration.setContext(encounterRef);
        medicationAdministration.setSubject(encounter.getSubject());
        medicationAdministration.setStatus(MedicationAdministration.MedicationAdministrationStatus.INPROGRESS);
        MedicationAdministration.MedicationAdministrationDosageComponent dosageComponent = new MedicationAdministration.MedicationAdministrationDosageComponent();
        dosageComponent.setText(newMedicationDto.getDosage());
        medicationAdministration.setDosage(dosageComponent);
        medicationAdministration.setEffective(new Period().setStart(patientRecordUpdateDto.getDate()));
        return medicationAdministration;
    }

    public MedicationAdministrationDto mapToMedicationAdministrationDto(MedicationAdministration resource) {
        MedicationAdministrationDto medicationAdministrationDto = new MedicationAdministrationDto();
        medicationAdministrationDto.setId(resource.getIdElement().getIdPart());
        medicationAdministrationDto.setEncounterId(resource.getContext().getReference());
        medicationAdministrationDto.setPatient(resource.getSubject().getDisplay());
        medicationAdministrationDto.setDosage(resource.getDosage().getText());
        medicationAdministrationDto.setStart(resource.getEffectivePeriod().getStart());
        medicationAdministrationDto.setEnd(resource.getEffectivePeriod().getEnd());
        medicationAdministrationDto.setStatus(resource.getStatus().toCode());
        medicationAdministrationDto.setMedication(resource.getMedicationReference().getDisplay());
        return medicationAdministrationDto;
    }

    public Medication mapToMedication(NewMedicationDto newMedicationDto) {
        Medication medication = new Medication();
        medication.setCode(new CodeableConcept().setText(newMedicationDto.getMedication()));
        return medication;
    }

    public DocumentReference mapToDocumentReference(Reference encounterRef, Encounter encounter, PatientRecordUpdateDto patientRecordUpdateDto, NewDocumentReferenceDto newDocumentReferenceDto) {
        DocumentReference documentReference = new DocumentReference();
        documentReference.setDate(patientRecordUpdateDto.getDate());
        documentReference.setSubject(encounter.getSubject());
        documentReference.setAuthenticator(encounter.getParticipant().get(0).getIndividual());
        documentReference.setStatus(Enumerations.DocumentReferenceStatus.CURRENT);
        List<DocumentReference.DocumentReferenceContentComponent> contentComponents = new ArrayList<>();
        DocumentReference.DocumentReferenceContentComponent contentComponent = new DocumentReference.DocumentReferenceContentComponent();
        Attachment attachment = new Attachment();
        byte[] imageBytes = Base64.getDecoder().decode(newDocumentReferenceDto.getData().split(",")[1]);
        attachment.setData(imageBytes);
        attachment.setCreation(patientRecordUpdateDto.getDate());
        attachment.setContentType(newDocumentReferenceDto.getContentType());
        attachment.setTitle(newDocumentReferenceDto.getTitle());
        contentComponent.setAttachment(attachment);
        contentComponents.add(contentComponent);
        documentReference.setContent(contentComponents);
        DocumentReference.DocumentReferenceContextComponent contextComponent = new DocumentReference.DocumentReferenceContextComponent();
        List<Reference> references = new ArrayList<>();
        references.add(encounterRef);
        contextComponent.setEncounter(references);
        documentReference.setContext(contextComponent);
        return documentReference;
    }

    public DocumentReferenceDto mapToDocumentReferenceDto(DocumentReference resource) {
        DocumentReferenceDto documentReferenceDto = new DocumentReferenceDto();
        documentReferenceDto.setId(resource.getIdElement().getIdPart());
        documentReferenceDto.setEncounterId(resource.getContext().getEncounter().get(0).getReference());
        documentReferenceDto.setDate(resource.getDate());
        String display = resource.getAuthenticator().getDisplay();
        String[] displayStrs = display.split(" - ");
        documentReferenceDto.setSpecialty(displayStrs[0]);
        documentReferenceDto.setPractitioner(displayStrs[1]);
        documentReferenceDto.setPatient(resource.getSubject().getDisplay());
        String base64Image = Base64.getEncoder().encodeToString(resource.getContent().get(0).getAttachment().getData());
        documentReferenceDto.setData(base64Image);
        documentReferenceDto.setContentType(resource.getContent().get(0).getAttachment().getContentType());
        documentReferenceDto.setTitle(resource.getContent().get(0).getAttachment().getTitle());
        return documentReferenceDto;
    }


    public Condition mapToCondition(Reference encounterRef, Encounter encounter, PatientRecordUpdateDto patientRecordUpdateDto, NewConditionDto newConditionDto) {
        Condition condition = new Condition();
        condition.setEncounter(encounterRef);
        condition.setSubject(encounter.getSubject());
        condition.setAsserter(encounter.getParticipant().get(0).getIndividual());
        condition.setRecordedDate(patientRecordUpdateDto.getDate());
        CodeableConcept codeableConceptCode = new CodeableConcept();
        codeableConceptCode.setText(newConditionDto.getText());
        condition.setCode(codeableConceptCode);
        CodeableConcept codeableConceptStatus = new CodeableConcept();
        codeableConceptStatus.setText("ACTIVE");
        condition.setClinicalStatus(codeableConceptStatus);
        condition.setOnset(new Period().setStart(patientRecordUpdateDto.getDate()));
        return condition;
    }

    public ConditionDto mapToConditionDto(Condition resource) {
        ConditionDto conditionDto = new ConditionDto();
        conditionDto.setId(resource.getIdElement().getIdPart());
        conditionDto.setEncounterId(resource.getEncounter().getReference());
        conditionDto.setPatient(resource.getSubject().getDisplay());
        conditionDto.setPractitioner(resource.getAsserter().getDisplay());
        conditionDto.setDate(resource.getRecordedDate());
        conditionDto.setCode(resource.getCode().getText());
        conditionDto.setStatus(resource.getClinicalStatus().getText());
        conditionDto.setStart(resource.getOnsetPeriod().getStart());
        conditionDto.setEnd(resource.getAbatementPeriod().getStart());
        return conditionDto;
    }

    public AllergyIntolerance mapToAllergyIntolerance(Reference encounterRef, Encounter encounter, NewAllergyDto newAllergyDto, PatientRecordUpdateDto patientRecordUpdateDto) {
        AllergyIntolerance allergyIntolerance = new AllergyIntolerance();
        allergyIntolerance.setPatient(encounter.getSubject());
        allergyIntolerance.setEncounter(encounterRef);
        allergyIntolerance.setRecordedDate(patientRecordUpdateDto.getDate());
        allergyIntolerance.setAsserter(encounter.getParticipant().get(0).getIndividual());
        CodeableConcept codeableConcept = new CodeableConcept();
        codeableConcept.setText(newAllergyDto.getCode());
        allergyIntolerance.setCode(codeableConcept);
        List<Enumeration<AllergyIntolerance.AllergyIntoleranceCategory>> categories = new ArrayList<>();
        Enumeration<AllergyIntolerance.AllergyIntoleranceCategory> category = new Enumeration<>(new AllergyIntolerance.AllergyIntoleranceCategoryEnumFactory());
        category.setValue(AllergyIntolerance.AllergyIntoleranceCategory.fromCode(newAllergyDto.getCategory().toLowerCase()));
        categories.add(category);
        allergyIntolerance.setCategory(categories);
        allergyIntolerance.setCriticality(AllergyIntoleranceCriticality.fromCode(newAllergyDto.getCriticality().toLowerCase()));
        CodeableConcept clinicalStatus = new CodeableConcept();
        clinicalStatus.setText("ACTIVE");
        allergyIntolerance.setClinicalStatus(clinicalStatus);
        allergyIntolerance.setOnset(new Period().setStart(patientRecordUpdateDto.getDate()));
        return allergyIntolerance;
    }

    public AllergyDto mapToAllergyDto(AllergyIntolerance resource) {
        AllergyDto allergyDto = new AllergyDto();
        allergyDto.setId(resource.getIdElement().getIdPart());
        allergyDto.setEncounterId(resource.getEncounter().getReference());
        allergyDto.setPatient(resource.getPatient().getDisplay());
        allergyDto.setPractitioner(resource.getAsserter().getDisplay());
        allergyDto.setDate(resource.getRecordedDate());
        allergyDto.setCode(resource.getCode().getText());
        allergyDto.setStatus(resource.getClinicalStatus().getText());
        allergyDto.setStart(resource.getOnsetPeriod().getStart());
        allergyDto.setEnd(resource.getOnsetPeriod().getEnd());
        return allergyDto;
    }
}