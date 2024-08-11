package healthscape.com.healthscape.monitoring;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import healthscape.com.healthscape.fabric.dao.AccessLogDAO;
import healthscape.com.healthscape.shared.ResponseJson;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import lombok.RequiredArgsConstructor;

@RequestMapping(value = "/monitoring", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@CrossOrigin
@RestController
public class MonitoringApi {

    private final MonitoringService monitoringService;

    @GetMapping(value = "")
    @PreAuthorize("hasAuthority('get_access_log')")
    public ResponseEntity<?> getAccessLog(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            List<AccessLogDAO> accessLogs = this.monitoringService.getAccessLog(token);
            System.out.println(accessLogs.get(0).getAccessorName());
            return ResponseEntity.ok().body(accessLogs);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private ResponseEntity<?> handleException(Exception e) {
        return ResponseEntity.badRequest().body(new ResponseJson(400, e.getMessage()));
    }
    
}
