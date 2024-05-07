package healthscape.com.healthscape.fabric.service;

import com.google.gson.Gson;
import healthscape.com.healthscape.accessRequests.dto.ReviewedAccessRequestDto;
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
public class FabricAccessRequestService {

    private final FabricTransactionService fabricTransactionService;
    private final Gson gson;

    public String getAccessRequestForUser(String email, String userId) throws Exception {
        Contract contract = fabricTransactionService.getContract(email);
        System.out.println("\n");
        System.out.println("Evaluate Transaction: GetAccessRequestForUser returns access request if it exists.");
        byte[] result = contract.evaluateTransaction("GetAccessRequestForUser", userId);
        return new String(result);
    }

    public String sendAccessRequest(String email, String userId) throws Exception {
        Contract contract = fabricTransactionService.getContract(email);
        System.out.println("\n");
        System.out.println("Submit Transaction: CreateAccessRequest creates new access request if it does not exist.");
        byte[] result = contract.submitTransaction("CreateAccessRequest", userId, String.valueOf(new Date().getTime()));
        return new String(result);
    }

    public String getAccessRequestsByReviewed(String email, Boolean reviewed) throws Exception {
        Contract contract = fabricTransactionService.getContract(email);
        System.out.println("\n");
        System.out.println("Submit Transaction: GetAccessRequestsByReviewed creates new access request if it does not exist.");
        byte[] result = contract.evaluateTransaction("GetAccessRequestsByReviewed", String.valueOf(reviewed));
        return new String(result);
    }

    public String getAccessRequestsByStatus(String email, String status) throws Exception {
        Contract contract = fabricTransactionService.getContract(email);
        System.out.println("\n");
        System.out.println("Submit Transaction: GetAccessRequestsByStatus creates new access request if it does not exist.");
        byte[] result = contract.evaluateTransaction("GetAccessRequestsByStatus", status);
        return new String(result);
    }

    public void reviewAccessRequest(String email, ReviewedAccessRequestDto reviewedAccessRequestDto) throws Exception {
        Contract contract = fabricTransactionService.getContract(email);
        System.out.println("\n");
        System.out.println("Submit Transaction: ReviewAccessRequest creates new access request if it does not exist.");
        contract.submitTransaction("ReviewAccessRequest", reviewedAccessRequestDto.getRequestId(), reviewedAccessRequestDto.getDecision(), reviewedAccessRequestDto.getAvailableFrom(), reviewedAccessRequestDto.getAvailableUntil(), gson.toJson(reviewedAccessRequestDto.getItemsAccess()), String.valueOf(new Date().getTime()));
    }

    public String getRecentAccessRequests(String email) throws Exception {
        Contract contract = fabricTransactionService.getContract(email);
        System.out.println("\n");
        System.out.println("Submit Transaction: GetRecentAccessRequests creates new access request if it does not exist.");
        byte[] result = contract.evaluateTransaction("GetRecentAccessRequests");
        return new String(result);
    }

    public String getAccessRequestHistory(String email, String requestId) throws Exception {
        Contract contract = fabricTransactionService.getContract(email);
        System.out.println("\n");
        System.out.println("Submit Transaction: GetAccessRequestHistory creates new access request if it does not exist.");
        byte[] result = contract.evaluateTransaction("GetAccessRequestHistory", requestId);
        return new String(result);
    }

    public String getAllAvailableAccessRequests(String email) throws Exception {
        Contract contract = fabricTransactionService.getContract(email);
        System.out.println("\n");
        System.out.println("Submit Transaction: GetAllAvailableAccessRequests creates new access request if it does not exist.");
        byte[] result = contract.evaluateTransaction("GetAllAvailableAccessRequests");
        return new String(result);
    }

    public String isAccessRequestApproved(String email, String requestId) throws Exception {
        Contract contract = fabricTransactionService.getContract(email);
        System.out.println("\n");
        System.out.println("Submit Transaction: IsAccessRequestApproved creates new access request if it does not exist.");
        byte[] result = contract.evaluateTransaction("IsAccessRequestApproved", requestId);
        return new String(result);
    }
}
