package healthscape.com.healthscape.monitoring;

import java.util.List;

import org.springframework.stereotype.Service;

import healthscape.com.healthscape.fabric.dao.AccessLogDAO;
import healthscape.com.healthscape.fabric.dao.PatientRecordDAO;
import healthscape.com.healthscape.fabric.service.FabricMonitoringService;
import healthscape.com.healthscape.fabric.service.FabricPatientRecordService;
import healthscape.com.healthscape.ipfs.IPFSService;
import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.users.service.UserService;
import healthscape.com.healthscape.util.EncryptionConfig;
import healthscape.com.healthscape.util.HashUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MonitoringService {
    
    private final UserService userService;
    private final FabricMonitoringService fabricMonitoringService;
    private final FabricPatientRecordService fabricPatientRecordService;
    private final EncryptionConfig encryptionConfig;
    private final IPFSService ipfsService;

    public List<AccessLogDAO> getAccessLog(String token) throws Exception {
        AppUser appUser = userService.getUserFromToken(token);
       return fabricMonitoringService.getAccessLog(appUser.getId().toString()).stream()
       .sorted((o1, o2) -> {
        Long date1 = Long.parseLong(o1.getTimestamp());
        Long date2 = Long.parseLong(o2.getTimestamp());
        if(date1 == date2){
            return 0;
        }

        if(date1 < date2){
            return 1;
        }

        return -1;
       })
       .collect(Collectors.toList());
    }

    
    public List<AccessLogDAO> getSecuirtyStatus(String token) throws Exception {
        AppUser appUser = userService.getUserFromToken(token);
       return fabricMonitoringService.getAccessLog(appUser.getId().toString());
    }


    public boolean verifyRecordIntegrity(String token) throws Exception {
        AppUser appUser = userService.getUserFromToken(token);
        PatientRecordDAO patientRecordDAO = fabricPatientRecordService.getMe(appUser.getId().toString());
        String encryptedPatientData = ipfsService.getJSONObject(patientRecordDAO.getOfflineDataUrl());
        String patientData = encryptionConfig.decryptIPFSData(encryptedPatientData);
        boolean isSecure = HashUtil.checkIntegrity(patientData, patientRecordDAO.getSalt(), patientRecordDAO.getHashedData());
        return isSecure;
    }
    
}
