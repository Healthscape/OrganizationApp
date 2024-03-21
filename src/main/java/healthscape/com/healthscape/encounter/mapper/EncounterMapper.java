package healthscape.com.healthscape.encounter.mapper;

import healthscape.com.healthscape.encounter.dto.NewConditionDto;
import healthscape.com.healthscape.encounter.dto.NewDocumentReferenceDto;
import healthscape.com.healthscape.encounter.dto.NewMedicationDto;
import healthscape.com.healthscape.encounter.dto.PatientRecordUpdateDto;
import healthscape.com.healthscape.patientRecords.dtos.*;
import lombok.AllArgsConstructor;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
        practitionerRef.setDisplay(practitioner.getName().get(0).getGivenAsSingleString() + " "+ practitioner.getName().get(0).getFamily());
        encounterParticipantComponent.setIndividual(practitionerRef);
        participant.add(encounterParticipantComponent);
        encounter.setParticipant(participant);
        encounter.setPeriod(new Period().setStart(new Date()));
        return encounter;
    }

    public EncounterDto mapToEncounterDto(Encounter encounter) {
        EncounterDto encounterDto = new EncounterDto();
        encounterDto.setId(encounter.getIdElement().getIdPart());
        encounterDto.setStatus(encounter.getStatus().toString());
        encounterDto.setPatient(encounter.getSubject().getDisplay());
        encounterDto.setPractitioner(encounter.getParticipant().get(0).getIndividual().getDisplay());
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
        clinicalImpressionDto.setEncounterId(resource.getEncounter().getReference());
        clinicalImpressionDto.setPatient(resource.getSubject().getDisplay());
        clinicalImpressionDto.setDate(resource.getDate());
        clinicalImpressionDto.setStatus(resource.getStatus().getDisplay());
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
        medicationAdministrationDto.setEncounterId(resource.getContext().getReference());
        medicationAdministrationDto.setPatient(resource.getSubject().getDisplay());
        medicationAdministrationDto.setDosage(resource.getDosage().getText());
        medicationAdministrationDto.setStart(resource.getEffectivePeriod().getStart());
        medicationAdministrationDto.setEnd(resource.getEffectivePeriod().getEnd());
        medicationAdministrationDto.setStatus(resource.getStatus().getDisplay());
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
        attachment.setData(newDocumentReferenceDto.getData().getBytes());
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
        documentReferenceDto.setEncounterId(resource.getContext().getEncounter().get(0).getReference());
        documentReferenceDto.setDate(resource.getDate());
        documentReferenceDto.setPractitioner(resource.getAuthenticator().getDisplay());
        documentReferenceDto.setPatient(resource.getSubject().getDisplay());
        documentReferenceDto.setData(resource.getContent().get(0).getAttachment().getData());
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
}
