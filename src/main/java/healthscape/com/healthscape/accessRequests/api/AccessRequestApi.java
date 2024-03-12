package healthscape.com.healthscape.accessRequests.api;

import healthscape.com.healthscape.accessRequests.dto.ReviewedAccessRequestDto;
import healthscape.com.healthscape.accessRequests.service.AccessRequestService;
import healthscape.com.healthscape.shared.ResponseJson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/accessRequests", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class AccessRequestApi {

    private final AccessRequestService accessRequestService;

    @PostMapping("/{userId}")
    @PreAuthorize("hasAuthority('send_access_request')")
    public ResponseEntity<?> sendAccessRequest(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @PathVariable String userId) {
        return ResponseEntity.ok(accessRequestService.sendAccessRequest(token, userId));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('get_access_request_for_user')")
    public ResponseEntity<?> getAccessRequestForUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @PathVariable String userId) {
        return ResponseEntity.ok(accessRequestService.getAccessRequestForUser(token, userId));
    }

    @GetMapping(value = "", params = {"review"})
    @PreAuthorize("hasAuthority('get_access_requests_by_reviewed')")
    public ResponseEntity<?> getAccessRequestsByReviewed(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestParam Boolean reviewed) {
        return ResponseEntity.ok(accessRequestService.getAccessRequestsByReviewed(token, reviewed));
    }

    @GetMapping(value = "", params = {"status"})
    @PreAuthorize("hasAuthority('get_access_requests_by_status')")
    public ResponseEntity<?> getAccessRequestsByStatus(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestParam String status) {
        return ResponseEntity.ok(accessRequestService.getAccessRequestsByStatus(token, status));
    }

    @GetMapping("/recent")
    @PreAuthorize("hasAuthority('get_recent_requests')")
    public ResponseEntity<?> getRecentRequests(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return ResponseEntity.ok(accessRequestService.getRecentAccessRequests(token));
    }

    @GetMapping("/{requestId}/history")
    @PreAuthorize("hasAuthority('get_request_history')")
    public ResponseEntity<?> getRequestHistory(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @PathVariable String requestId) {
        return ResponseEntity.ok(accessRequestService.getAccessRequestHistory(token, requestId));
    }

    @PutMapping("")
    @PreAuthorize("hasAuthority('review_access_request')")
    public ResponseEntity<?> reviewAccessRequest(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestBody ReviewedAccessRequestDto reviewedAccessRequestDto) {
        accessRequestService.reviewAccessRequest(token, reviewedAccessRequestDto);
        return ResponseEntity.ok().body(ResponseEntity.ok().body(new ResponseJson(200, "OK")));
    }
}
