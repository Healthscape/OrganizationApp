package healthscape.com.healthscape.fabric.service;

import healthscape.com.healthscape.fabric.dao.IdentifiersDAO;
import healthscape.com.healthscape.fabric.dao.PatientRecordDAO;
import healthscape.com.healthscape.fabric.dto.ChaincodePatientRecordDto;
import healthscape.com.healthscape.fabric.dto.MyChaincodePatientRecordDto;
import healthscape.com.healthscape.fabric.util.FabricTransactionType;
import healthscape.com.healthscape.fhir.dtos.NewPatientRecordDTO;
import healthscape.com.healthscape.ipfs.IPFSService;
import healthscape.com.healthscape.patient.service.PatientService;
import healthscape.com.healthscape.util.Config;
import healthscape.com.healthscape.util.EncryptionConfig;
import healthscape.com.healthscape.util.HashUtil;
import healthscape.com.healthscape.util.HashWithSalt;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.hyperledger.fabric.gateway.Contract;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FabricPatientRecordService {

    private final FabricTransactionService fabricTransactionService;
    private final EncryptionConfig encryptionConfig;
    private final ObjectMapper objectMapper;

    private final Map<FabricTransactionType, String> fabricTransactions = Map.of(
            FabricTransactionType.SUBMIT, "Submit Transaction",
            FabricTransactionType.EVALUATE, "Evaluate Transaction");

    public String createPatientRecord(String userId, PatientRecordDAO patientRecordDAO, IdentifiersDAO identifiersDAO) throws Exception {
        Contract contract = fabricTransactionService.getContract(userId);
        String methodName = "CreatePatientRecord";
        print(FabricTransactionType.SUBMIT, methodName);

        String hashedUserId = HashUtil.hashData(userId);
        byte[] result = contract.submitTransaction(
                                    methodName,
                                    patientRecordDAO.getHashedIdentifier(),
                                    hashedUserId,
                                    patientRecordDAO.getOfflineDataUrl(),
                                    patientRecordDAO.getHashedData(),
                                    patientRecordDAO.getSalt(),
                                    identifiersDAO.getOfflineIdentifierUrl(),
                                    identifiersDAO.getHashedIdentifiers(),
                                    identifiersDAO.getSalt(),
                                    String.valueOf(new Date().getTime()));

        PatientRecordDAO savedPatientRecordDAO = objectMapper.readValue(new String(result), PatientRecordDAO.class);
        return encryptionConfig.encryptDefaultData(savedPatientRecordDAO.getOfflineDataUrl());
    }

    public String getMe(String userId) throws Exception {
        Contract contract = fabricTransactionService.getContract(userId);
        String methodName = "GetMyPatientRecord";
        print(FabricTransactionType.EVALUATE, methodName);
        byte[] result = contract.evaluateTransaction(
                                    methodName,
                                    String.valueOf(new Date().getTime()));
        PatientRecordDAO patientRecordDAO = objectMapper.readValue(new String(result), PatientRecordDAO.class);
        return patientRecordDAO.getOfflineDataUrl();
    }

    public String updateMyPatientRecord(String userId, PatientRecordDAO updatedPatient)
            throws Exception {
        Contract contract = fabricTransactionService.getContract(userId);
        String methodName = "UpdateMyPatientRecord";
        print(FabricTransactionType.EVALUATE, methodName);
        byte[] result = contract.submitTransaction(
                                    methodName,
                                    updatedPatient.getOfflineDataUrl(),
                                    updatedPatient.getHashedData(),
                                    updatedPatient.getSalt(),
                                    String.valueOf(new Date().getTime()));
        return new String(result);
    }

    public String getPatientRecord(String userId, String hashedPatientUserId) throws Exception {
        Contract contract = fabricTransactionService.getContract(userId);
        String methodName = "GetPatientRecord";
        print(FabricTransactionType.SUBMIT, methodName);
        byte[] result = contract.submitTransaction(
                                    methodName, 
                                    hashedPatientUserId,
                                    String.valueOf(new Date().getTime()));
        PatientRecordDAO patientRecordDAO = objectMapper.readValue(new String(result), PatientRecordDAO.class);
        return patientRecordDAO.getOfflineDataUrl();
    }

    // public String updatePatientRecord(String userId, PatientRecordDAO updatedPatient)
    //         throws Exception {
    //     Contract contract = fabricTransactionService.getContract(userId);
    //     String methodName = "UpdatePatientRecord";
    //     print(FabricTransactionType.EVALUATE, methodName);
    //     byte[] result = contract.submitTransaction(
    //                                 methodName,
    //                                 hashedUserId,
    //                                 updatedPatient.getOfflineDataUrl(),
    //                                 updatedPatient.getHashedData(),
    //                                 updatedPatient.getSalt(),
    //                                 String.valueOf(new Date().getTime()));
    //     return new String(result);
    // }

    // public String previewPatientRecord(String email) throws Exception {
    //     Contract contract = fabricTransactionService.getContract(email);
    //     String methodName = "PreviewPatientRecord";
    //     print(FabricTransactionType.EVALUATE, methodName);
    //     byte[] result = contract.evaluateTransaction(methodName);
    //     return new String(result);
    // }

    private void print(FabricTransactionType type, String methodName) {
        System.out.println("\n");
        System.out.println(fabricTransactions.get(type) + ": " + methodName);
    }
}
