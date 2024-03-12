package healthscape.com.healthscape.accessRequests.service;

import healthscape.com.healthscape.accessRequests.dto.AccessRequestDto;
import healthscape.com.healthscape.accessRequests.dto.ReviewedAccessRequestDto;
import healthscape.com.healthscape.accessRequests.mapper.AccessRequestMapper;
import healthscape.com.healthscape.fabric.service.FabricAccessRequestService;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.users.service.UserService;
import healthscape.com.healthscape.util.EncryptionUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AccessRequestService {

    private final FabricAccessRequestService fabricAccessRequestService;
    private final UserService userService;
    private final AccessRequestMapper accessRequestMapper;

    public AccessRequestDto getAccessRequestForUser(String token, String userId) {
        AppUser appUser = userService.getUserFromToken(token);
        try {
            String accessRequestStr = fabricAccessRequestService.getAccessRequestForUser(appUser.getEmail(), userId);
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

            String accessRequestStr = fabricAccessRequestService.sendAccessRequest(appUser.getEmail(), userId);
            return accessRequestMapper.mapToAccessRequestDto(accessRequestStr);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public List<AccessRequestDto> getAccessRequestsByReviewed(String token, Boolean reviewed) {
        AppUser appUser = userService.getUserFromToken(token);
        try {
            String accessRequestStr = fabricAccessRequestService.getAccessRequestsByReviewed(appUser.getEmail(), reviewed);
            return accessRequestMapper.mapToAccessRequestsDto(accessRequestStr);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public List<AccessRequestDto> getAccessRequestsByStatus(String token, String status) {
        AppUser appUser = userService.getUserFromToken(token);
        try {
            String accessRequestStr = fabricAccessRequestService.getAccessRequestsByStatus(appUser.getEmail(), status);
            return accessRequestMapper.mapToAccessRequestsDto(accessRequestStr);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public void reviewAccessRequest(String token, ReviewedAccessRequestDto reviewedAccessRequestDto) {
        AppUser appUser = userService.getUserFromToken(token);
        try {
            fabricAccessRequestService.reviewAccessRequest(appUser.getEmail(), reviewedAccessRequestDto);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public List<AccessRequestDto> getRecentAccessRequests(String token) {
        AppUser appUser = userService.getUserFromToken(token);
        try {
            String accessRequestStr = fabricAccessRequestService.getRecentAccessRequests(appUser.getEmail());
            return accessRequestMapper.mapToAccessRequestsDto(accessRequestStr);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public List<AccessRequestDto> getAccessRequestHistory(String token, String requestId) {
        AppUser appUser = userService.getUserFromToken(token);
        try {
            String accessRequestStr = fabricAccessRequestService.getAccessRequestHistory(appUser.getEmail(), requestId);
            return accessRequestMapper.mapAccessRequestHistory(accessRequestStr);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public List<AccessRequestDto> getAllAvailableAccessRequests(String token) {
        AppUser appUser = userService.getUserFromToken(token);
        try {
            String accessRequestsStr = fabricAccessRequestService.getAllAvailableAccessRequests(appUser.getEmail());
            return accessRequestMapper.mapToAccessRequestsDto(accessRequestsStr);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }
}
