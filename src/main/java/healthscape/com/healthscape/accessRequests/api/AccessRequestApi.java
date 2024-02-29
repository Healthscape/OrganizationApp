package healthscape.com.healthscape.accessRequests.api;

import healthscape.com.healthscape.accessRequests.service.AccessRequestService;
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

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('find_access_request')")
    private ResponseEntity<?> findAccessRequest(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @PathVariable String userId) {
        return ResponseEntity.ok(this.accessRequestService.findAccessRequest(token, userId));
    }

    @PostMapping("/{userId}")
    @PreAuthorize("hasAuthority('send_access_request')")
    private ResponseEntity<?> sendAccessRequest(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @PathVariable String userId) {
        return ResponseEntity.ok(this.accessRequestService.sendAccessRequest(token, userId));
    }
}
