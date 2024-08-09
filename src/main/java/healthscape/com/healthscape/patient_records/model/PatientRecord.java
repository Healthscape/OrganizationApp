package healthscape.com.healthscape.patient_records.model;

import java.util.List;
import java.util.ArrayList;

import org.hl7.fhir.r4.model.AllergyIntolerance;
import org.hl7.fhir.r4.model.ClinicalImpression;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.MedicationAdministration;
import org.hl7.fhir.r4.model.Patient;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatientRecord {
    public Patient patient;
    public List<Encounter> encounters;
    public List<MedicationAdministration> medications;
    public List<ClinicalImpression> impressions;
    public List<Condition> conditions;
    public List<AllergyIntolerance> allergies;
    public List<DocumentReference> documentReferences;

    public PatientRecord(Patient patient) {
        this.patient = patient;
        this.encounters = new ArrayList<>();
        this.medications = new ArrayList<>();
        this.impressions = new ArrayList<>();
        this.conditions = new ArrayList<>();
        this.allergies = new ArrayList<>();
        this.documentReferences = new ArrayList<>();
    }

}
