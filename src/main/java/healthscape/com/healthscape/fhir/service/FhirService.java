package healthscape.com.healthscape.fhir.service;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import healthscape.com.healthscape.fabric.dto.MyChaincodePatientRecordDto;
import healthscape.com.healthscape.fabric.dto.ChaincodePatientRecordDto;
import healthscape.com.healthscape.fhir.config.FhirConfig;
import healthscape.com.healthscape.fhir.dtos.FhirUserDto;
import healthscape.com.healthscape.fhir.mapper.PatientMapper;
import healthscape.com.healthscape.fhir.mapper.PractitionerMapper;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.users.model.Specialty;
import healthscape.com.healthscape.users.service.SpecialtyService;
import healthscape.com.healthscape.util.EncryptionUtil;
import healthscape.com.healthscape.util.HashUtil;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FhirService {

    private final IGenericClient fhirClient;
    private final PatientMapper patientMapper;
    private final PractitionerMapper practitionerMapper;
    private final SpecialtyService specialtyService;
    private final FhirConfig fhirConfig;
    private final EncryptionUtil encryptionUtil;

    public ChaincodePatientRecordDto registerPatient(AppUser appUser, String personalId) throws Exception {
        Patient patientData = getPatientWithPersonalId(personalId);
        if (patientData == null) {
            return createNewPatient(appUser, personalId);
        } else {
            return addHealthscapeId(appUser, patientData);
        }
    }

    private ChaincodePatientRecordDto addHealthscapeId(AppUser appUser, Patient patientData) throws Exception {
        for (Identifier id : patientData.getIdentifier()) {
            if (id.getSystem().equals("http://healthscape.com")) {
                throw new Exception("User is already registered at Healthscape!");
            }
        }
        String encryptedUserId = this.encryptionUtil.encrypt(appUser.getId().toString());
        Identifier identifier = createHealthscapeId(encryptedUserId);
        patientData.getIdentifier().add(identifier);

        MethodOutcome methodOutcome = this.fhirClient.update().resource(patientData).execute();
        return getPatientRecordUpdateDto(methodOutcome.getResource().getIdElement().getIdPart(), encryptedUserId, true);
    }

    private static Identifier createHealthscapeId(String encryptedUserId) {
        Identifier identifier = new Identifier();
        identifier.setSystem("http://healthscape.com");
        identifier.setUse(Identifier.IdentifierUse.OFFICIAL);
        identifier.setValue(encryptedUserId);
        return identifier;
    }

    private ChaincodePatientRecordDto createNewPatient(AppUser appUser, String personalId) {
        String encryptedPersonalId = this.encryptionUtil.encrypt(personalId);
        String encryptedUserId = this.encryptionUtil.encrypt(appUser.getId().toString());
        Patient patient = patientMapper.appUserToFhirPatient(appUser, encryptedPersonalId, encryptedUserId);
        MethodOutcome methodOutcome = this.fhirClient.update().resource(patient).execute();
        return getPatientRecordUpdateDto(methodOutcome.getResource().getIdElement().getIdPart(), encryptedUserId, false);
    }

    public ChaincodePatientRecordDto getPatientRecordUpdateDto(String recordId, String encryptedUserId, boolean existing) {
        String hashedData = this.getPatientDataHash(recordId);
        String offlineDataUrl = this.encryptionUtil.encrypt(recordId);
        return new ChaincodePatientRecordDto(offlineDataUrl, hashedData, encryptedUserId, existing);
    }

    private MyChaincodePatientRecordDto getMyPatientRecordUpdateDto(MethodOutcome methodOutcome, boolean existing) {
        String recordId = methodOutcome.getResource().getIdElement().getIdPart();
        String hashedData = this.getPatientDataHash(recordId);
        String offlineDataUrl = this.encryptionUtil.encrypt(recordId);
        return new MyChaincodePatientRecordDto(offlineDataUrl, hashedData, existing);
    }

    public String getPatientDataHash(String recordId) {
        String bundleStr = getPatientRecord(recordId);
        JsonObject jsonObject = JsonParser.parseString(bundleStr).getAsJsonObject();
        String dataStr = jsonObject.get("entry").toString();
        return HashUtil.hashData(dataStr);
    }

    public String getPatientRecord(String recordId) {
        String url = this.fhirClient.getServerBase() + "/Patient/" + recordId + "/$everything";
        Bundle bundle = this.fhirClient.search().byUrl(url).returnBundle(Bundle.class).execute();
        return fhirConfig.getFhirContext().newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle);
    }

    public Patient getPatientWithPersonalId(String personalId) {
        String encryptedId = encryptionUtil.encrypt(personalId);
        Bundle bundle = this.fhirClient.search().forResource(Patient.class).where(Patient.IDENTIFIER.exactly().systemAndValues("http://hl7.org/fhir/sid/us-ssn", encryptedId)).returnBundle(Bundle.class).execute();
        System.out.println(fhirConfig.getFhirContext().newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle));
        for (Bundle.BundleEntryComponent e : bundle.getEntry()) {
            return (Patient) e.getResource();
        }
        return null;
    }

    public void registerPractitioner(AppUser appUser, String specialtyCode) {
        Specialty specialty = this.specialtyService.getByCode(specialtyCode);
        Practitioner practitioner = practitionerMapper.appUserToFhirPractitioner(appUser, specialty);
        this.fhirClient.update().resource(practitioner).execute();
    }

    public Patient getPatient(String id) {
        String decryptedId = this.encryptionUtil.decrypt(id);
        return this.fhirClient.read().resource(Patient.class).withId(decryptedId).execute();
    }

    public Practitioner getPractitioner(String id) {
        String encryptedId = encryptionUtil.encrypt(id);
        Bundle bundle = this.fhirClient.search().forResource(Practitioner.class).where(Practitioner.IDENTIFIER.exactly().systemAndValues("http://healthscape.com", encryptedId)).returnBundle(Bundle.class).execute();
        System.out.println(fhirConfig.getFhirContext().newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle));
        for (Bundle.BundleEntryComponent e : bundle.getEntry()) {
            return (Practitioner) e.getResource();
        }
        return null;
    }

    public MyChaincodePatientRecordDto changeEmailPatient(String offlineDataUrl, String email) {
        Patient patient = getPatient(offlineDataUrl);
        for (ContactPoint telecom : patient.getTelecom()) {
            if (telecom.getSystem().equals(ContactPoint.ContactPointSystem.EMAIL)) {
                telecom.setValue(email);
            }
        }
        MethodOutcome methodOutcome = this.fhirClient.update().resource(patient).execute();
        return getMyPatientRecordUpdateDto(methodOutcome, true);
    }

    public void changeEmailPractitioner(String id, String email) {
        Practitioner practitioner = getPractitioner(id);
        for (ContactPoint telecom : practitioner.getTelecom()) {
            if (telecom.getSystem().equals(ContactPoint.ContactPointSystem.EMAIL)) {
                telecom.setValue(email);
            }
        }
        this.fhirClient.update().resource(practitioner).execute();
    }

    public MyChaincodePatientRecordDto updatePatient(FhirUserDto userDto, String offlineDataUrl) {
        Patient patient = getPatient(offlineDataUrl);
        Patient updatePatient = this.patientMapper.mapUpdatedToPatient(patient, userDto);
        MethodOutcome methodOutcome = this.fhirClient.update().resource(updatePatient).execute();
        return getMyPatientRecordUpdateDto(methodOutcome, true);
    }

    public void updatePractitioner(FhirUserDto userDto, String id) {
        Practitioner practitioner = getPractitioner(id);
        Practitioner updatePractitioner = this.practitionerMapper.mapUpdatedToPractitioner(practitioner, userDto);
        this.fhirClient.update().resource(updatePractitioner).execute();
    }
}
