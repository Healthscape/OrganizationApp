package healthscape.com.healthscape.patient_records.parser;

import org.hl7.fhir.r4.model.AllergyIntolerance;
import org.hl7.fhir.r4.model.ClinicalImpression;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.MedicationAdministration;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ca.uhn.fhir.parser.IParser;
import healthscape.com.healthscape.fhir.config.FhirConfig;
import healthscape.com.healthscape.patient_records.model.PatientRecord;
import io.jsonwebtoken.io.IOException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class PatientRecordParser {

    private final FhirConfig fhirConfig;
    private final ObjectMapper objectMapper;
    
    public PatientRecord parsePatientRecord(String jsonString) throws IOException, JsonMappingException, JsonProcessingException {
        IParser jsonParser = fhirConfig.getFhirContext().newJsonParser();
        // Parse JSON string into a JsonNode tree for easier access
        JsonNode rootNode = objectMapper.readTree(jsonString);
        log.info("rootNode size: {}", rootNode.size());

        // Create a new PatientRecord object
        PatientRecord patientRecord = new PatientRecord();
        
        // Parse the patient field
        JsonNode patientNode = rootNode.get("patient");
        if (patientNode != null) {
            patientRecord.patient = (Patient) jsonParser.parseResource(Patient.class, patientNode.toString());
            log.info("Done patinet");
        }
        
        // Parse the encounters list
        JsonNode encountersNode = rootNode.get("encounters");
        if (encountersNode != null && encountersNode.isArray()) {
            patientRecord.encounters = new ArrayList<>();
            for (JsonNode encounterNode : encountersNode) {
                Encounter encounter = (Encounter) jsonParser.parseResource(Encounter.class, encounterNode.toString());
                patientRecord.encounters.add(encounter);
            }
            log.info("Done encountersNode");

        }
        
        // Parse the medications list
        JsonNode medicationsNode = rootNode.get("medications");
        if (medicationsNode != null && medicationsNode.isArray()) {
            patientRecord.medications = new ArrayList<>();
            for (JsonNode medicationNode : medicationsNode) {
                MedicationAdministration medication = (MedicationAdministration) jsonParser.parseResource(MedicationAdministration.class, medicationNode.toString());
                patientRecord.medications.add(medication);
            }
            log.info("Done medicationsNode");
        }
        
        // Parse the impressions list
        JsonNode impressionsNode = rootNode.get("impressions");
        if (impressionsNode != null && impressionsNode.isArray()) {
            patientRecord.impressions = new ArrayList<>();
            for (JsonNode impressionNode : impressionsNode) {
                ClinicalImpression impression = (ClinicalImpression) jsonParser.parseResource(ClinicalImpression.class, impressionNode.toString());
                patientRecord.impressions.add(impression);
            }
            log.info("Done impressionsNode");
        }
        
        // Parse the conditions list
        JsonNode conditionsNode = rootNode.get("conditions");
        if (conditionsNode != null && conditionsNode.isArray()) {
            patientRecord.conditions = new ArrayList<>();
            for (JsonNode conditionNode : conditionsNode) {
                Condition condition = (Condition) jsonParser.parseResource(Condition.class, conditionNode.toString());
                patientRecord.conditions.add(condition);
            }
            log.info("Done conditionsNode");
        }
        
        // Parse the allergies list
        JsonNode allergiesNode = rootNode.get("allergies");
        if (allergiesNode != null && allergiesNode.isArray()) {
            patientRecord.allergies = new ArrayList<>();
            for (JsonNode allergyNode : allergiesNode) {
                AllergyIntolerance allergy = (AllergyIntolerance) jsonParser.parseResource(AllergyIntolerance.class, allergyNode.toString());
                patientRecord.allergies.add(allergy);
            }
            log.info("Done allergiesNode");
        }
        
        // Parse the documentReferences list
        JsonNode documentReferencesNode = rootNode.get("documentReferences");
        if (documentReferencesNode != null && documentReferencesNode.isArray()) {
            patientRecord.documentReferences = new ArrayList<>();
            for (JsonNode documentReferenceNode : documentReferencesNode) {
                DocumentReference documentReference = (DocumentReference) jsonParser.parseResource(DocumentReference.class, documentReferenceNode.toString());
                patientRecord.documentReferences.add(documentReference);
            }
            log.info("Done documentReferencesNode");
        }
        
        log.info("jsonString: {}", jsonString);
        log.info("patientRecord: {}", patientRecord.getPatient().getId().toString());
        return patientRecord;
    }

    public String convertPatientRecordToJson(PatientRecord patientRecord) throws JsonMappingException, JsonProcessingException {
        // Create a JSON node to hold the entire PatientRecord
        ObjectNode patientRecordNode = objectMapper.createObjectNode();

        // Create an IParser for JSON serialization
        IParser jsonParser = fhirConfig.getFhirContext().newJsonParser();

        // Serialize the patient field
        if (patientRecord.patient != null) {
            String patientJson = jsonParser.encodeResourceToString(patientRecord.patient);
            patientRecordNode.set("patient", objectMapper.readTree(patientJson));
        }

        // Serialize the encounters list
        if (patientRecord.encounters != null) {
            List<ObjectNode> encounterNodes = new ArrayList<>();
            for (Encounter encounter : patientRecord.encounters) {
                String encounterJson = jsonParser.encodeResourceToString(encounter);
                encounterNodes.add((ObjectNode) objectMapper.readTree(encounterJson));
            }
            patientRecordNode.set("encounters", objectMapper.valueToTree(encounterNodes));
        }

        // Serialize the medications list
        if (patientRecord.medications != null) {
            List<ObjectNode> medicationNodes = new ArrayList<>();
            for (MedicationAdministration medication : patientRecord.medications) {
                String medicationJson = jsonParser.encodeResourceToString(medication);
                medicationNodes.add((ObjectNode) objectMapper.readTree(medicationJson));
            }
            patientRecordNode.set("medications", objectMapper.valueToTree(medicationNodes));
        }

        // Serialize the impressions list
        if (patientRecord.impressions != null) {
            List<ObjectNode> impressionNodes = new ArrayList<>();
            for (ClinicalImpression impression : patientRecord.impressions) {
                String impressionJson = jsonParser.encodeResourceToString(impression);
                impressionNodes.add((ObjectNode) objectMapper.readTree(impressionJson));
            }
            patientRecordNode.set("impressions", objectMapper.valueToTree(impressionNodes));
        }

        // Serialize the conditions list
        if (patientRecord.conditions != null) {
            List<ObjectNode> conditionNodes = new ArrayList<>();
            for (Condition condition : patientRecord.conditions) {
                String conditionJson = jsonParser.encodeResourceToString(condition);
                conditionNodes.add((ObjectNode) objectMapper.readTree(conditionJson));
            }
            patientRecordNode.set("conditions", objectMapper.valueToTree(conditionNodes));
        }

        // Serialize the allergies list
        if (patientRecord.allergies != null) {
            List<ObjectNode> allergyNodes = new ArrayList<>();
            for (AllergyIntolerance allergy : patientRecord.allergies) {
                String allergyJson = jsonParser.encodeResourceToString(allergy);
                allergyNodes.add((ObjectNode) objectMapper.readTree(allergyJson));
            }
            patientRecordNode.set("allergies", objectMapper.valueToTree(allergyNodes));
        }

        // Serialize the documentReferences list
        if (patientRecord.documentReferences != null) {
            List<ObjectNode> documentReferenceNodes = new ArrayList<>();
            for (DocumentReference documentReference : patientRecord.documentReferences) {
                String documentReferenceJson = jsonParser.encodeResourceToString(documentReference);
                documentReferenceNodes.add((ObjectNode) objectMapper.readTree(documentReferenceJson));
            }
            patientRecordNode.set("documentReferences", objectMapper.valueToTree(documentReferenceNodes));
        }

        // Convert the JSON node to a string
        return patientRecordNode.toString();
    }
}
