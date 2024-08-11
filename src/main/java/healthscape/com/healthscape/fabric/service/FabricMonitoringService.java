package healthscape.com.healthscape.fabric.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hyperledger.fabric.gateway.Contract;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import healthscape.com.healthscape.fabric.dao.AccessLogDAO;
import healthscape.com.healthscape.fabric.dao.FabricItemDAO;
import healthscape.com.healthscape.fabric.util.FabricTransactionType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class FabricMonitoringService {

    private final FabricTransactionService fabricTransactionService;
    private final ObjectMapper objectMapper;

    private final Map<FabricTransactionType, String> fabricTransactions = Map.of(
            FabricTransactionType.SUBMIT, "Submit Transaction",
            FabricTransactionType.EVALUATE, "Evaluate Transaction");
    

    public List<AccessLogDAO> getAccessLog(String userId) throws Exception {
        Contract contract = fabricTransactionService.getContract(userId);
        String methodName = "GetAccessLog";
        print(FabricTransactionType.EVALUATE, methodName);
        String result = new String(contract.evaluateTransaction(methodName));
        System.out.println(result);
        if(result.equals("")){
            return null;
        }
        List<FabricItemDAO<AccessLogDAO>> list = objectMapper.readValue(result, new TypeReference<List<FabricItemDAO<AccessLogDAO>>>() {});
        List<AccessLogDAO> accessLogs = list.stream().map(FabricItemDAO::getRecord).collect(Collectors.toList());
        return accessLogs;
    }

    private void print(FabricTransactionType type, String methodName) {
        System.out.println("\n");
        System.out.println(fabricTransactions.get(type) + ": " + methodName);
    }
}
