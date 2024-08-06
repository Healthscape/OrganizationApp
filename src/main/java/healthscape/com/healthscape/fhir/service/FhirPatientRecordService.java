package healthscape.com.healthscape.fhir.service;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import healthscape.com.healthscape.encounter.dto.*;
import healthscape.com.healthscape.encounter.mapper.EncounterMapper;
import healthscape.com.healthscape.fabric.dto.ChaincodePatientRecordDto;
import healthscape.com.healthscape.fhir.config.FhirConfig;
import healthscape.com.healthscape.patientRecords.dtos.AllergyDto;
import healthscape.com.healthscape.patientRecords.dtos.ConditionDto;
import healthscape.com.healthscape.patientRecords.dtos.MedicationAdministrationDto;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.util.Config;
import healthscape.com.healthscape.util.EncryptionConfig;
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

    // private final IGenericClient fhirClient;
    private final FhirConfig fhirConfig;
    private final EncryptionConfig encryptionConfig;
    private final EncounterMapper encounterMapper;
    private final FhirMapperService fhirMapperService;


    // public ChaincodePatientRecordDto createPatientRecordUpdateDto(String recordId, String userId) {
    //     Bundle bundle = getPatientRecord(recordId);
    //     String hashedData = this.getPatientDataHash(bundle);
    //     String offlineDataUrl = this.encryptionConfig.defaultEncryptionUtil().encryptIfNotAlready(recordId);
    //     String encryptedUserId = this.encryptionConfig.defaultEncryptionUtil().encryptIfNotAlready(userId);
    //     return new ChaincodePatientRecordDto(offlineDataUrl, hashedData, encryptedUserId, "lala");
    // }

    public String getPatientDataHash(Bundle bundle) {
        String bundleStr = fhirConfig.getFhirContext().newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle);
        JsonObject jsonObject = JsonParser.parseString(bundleStr).getAsJsonObject();
        String dataStr = jsonObject.get("entry").toString();
        return HashUtil.hashData(dataStr);
    }

    // public Bundle getPatientRecord(String recordId) {
    //     if (recordId.length() == 64) {
    //         recordId = this.encryptionConfig.defaultEncryptionUtil().decryptIfNotAlready(recordId);
    //     }
    //     String url = this.fhirClient.getServerBase() + "/Patient/" + recordId + "/$everything";
    //     return this.fhirClient.search().byUrl(url).returnBundle(Bundle.class).execute();
    // }

    // public StartEncounterDto updatePatientRecordWithEncounter(String encryptedPatientId, ChaincodePatientRecordDto patientRecord, AppUser user) throws Exception {

    //     Encounter encounter = createEncounter(patientRecord.getOfflineDataUrl(), user.getId().toString());
    //     MethodOutcome methodOutcome = saveEncounterResources(encounter);
    //     String encounterId = methodOutcome.getResource().getIdElement().getIdPart();

    //     String recordId = this.encryptionConfig.defaultEncryptionUtil().decryptIfNotAlready(patientRecord.getOfflineDataUrl());
    //     String patientId = this.encryptionConfig.defaultEncryptionUtil().decryptIfNotAlready(encryptedPatientId);
    //     ChaincodePatientRecordDto updatedPatientRecord = createPatientRecordUpdateDto(recordId, patientId);
    //     return new StartEncounterDto(encounterId, updatedPatientRecord);
    // }

    // public ChaincodePatientRecordDto updatePatientRecordsEncounter(PatientRecordUpdateDto patientRecordUpdateDto, Encounter encounter) throws Exception {

    //     patientRecordUpdateDto.setDate(new Date());
    //     encounter.setStatus(Encounter.EncounterStatus.FINISHED);
    //     encounter.getPeriod().setEnd(patientRecordUpdateDto.getDate());
    //     this.fhirClient.update().resource(encounter).execute();

    //     Reference encounterRef = new Reference(encounter);
    //     encounterRef.setReference("Encounter/" + patientRecordUpdateDto.getEncounterId());

    //     saveEncounterData(encounterRef, encounter, patientRecordUpdateDto);
    //     String recordId = encounter.getSubject().getReference().substring("Patient/".length());
    //     return createPatientRecordUpdateDto(recordId, patientRecordUpdateDto.getPatientId());
    // }

    // public Encounter getEncounter(String id) {
    //     return this.fhirClient.read().resource(Encounter.class).withId(id).execute();
    // }

    // public MethodOutcome saveEncounterResources(Resource resource) throws Exception {
    //     List<ResourceType> allowedTypes = Arrays.asList(ResourceType.Condition, ResourceType.Medication, ResourceType.MedicationAdministration, ResourceType.DocumentReference, ResourceType.ClinicalImpression, ResourceType.Encounter, ResourceType.AllergyIntolerance);
    //     if (allowedTypes.contains(resource.getResourceType())) {
    //         return this.fhirClient.create().resource(resource).execute();
    //     } else {
    //         throw new Exception("Creation of " + resource.getResourceType().toString() + " resource type is not supported.");
    //     }
    // }

    // public Encounter createEncounter(String encryptedRecordId, String practitionerId) {
    //     Patient patient = fhirMapperService.getPatient(encryptedRecordId);
    //     Practitioner practitioner = fhirMapperService.getPractitioner(practitionerId);
    //     return this.encounterMapper.mapToEncounter(patient, practitioner);
    // }

    // public void saveEncounterData(Reference encounterRef, Encounter encounter, PatientRecordUpdateDto patientRecordUpdateDto) throws Exception {
    //     saveClinicalImpression(encounterRef, encounter, patientRecordUpdateDto);
    //     saveDocuments(encounterRef, encounter, patientRecordUpdateDto);
    //     saveMedications(encounterRef, encounter, patientRecordUpdateDto);
    //     saveAllergies(encounterRef, encounter, patientRecordUpdateDto);


    // }

    // private void saveClinicalImpression(Reference encounterRef, Encounter encounter, PatientRecordUpdateDto patientRecordUpdateDto) throws Exception {
    //     List<ClinicalImpression.ClinicalImpressionFindingComponent> conditions = saveConditions(encounterRef, encounter, patientRecordUpdateDto);
    //     ClinicalImpression clinicalImpression = this.encounterMapper.mapToClinicalImpression(encounterRef, encounter, patientRecordUpdateDto);
    //     if(conditions!=null) {
    //         clinicalImpression.setFinding(conditions);
    //     }
    //     saveEncounterResources(clinicalImpression);
    // }

    // public List<MedicationAdministrationDto> getMedicationAdministrationHistory(String patientId) {
    //     Bundle response = this.fhirClient.search()
    //             .forResource(MedicationAdministration.class)
    //             .where(Patient.IDENTIFIER.exactly().systemAndValues(Config.HEALTHSCAPE_URL, patientId))
    //             .and(MedicationAdministration.STATUS.exactly().codes("stopped", "completed"))
    //             .returnBundle(Bundle.class)
    //             .execute();
    //     List<MedicationAdministrationDto> medicationAdministrationDtos = new ArrayList<>();
    //     for (Bundle.BundleEntryComponent entry : response.getEntry()) {
    //         if (entry.getResource() instanceof MedicationAdministration medicationAdministration) {
    //             MedicationAdministrationDto administrationDto = this.encounterMapper.mapToMedicationAdministrationDto(medicationAdministration);
    //             medicationAdministrationDtos.add(administrationDto);
    //         }
    //     }
    //     return medicationAdministrationDtos;
    // }

    // private void saveAllergies(Reference encounterRef, Encounter encounter, PatientRecordUpdateDto patientRecordUpdateDto) throws Exception {
    //     if(patientRecordUpdateDto.getAllergies()==null){
    //         return;
    //     }
    //     for (NewAllergyDto newAllergyDto : patientRecordUpdateDto.getAllergies()) {
    //         if (newAllergyDto.getId() != null) {
    //             updateAllergy(newAllergyDto, patientRecordUpdateDto);
    //         } else {
    //             createAllergy(encounterRef, encounter, newAllergyDto, patientRecordUpdateDto);
    //         }
    //     }
    // }

    // private void createAllergy(Reference encounterRef, Encounter encounter, NewAllergyDto newAllergyDto, PatientRecordUpdateDto patientRecordUpdateDto) throws Exception {
    //     AllergyIntolerance allergyIntolerance = this.encounterMapper.mapToAllergyIntolerance(encounterRef, encounter, newAllergyDto, patientRecordUpdateDto);
    //     saveEncounterResources(allergyIntolerance);
    // }

    // private void updateAllergy(NewAllergyDto newAllergyDto, PatientRecordUpdateDto patientRecordUpdateDto) {
    //     AllergyIntolerance allergyIntolerance = this.fhirClient.read().resource(AllergyIntolerance.class).withId(newAllergyDto.getId()).execute();
    //     allergyIntolerance.getOnsetPeriod().setEnd(patientRecordUpdateDto.getDate());
    //     CodeableConcept codeableConcept = new CodeableConcept();
    //     codeableConcept.setText("INACTIVE");
    //     allergyIntolerance.setClinicalStatus(codeableConcept);
    //     this.fhirClient.update().resource(allergyIntolerance).execute();
    // }

    // private void saveMedications(Reference encounterRef, Encounter encounter, PatientRecordUpdateDto patientRecordUpdateDto) throws Exception {
    //     if(patientRecordUpdateDto.getMedications()==null){
    //         return;
    //     }
    //     for (NewMedicationDto newMedicationDto : patientRecordUpdateDto.getMedications()) {
    //         if (newMedicationDto.getId() != null) {
    //             updateMedicationAdministration(newMedicationDto, patientRecordUpdateDto);
    //         } else {
    //             createNewMedicationAdministration(encounterRef, encounter, newMedicationDto, patientRecordUpdateDto);
    //         }
    //     }
    // }

    // private void createNewMedicationAdministration(Reference encounterRef, Encounter encounter, NewMedicationDto newMedicationDto, PatientRecordUpdateDto patientRecordUpdateDto) throws Exception {
    //     Reference medicationRef = saveMedication(newMedicationDto);
    //     MedicationAdministration medicationAdministration = this.encounterMapper.mapToMedicationAdministration(encounterRef, encounter, newMedicationDto, patientRecordUpdateDto, medicationRef);
    //     saveEncounterResources(medicationAdministration);
    // }

    // private Reference saveMedication(NewMedicationDto newMedicationDto) throws Exception {
    //     Medication medication = this.encounterMapper.mapToMedication(newMedicationDto);
    //     MethodOutcome savedMedication = saveEncounterResources(medication);
    //     Reference medicationRef = new Reference(medication);
    //     medicationRef.setReference("Medication/" + savedMedication.getResource().getIdElement().getIdPart());
    //     medicationRef.setDisplay(newMedicationDto.getMedication());
    //     return medicationRef;
    // }

    // private void updateMedicationAdministration(NewMedicationDto newMedicationDto, PatientRecordUpdateDto patientRecordUpdateDto) {
    //     MedicationAdministration medicationAdministration = this.fhirClient.read().resource(MedicationAdministration.class).withId(newMedicationDto.getId()).execute();
    //     medicationAdministration.setEffective(((Period) medicationAdministration.getEffective()).setEnd(patientRecordUpdateDto.getDate()));
    //     medicationAdministration.setStatus(MedicationAdministration.MedicationAdministrationStatus.fromCode(newMedicationDto.getStatus()));
    //     this.fhirClient.update().resource(medicationAdministration).execute();
    // }

    // private void saveDocuments(Reference encounterRef, Encounter encounter, PatientRecordUpdateDto patientRecordUpdateDto) throws Exception {
    //     if(patientRecordUpdateDto.getDocuments()==null){
    //         return;
    //     }
    //     for (NewDocumentReferenceDto newDocumentReferenceDto : patientRecordUpdateDto.getDocuments()) {
    //         DocumentReference documentReference = this.encounterMapper.mapToDocumentReference(encounterRef, encounter, patientRecordUpdateDto, newDocumentReferenceDto);
    //         saveEncounterResources(documentReference);
    //     }
    // }

    // private List<ClinicalImpression.ClinicalImpressionFindingComponent> saveConditions(Reference encounterRef, Encounter encounter, PatientRecordUpdateDto patientRecordUpdateDto) throws Exception {
    //     List<ClinicalImpression.ClinicalImpressionFindingComponent> findingComponents = new ArrayList<>();
    //     if(patientRecordUpdateDto.getConditions() == null){
    //         return null;
    //     }
    //     for (NewConditionDto newConditionDto : patientRecordUpdateDto.getConditions()) {
    //         if (newConditionDto.getId() != null) {
    //             updateCondition(newConditionDto, patientRecordUpdateDto);
    //         } else {
    //             ClinicalImpression.ClinicalImpressionFindingComponent component = createNewCondition(encounterRef, encounter, patientRecordUpdateDto, newConditionDto);
    //             findingComponents.add(component);
    //         }
    //     }
    //     return findingComponents;
    // }

    // private ClinicalImpression.ClinicalImpressionFindingComponent createNewCondition(Reference encounterRef, Encounter encounter, PatientRecordUpdateDto patientRecordUpdateDto, NewConditionDto newConditionDto) throws Exception {
    //     Condition condition = this.encounterMapper.mapToCondition(encounterRef, encounter, patientRecordUpdateDto, newConditionDto);
    //     MethodOutcome methodOutcome = saveEncounterResources(condition);
    //     Condition savedCondition = (Condition) methodOutcome.getResource();
    //     Reference conditionRef = new Reference(savedCondition);
    //     conditionRef.setReference("Condition/" + savedCondition.getIdElement().getIdPart());
    //     conditionRef.setDisplay(newConditionDto.getText());
    //     ClinicalImpression.ClinicalImpressionFindingComponent clinicalImpressionFindingComponent = new ClinicalImpression.ClinicalImpressionFindingComponent();
    //     clinicalImpressionFindingComponent.setItemReference(conditionRef);
    //     return clinicalImpressionFindingComponent;
    // }

    // private void updateCondition(NewConditionDto newConditionDto, PatientRecordUpdateDto patientRecordUpdateDto) {
    //     Condition condition = this.fhirClient.read().resource(Condition.class).withId(newConditionDto.getId()).execute();
    //     CodeableConcept codeableConceptStatus = new CodeableConcept();
    //     codeableConceptStatus.setText("INACTIVE");
    //     condition.setClinicalStatus(codeableConceptStatus);
    //     condition.setAbatement(new DateTimeType(patientRecordUpdateDto.getDate()));
    //     this.fhirClient.update().resource(condition).execute();
    // }

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
