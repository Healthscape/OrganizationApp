package healthscape.com.healthscape.monitoring;

import java.util.List;

import org.springframework.stereotype.Service;

import healthscape.com.healthscape.fabric.dao.AccessLogDAO;
import healthscape.com.healthscape.fabric.service.FabricMonitoringService;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.users.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MonitoringService {
    
    private final UserService userService;
    private final FabricMonitoringService fabricMonitoringService;

    public List<AccessLogDAO> getAccessLog(String token) throws Exception {
        AppUser appUser = userService.getUserFromToken(token);
       return fabricMonitoringService.getAccessLog(appUser.getId().toString());
    }
    
}
