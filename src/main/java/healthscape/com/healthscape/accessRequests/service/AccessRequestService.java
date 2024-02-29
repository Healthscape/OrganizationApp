package healthscape.com.healthscape.accessRequests.service;

import healthscape.com.healthscape.accessRequests.dto.AccessRequestDto;
import healthscape.com.healthscape.accessRequests.mapper.AccessRequestMapper;
import healthscape.com.healthscape.fabric.service.FabricTransactionService;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.users.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AccessRequestService {

    private final FabricTransactionService fabricTransactionService;
    private final UserService userService;
    private final AccessRequestMapper accessRequestMapper;


    public AccessRequestDto findAccessRequest(String token, String userId) {
        AppUser appUser = userService.getUserFromToken(token);
        try {
            String accessRequestStr = fabricTransactionService.getAccessRequest(appUser.getEmail(), userId);
            return accessRequestMapper.mapToAccessRequestDto(accessRequestStr);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public AccessRequestDto sendAccessRequest(String token, String userId) {
        AppUser appUser = userService.getUserFromToken(token);
        try {
            AppUser patient = userService.getUserById(userId);
            if (patient == null) {
                throw new Exception("User not found!");
            }

            if (!patient.getRole().getName().equals("ROLE_PATIENT")) {
                throw new Exception("Inadequate role for sending access request!");
            }

            String accessRequestStr = fabricTransactionService.sendAccessRequest(appUser.getEmail(), userId);
            return accessRequestMapper.mapToAccessRequestDto(accessRequestStr);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }
}
