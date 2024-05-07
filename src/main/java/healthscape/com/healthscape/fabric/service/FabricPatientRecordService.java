package healthscape.com.healthscape.fabric.service;

import healthscape.com.healthscape.fabric.dto.ChaincodePatientRecordDto;
import healthscape.com.healthscape.fabric.dto.MyChaincodePatientRecordDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.gateway.Contract;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FabricPatientRecordService {

    private final FabricTransactionService fabricTransactionService;

    public String createPatientRecord(String email, ChaincodePatientRecordDto patientRecordDto) throws Exception {
        Contract contract = fabricTransactionService.getContract(email);
        System.out.println("\n");
        System.out.println("Submit Transaction: CreatePatientRecord creates new access request if it does not exist.");
        byte[] result = contract.submitTransaction("CreatePatientRecord", patientRecordDto.getUserId(), patientRecordDto.getOfflineDataUrl(), patientRecordDto.getHashedData(), String.valueOf(new Date().getTime()));
        return new String(result);
    }

    public String updatePatientRecord(String email, ChaincodePatientRecordDto updatedPatientRecordDto) throws Exception {
        Contract contract = fabricTransactionService.getContract(email);
        System.out.println("\n");
        System.out.println("Submit Transaction: UpdatePatientRecord creates new access request if it does not exist.");
        byte[] result = contract.submitTransaction("UpdatePatientRecord", updatedPatientRecordDto.getUserId(), updatedPatientRecordDto.getHashedData(), String.valueOf(new Date().getTime()));
        return new String(result);
    }

    public String getPatientRecord(String email, String recordId) throws Exception {
        Contract contract = fabricTransactionService.getContract(email);
        System.out.println("\n");
        System.out.println("Submit Transaction: GetPatientRecord creates new access request if it does not exist.");
        byte[] result = contract.evaluateTransaction("GetPatientRecord", recordId);
        return new String(result);
    }

    public String getMyPatientRecord(String email) throws Exception {
        Contract contract = fabricTransactionService.getContract(email);
        System.out.println("\n");
        System.out.println("Submit Transaction: GetMyPatientRecord creates new access request if it does not exist.");
        byte[] result = contract.evaluateTransaction("GetMyPatientRecord");
        return new String(result);
    }

    public String updateMyPatientRecord(String email, MyChaincodePatientRecordDto updatedMyPatientRecordDto) throws Exception {
        Contract contract = fabricTransactionService.getContract(email);
        System.out.println("\n");
        System.out.println("Submit Transaction: UpdateMyPatientRecord creates new access request if it does not exist.");
        byte[] result = contract.submitTransaction("UpdateMyPatientRecord", updatedMyPatientRecordDto.getHashedData(), String.valueOf(new Date().getTime()));
        return new String(result);
    }

    public String previewPatientRecord(String email) throws Exception {
        Contract contract = fabricTransactionService.getContract(email);
        System.out.println("\n");
        System.out.println("Submit Transaction: PreviewPatientRecord creates new access request if it does not exist.");
        byte[] result = contract.evaluateTransaction("PreviewPatientRecord");
        return new String(result);
    }
}
