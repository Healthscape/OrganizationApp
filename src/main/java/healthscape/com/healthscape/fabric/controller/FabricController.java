package healthscape.com.healthscape.fabric.controller;

import healthscape.com.healthscape.fabric.service.FabricTransactionService;
import healthscape.com.healthscape.fabric.service.FabricUserService;
import healthscape.com.healthscape.security.util.TokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/fabric", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class FabricController {

    private final FabricUserService fabricUserService;
    private final FabricTransactionService fabricTransactionService;
    private final TokenUtils tokenUtils;

    @GetMapping("")
    public ResponseEntity<?> run(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        try {
            String email = tokenUtils.getEmailFromToken(token);
            fabricTransactionService.run(email);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return ResponseEntity.ok().body("OK");

    }
}
