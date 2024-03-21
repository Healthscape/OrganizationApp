package healthscape.com.healthscape.fhir.service;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import healthscape.com.healthscape.encounter.dto.*;
import healthscape.com.healthscape.encounter.mapper.EncounterMapper;
import healthscape.com.healthscape.fabric.dto.ChaincodePatientRecordDto;
import healthscape.com.healthscape.fhir.config.FhirConfig;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.util.EncryptionUtil;
import healthscape.com.healthscape.util.HashUtil;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FhirPatientRecordService {

    private final IGenericClient fhirClient;
    private final FhirConfig fhirConfig;
    private final EncryptionUtil encryptionUtil;
    private final EncounterMapper encounterMapper;
    private final FhirUserService fhirUserService;


    public ChaincodePatientRecordDto createPatientRecordUpdateDto(String recordId, String userId) {
        String hashedData = this.getPatientDataHash(recordId);
        String offlineDataUrl = this.encryptionUtil.encryptIfNotAlready(recordId);
        String encryptedUserId = this.encryptionUtil.encryptIfNotAlready(userId);
        return new ChaincodePatientRecordDto(offlineDataUrl, hashedData, encryptedUserId);
    }

    public String getPatientDataHash(String recordId) {
        Bundle bundle = getPatientRecord(recordId);
        String bundleStr = fhirConfig.getFhirContext().newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle);
        JsonObject jsonObject = JsonParser.parseString(bundleStr).getAsJsonObject();
        String dataStr = jsonObject.get("entry").toString();
        return HashUtil.hashData(dataStr);
    }

    public Bundle getPatientRecord(String recordId) {
        if (recordId.length() == 64) {
            recordId = this.encryptionUtil.decryptIfNotAlready(recordId);
        }
        String url = this.fhirClient.getServerBase() + "/Patient/" + recordId + "/$everything";
        return this.fhirClient.search().byUrl(url).returnBundle(Bundle.class).execute();
    }

    public StartEncounterDto updatePatientRecordWithEncounter(String encryptedPatientId, ChaincodePatientRecordDto patientRecord, AppUser user) throws Exception {

        Encounter encounter = createEncounter(patientRecord.getOfflineDataUrl(), user.getId().toString());
        MethodOutcome methodOutcome = saveEncounterResources(encounter);
        String encounterId = methodOutcome.getResource().getIdElement().getIdPart();

        String recordId = this.encryptionUtil.decryptIfNotAlready(patientRecord.getOfflineDataUrl());
        String patientId = this.encryptionUtil.decryptIfNotAlready(encryptedPatientId);
        ChaincodePatientRecordDto updatedPatientRecord = createPatientRecordUpdateDto(recordId, patientId);
        return new StartEncounterDto(encounterId, updatedPatientRecord);
    }

    public ChaincodePatientRecordDto updatePatientRecordsEncounter(PatientRecordUpdateDto patientRecordUpdateDto, Encounter encounter) throws Exception {

        patientRecordUpdateDto.setDate(new Date());
        encounter.setStatus(Encounter.EncounterStatus.FINISHED);
        encounter.getPeriod().setEnd(patientRecordUpdateDto.getDate());

        Reference encounterRef = new Reference(encounter);
        encounterRef.setReference("Encounter/" + patientRecordUpdateDto.getEncounterId());

        saveEncounterData(encounterRef, encounter, patientRecordUpdateDto);
        String recordId = encounter.getSubject().getReference().substring("Patient/".length());
        return createPatientRecordUpdateDto(recordId, patientRecordUpdateDto.getPatientId());
    }

    public Encounter getEncounter(String id) {
        return this.fhirClient.read().resource(Encounter.class).withId(id).execute();
    }

    public MethodOutcome saveEncounterResources(Resource resource) throws Exception {
        List<ResourceType> allowedTypes = Arrays.asList(ResourceType.Condition, ResourceType.Medication, ResourceType.MedicationAdministration, ResourceType.DocumentReference, ResourceType.ClinicalImpression, ResourceType.Encounter);
        if (allowedTypes.contains(resource.getResourceType())) {
            return this.fhirClient.create().resource(resource).execute();
        } else {
            throw new Exception("Creation of " + resource.getResourceType().toString() + " resource type is not supported.");
        }
    }

    public Encounter createEncounter(String encryptedRecordId, String practitionerId) {
        Patient patient = fhirUserService.getPatient(encryptedRecordId);
        Practitioner practitioner = fhirUserService.getPractitioner(practitionerId);
        return this.encounterMapper.mapToEncounter(patient, practitioner);
    }

    public void saveEncounterData(Reference encounterRef, Encounter encounter, PatientRecordUpdateDto patientRecordUpdateDto) throws Exception {
        List<ClinicalImpression.ClinicalImpressionFindingComponent> conditions = saveConditions(encounterRef, encounter, patientRecordUpdateDto);
        ClinicalImpression clinicalImpression = this.encounterMapper.mapToClinicalImpression(encounterRef, encounter, patientRecordUpdateDto);
        clinicalImpression.setFinding(conditions);

        saveDocuments(encounterRef, encounter, patientRecordUpdateDto);
        saveMedications(encounterRef, encounter, patientRecordUpdateDto);

        saveEncounterResources(clinicalImpression);
    }

    private void saveMedications(Reference encounterRef, Encounter encounter, PatientRecordUpdateDto patientRecordUpdateDto) throws Exception {
        for (NewMedicationDto newMedicationDto : patientRecordUpdateDto.getMedications()) {
            if (newMedicationDto.getId() != null) {
                updateMedicationAdministration(newMedicationDto, patientRecordUpdateDto);
            } else {
                createNewMedicationAdministration(encounterRef, encounter, newMedicationDto, patientRecordUpdateDto);
            }
        }
    }

    private void createNewMedicationAdministration(Reference encounterRef, Encounter encounter, NewMedicationDto newMedicationDto, PatientRecordUpdateDto patientRecordUpdateDto) throws Exception {
        Reference medicationRef = saveMedication(newMedicationDto);
        MedicationAdministration medicationAdministration = this.encounterMapper.mapToMedicationAdministration(encounterRef, encounter, newMedicationDto, patientRecordUpdateDto, medicationRef);
        saveEncounterResources(medicationAdministration);
    }

    private Reference saveMedication(NewMedicationDto newMedicationDto) throws Exception {
        Medication medication = this.encounterMapper.mapToMedication(newMedicationDto);
        MethodOutcome savedMedication = saveEncounterResources(medication);
        Reference medicationRef = new Reference(medication);
        medicationRef.setReference("Medication/" + savedMedication.getResource().getIdElement().getIdPart());
        medicationRef.setDisplay(newMedicationDto.getMedication());
        return medicationRef;
    }

    private void updateMedicationAdministration(NewMedicationDto newMedicationDto, PatientRecordUpdateDto patientRecordUpdateDto) {
        MedicationAdministration medicationAdministration = this.fhirClient.read().resource(MedicationAdministration.class).withId(newMedicationDto.getId()).execute();
        medicationAdministration.setEffective(((Period) medicationAdministration.getEffective()).setEnd(patientRecordUpdateDto.getDate()));
        if (newMedicationDto.getStatus().equals(MedicationAdministration.MedicationAdministrationStatus.COMPLETED.toString())) {
            medicationAdministration.setStatus(MedicationAdministration.MedicationAdministrationStatus.COMPLETED);
        } else if (newMedicationDto.getStatus().equals(MedicationAdministration.MedicationAdministrationStatus.STOPPED.toString())) {
            medicationAdministration.setStatus(MedicationAdministration.MedicationAdministrationStatus.STOPPED);
        }
        this.fhirClient.update().resource(medicationAdministration).execute();
    }

    private void saveDocuments(Reference encounterRef, Encounter encounter, PatientRecordUpdateDto patientRecordUpdateDto) throws Exception {
        for (NewDocumentReferenceDto newDocumentReferenceDto : patientRecordUpdateDto.getDocuments()) {
            DocumentReference documentReference = this.encounterMapper.mapToDocumentReference(encounterRef, encounter, patientRecordUpdateDto, newDocumentReferenceDto);
            saveEncounterResources(documentReference);
        }
    }

    private List<ClinicalImpression.ClinicalImpressionFindingComponent> saveConditions(Reference encounterRef, Encounter encounter, PatientRecordUpdateDto patientRecordUpdateDto) throws Exception {
        List<ClinicalImpression.ClinicalImpressionFindingComponent> findingComponents = new ArrayList<>();
        for (NewConditionDto newConditionDto : patientRecordUpdateDto.getConditions()) {
            if (newConditionDto.getId() != null) {
                updateCondition(newConditionDto, patientRecordUpdateDto);
            } else {
                ClinicalImpression.ClinicalImpressionFindingComponent component = createNewCondition(encounterRef, encounter, patientRecordUpdateDto, newConditionDto);
                findingComponents.add(component);
            }
        }
        return findingComponents;
    }

    private ClinicalImpression.ClinicalImpressionFindingComponent createNewCondition(Reference encounterRef, Encounter encounter, PatientRecordUpdateDto patientRecordUpdateDto, NewConditionDto newConditionDto) throws Exception {
        Condition condition = this.encounterMapper.mapToCondition(encounterRef, encounter, patientRecordUpdateDto, newConditionDto);
        MethodOutcome methodOutcome = saveEncounterResources(condition);
        Condition savedCondition = (Condition) methodOutcome.getResource();
        Reference conditionRef = new Reference(savedCondition);
        conditionRef.setReference("Condition/" + savedCondition.getIdElement().getIdPart());
        conditionRef.setDisplay(newConditionDto.getText());
        ClinicalImpression.ClinicalImpressionFindingComponent clinicalImpressionFindingComponent = new ClinicalImpression.ClinicalImpressionFindingComponent();
        clinicalImpressionFindingComponent.setItemReference(conditionRef);
        return clinicalImpressionFindingComponent;
    }

    private void updateCondition(NewConditionDto newConditionDto, PatientRecordUpdateDto patientRecordUpdateDto) {
        Condition condition = this.fhirClient.read().resource(Condition.class).withId(newConditionDto.getId()).execute();
        CodeableConcept codeableConceptStatus = new CodeableConcept();
        codeableConceptStatus.setText("INACTIVE");
        condition.setClinicalStatus(codeableConceptStatus);
        condition.setAbatement(new DateTimeType(patientRecordUpdateDto.getDate()));
        this.fhirClient.update().resource(condition).execute();
    }
}
