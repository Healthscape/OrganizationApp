package healthscape.com.healthscape.fabric.service;

import java.util.Date;
import java.util.Map;

import org.hyperledger.fabric.gateway.Contract;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import healthscape.com.healthscape.fabric.dao.IdentifiersDAO;
import healthscape.com.healthscape.fabric.util.FabricTransactionType;
import healthscape.com.healthscape.util.Config;
import healthscape.com.healthscape.util.HashUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class FabricAdminService {

    private final FabricTransactionService fabricTransactionService;
    private final ObjectMapper objectMapper;

    private final Map<FabricTransactionType, String> fabricTransactions = Map.of(
            FabricTransactionType.SUBMIT, "Submit Transaction",
            FabricTransactionType.EVALUATE, "Evaluate Transaction");

    public IdentifiersDAO userExists(String identifier) throws Exception {
        Contract contract = fabricTransactionService.getContract(Config.ADMIN_ID);
        String methodName = "UserExists";
        print(FabricTransactionType.EVALUATE, methodName);
        String result = new String(contract.evaluateTransaction(methodName, identifier));
        System.out.println(result);
        if(result.equals("")){
            return null;
        }
        return objectMapper.readValue(result, IdentifiersDAO.class);
    }

    public String updatePatientIdentifiers(IdentifiersDAO identifiersDAO) throws Exception {
        Contract contract = fabricTransactionService.getContract(Config.ADMIN_ID);
        String methodName = "UpdatePatientIdentifiers";
        print(FabricTransactionType.SUBMIT, methodName);

        byte[] result = contract.submitTransaction(
                                    methodName,
                                    identifiersDAO.getIdentifiersId(),
                                    identifiersDAO.getHashedIdentifier(),
                                    identifiersDAO.getOfflineIdentifierUrl(),
                                    identifiersDAO.getHashedIdentifiers(),
                                    identifiersDAO.getSalt(),
                                    String.valueOf(new Date().getTime()));
        return new String(result);
    }

    private void print(FabricTransactionType type, String methodName) {
        System.out.println("\n");
        System.out.println(fabricTransactions.get(type) + ": " + methodName);
    }
}
