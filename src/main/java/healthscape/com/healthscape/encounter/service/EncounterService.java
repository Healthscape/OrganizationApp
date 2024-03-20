package healthscape.com.healthscape.encounter.service;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IReadExecutable;
import healthscape.com.healthscape.encounter.dto.ConditionDto;
import healthscape.com.healthscape.encounter.dto.DocumentReferenceDto;
import healthscape.com.healthscape.encounter.dto.EncounterDto;
import healthscape.com.healthscape.encounter.dto.MedicationDto;
import healthscape.com.healthscape.fabric.dto.ChaincodePatientRecordDto;
import healthscape.com.healthscape.fabric.service.FabricAccessRequestService;
import healthscape.com.healthscape.fhir.service.FhirService;
import healthscape.com.healthscape.patientRecords.mapper.PatientRecordChaincodeMapper;
import healthscape.com.healthscape.patientRecords.service.PatientRecordService;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.users.service.UserService;
import healthscape.com.healthscape.util.EncryptionUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EncounterService {


    private final UserService userService;
    private final FabricAccessRequestService accessRequestService;
    private final PatientRecordService patientRecordService;
    private final FhirService fhirService;
    private final IGenericClient fhirClient;
    private final PatientRecordChaincodeMapper patientRecordChaincodeMapper;
    private final EncryptionUtil encryptionUtil;

    public EncounterDto startNewEncounter(String token, String patientId, String recordId) throws Exception {
        AppUser user = userService.getUserFromToken(token);
        String patientRecordStr = this.accessRequestService.isAccessRequestApproved(user.getEmail(), patientId, recordId);
        ChaincodePatientRecordDto patientRecord = patientRecordChaincodeMapper.mapToPatientRecordDto(patientRecordStr);

        if(patientRecord == null){
            throw new Exception("Unauthorized access");
        }

        Encounter encounter = this.createEncounter(patientRecord.getOfflineDataUrl(), user.getId().toString());
        MethodOutcome methodOutcome = this.fhirClient.create().resource(encounter).execute();
        String encounterId = methodOutcome.getResource().getIdElement().getIdPart();

        String decryptedRecordId = this.encryptionUtil.decrypt(patientRecord.getOfflineDataUrl());
        ChaincodePatientRecordDto updatedPatientRecord = this.fhirService.getPatientRecordUpdateDto(decryptedRecordId, patientId, true);
        this.patientRecordService.updatePatientRecord(user, updatedPatientRecord);
        return new EncounterDto(encounterId, decryptedRecordId, patientId);
    }

    public void endEncounter(String token, EncounterDto encounterDto) throws Exception {
        AppUser user = userService.getUserFromToken(token);

        encounterDto.setDate(new Date());
        Encounter encounter = this.getEncounter(encounterDto.getEncounterId());

        if(encounter.getStatus().equals(Encounter.EncounterStatus.FINISHED)){
            throw new Exception("Encounter already finished.");
        }

        encounter.setStatus(Encounter.EncounterStatus.FINISHED);
        encounter.getPeriod().setEnd(encounterDto.getDate());

        Reference encounterRef = new Reference(encounter);
        encounterRef.setReference("Encounter/" + encounterDto.getEncounterId());

        this.createClinicalImpression(encounterRef, encounter, encounterDto);

        ChaincodePatientRecordDto updatedPatientRecord = this.fhirService.getPatientRecordUpdateDto(encounterDto.getRecordId(), encounterDto.getPatientId(), true);
        this.patientRecordService.updatePatientRecord(user, updatedPatientRecord);
    }

    private Encounter createEncounter(String encryptedRecordId, String practitionerId) {
        Encounter encounter = new Encounter();
        encounter.setStatus(Encounter.EncounterStatus.INPROGRESS);
        Patient patient = fhirService.getPatient(encryptedRecordId);
        Practitioner practitioner = fhirService.getPractitioner(practitionerId);
        Reference patientRef = new Reference(patient);
        patientRef = patientRef.setReference("Patient/" + patient.getIdElement().getIdPart());
        patientRef = patientRef.setDisplay(patient.getName().get(0).getGivenAsSingleString() + patient.getName().get(0).getFamily());
        encounter.setSubject(patientRef);
        List<Encounter.EncounterParticipantComponent> participant = new ArrayList<>();
        Encounter.EncounterParticipantComponent encounterParticipantComponent = new Encounter.EncounterParticipantComponent();
        Reference practitionerRef = new Reference(practitioner);
        practitionerRef.setReference("Practitioner/" + practitioner.getIdElement().getIdPart());
        practitionerRef.setDisplay(practitioner.getName().get(0).getGivenAsSingleString() + practitioner.getName().get(0).getFamily());
        encounterParticipantComponent.setIndividual(practitionerRef);
        participant.add(encounterParticipantComponent);
        encounter.setParticipant(participant);
        encounter.setPeriod(new Period().setStart(new Date()));
        return encounter;
    }

    private void createClinicalImpression(Reference encounterRef, Encounter encounter, EncounterDto encounterDto) {
        ClinicalImpression clinicalImpression = new ClinicalImpression();
        clinicalImpression.setEncounter(encounterRef);
        clinicalImpression.setSubject(encounter.getSubject());
        clinicalImpression.setDate(encounterDto.getDate());
        clinicalImpression.setStatus(ClinicalImpression.ClinicalImpressionStatus.COMPLETED);
        clinicalImpression.setAssessor(encounter.getParticipant().get(0).getIndividual());
        clinicalImpression.setDescription(encounterDto.getClinicalImpressionDescription());
        clinicalImpression.setSummary(encounterDto.getClinicalImpressionSummary());
        List<ClinicalImpression.ClinicalImpressionFindingComponent> conditions = saveConditions(encounterRef, encounter, encounterDto);
        clinicalImpression.setFinding(conditions);

        saveDocuments(encounterRef, encounter, encounterDto);
        saveMedications(encounterRef, encounter, encounterDto);

        this.fhirClient.create().resource(clinicalImpression).execute();
    }

    private void saveMedications(Reference encounterRef, Encounter encounter, EncounterDto encounterDto) {
        for (MedicationDto medicationDto: encounterDto.getMedications()) {
            if(medicationDto.getId() != null){
                updateMedicationAdministration(medicationDto, encounterDto);
            }else{
                createNewMedicationAdministration(encounterRef, encounter, medicationDto, encounterDto);
            }
        }
    }

    private void createNewMedicationAdministration(Reference encounterRef, Encounter encounter, MedicationDto medicationDto, EncounterDto encounterDto) {
        MedicationAdministration medicationAdministration = new MedicationAdministration();
        Medication medication = new Medication();
        medication.setCode(new CodeableConcept().setText(medicationDto.getMedication()));
        Reference medicationRef = new Reference(medication);
        MethodOutcome savedMedication = this.fhirClient.create().resource(medication).execute();
        medicationRef.setReference("Medication/" + savedMedication.getResource().getIdElement().getIdPart());
        medicationRef.setDisplay(medicationDto.getMedication());
        medicationAdministration.setMedication(medicationRef);
        medicationAdministration.setContext(encounterRef);
        medicationAdministration.setSubject(encounter.getSubject());
        medicationAdministration.setStatus(MedicationAdministration.MedicationAdministrationStatus.INPROGRESS);
        MedicationAdministration.MedicationAdministrationDosageComponent dosageComponent = new MedicationAdministration.MedicationAdministrationDosageComponent();
        dosageComponent.setText(medicationDto.getDosage());
        medicationAdministration.setDosage(dosageComponent);
        medicationAdministration.setEffective(new Period().setStart(encounterDto.getDate()));

        this.fhirClient.create().resource(medicationAdministration).execute();
    }

    private void updateMedicationAdministration(MedicationDto medicationDto, EncounterDto encounterDto) {
        MedicationAdministration medicationAdministration = this.fhirClient.read().resource(MedicationAdministration.class).withId(medicationDto.getId()).execute();
        medicationAdministration.setEffective(((Period) medicationAdministration.getEffective()).setEnd(encounterDto.getDate()));
        if(medicationDto.getStatus().equals(MedicationAdministration.MedicationAdministrationStatus.COMPLETED.toString())){
            medicationAdministration.setStatus(MedicationAdministration.MedicationAdministrationStatus.COMPLETED);
        }else if(medicationDto.getStatus().equals(MedicationAdministration.MedicationAdministrationStatus.STOPPED.toString())){
            medicationAdministration.setStatus(MedicationAdministration.MedicationAdministrationStatus.STOPPED);
        }
        this.fhirClient.update().resource(medicationAdministration).execute();
    }

    private void saveDocuments(Reference encounterRef, Encounter encounter, EncounterDto encounterDto) {
        for (DocumentReferenceDto documentReferenceDto: encounterDto.getDocuments()) {
            DocumentReference documentReference = new DocumentReference();
            documentReference.setDate(encounterDto.getDate());
            documentReference.setSubject(encounter.getSubject());
            documentReference.setAuthenticator(encounter.getParticipant().get(0).getIndividual());
            documentReference.setStatus(Enumerations.DocumentReferenceStatus.CURRENT);
            List<DocumentReference.DocumentReferenceContentComponent> contentComponents = new ArrayList<>();
            DocumentReference.DocumentReferenceContentComponent contentComponent = new DocumentReference.DocumentReferenceContentComponent();
            Attachment attachment = new Attachment();
            attachment.setData(documentReferenceDto.getData().getBytes());
            attachment.setCreation(encounterDto.getDate());
            attachment.setContentType(documentReferenceDto.getContentType());
            attachment.setTitle(documentReferenceDto.getTitle());
            contentComponent.setAttachment(attachment);
            contentComponents.add(contentComponent);
            documentReference.setContent(contentComponents);
            DocumentReference.DocumentReferenceContextComponent contextComponent = new DocumentReference.DocumentReferenceContextComponent();
            List<Reference> references = new ArrayList<>();
            references.add(encounterRef);
            contextComponent.setEncounter(references);
            documentReference.setContext(contextComponent);

            this.fhirClient.create().resource(documentReference).execute();
        }
    }

    private List<ClinicalImpression.ClinicalImpressionFindingComponent> saveConditions(Reference encounterRef, Encounter encounter, EncounterDto encounterDto) {
        List<ClinicalImpression.ClinicalImpressionFindingComponent> findingComponents = new ArrayList<>();
        for (ConditionDto conditionDto: encounterDto.getConditions()) {
            if(conditionDto.getId() != null){
                updateCondition(conditionDto, encounterDto);
            }else{
                ClinicalImpression.ClinicalImpressionFindingComponent component = createNewCondition(encounterRef, encounter, encounterDto, conditionDto);
                findingComponents.add(component);
            }
        }
        return findingComponents;
    }

    private ClinicalImpression.ClinicalImpressionFindingComponent createNewCondition(Reference encounterRef, Encounter encounter, EncounterDto encounterDto, ConditionDto conditionDto) {
        Condition condition = new Condition();
        condition.setEncounter(encounterRef);
        condition.setSubject(encounter.getSubject());
        condition.setAsserter(encounter.getParticipant().get(0).getIndividual());
        condition.setRecordedDate(encounterDto.getDate());
        CodeableConcept codeableConceptCode = new CodeableConcept();
        codeableConceptCode.setText(conditionDto.getText());
        condition.setCode(codeableConceptCode);
        CodeableConcept codeableConceptStatus = new CodeableConcept();
        codeableConceptStatus.setText("ACTIVE");
        condition.setClinicalStatus(codeableConceptStatus);
        condition.setOnset(new DateTimeType(encounterDto.getDate()));

        MethodOutcome methodOutcome = this.fhirClient.create().resource(condition).execute();
        Condition savedCondition = (Condition) methodOutcome.getResource();
        Reference conditionRef = new Reference(savedCondition);
        conditionRef.setReference("Condition/" + savedCondition.getIdElement().getIdPart());
        conditionRef.setDisplay(conditionDto.getText());
        ClinicalImpression.ClinicalImpressionFindingComponent clinicalImpressionFindingComponent = new ClinicalImpression.ClinicalImpressionFindingComponent();
        clinicalImpressionFindingComponent.setItemReference(conditionRef);
        return clinicalImpressionFindingComponent;
    }

    private void updateCondition(ConditionDto conditionDto, EncounterDto encounterDto) {
        Condition condition = this.fhirClient.read().resource(Condition.class).withId(conditionDto.getId()).execute();
        CodeableConcept codeableConceptStatus = new CodeableConcept();
        codeableConceptStatus.setText("INACTIVE");
        condition.setClinicalStatus(codeableConceptStatus);
        condition.setAbatement(new DateTimeType(encounterDto.getDate()));
        this.fhirClient.update().resource(condition).execute();
    }
    private Encounter getEncounter(String id){
        return this.fhirClient.read().resource(Encounter.class).withId(id).execute();
    }
}
