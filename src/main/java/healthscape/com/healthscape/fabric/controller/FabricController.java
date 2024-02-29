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


    @GetMapping("/test")
    public ResponseEntity<?> test(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            String email = tokenUtils.getEmailFromToken(token);
            fabricTransactionService.test(email);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return ResponseEntity.ok().body("OK");

    }

    @GetMapping("/init")
    public ResponseEntity<?> initLedger(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        try {
            String email = tokenUtils.getEmailFromToken(token);
            fabricTransactionService.initLedger(email);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return ResponseEntity.ok().body("OK");

    }

    @GetMapping("/getAssetsByRange")
    public ResponseEntity<?> getAssetsByRange(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        try {
            String email = tokenUtils.getEmailFromToken(token);
            fabricTransactionService.getAssetsByRange(email);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return ResponseEntity.ok().body("OK");

    }

    @GetMapping("/createAsset")
    public ResponseEntity<?> createAsset(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestParam String id) {

        try {
            String email = tokenUtils.getEmailFromToken(token);
            fabricTransactionService.createAsset(email, id);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return ResponseEntity.ok().body("OK");

    }

    @GetMapping("/readAsset")
    public ResponseEntity<?> readAsset(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestParam String id) {

        try {
            String email = tokenUtils.getEmailFromToken(token);
            fabricTransactionService.readAsset(email, id);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return ResponseEntity.ok().body("OK");

    }

    @GetMapping("/assetExists")
    public ResponseEntity<?> assetExists(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestParam String id) {

        try {
            String email = tokenUtils.getEmailFromToken(token);
            fabricTransactionService.assetExists(email, id);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return ResponseEntity.ok().body("OK");

    }

    @GetMapping("/deleteAsset")
    public ResponseEntity<?> deleteAsset(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestParam String id) {

        try {
            String email = tokenUtils.getEmailFromToken(token);
            fabricTransactionService.deleteAsset(email, id);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return ResponseEntity.ok().body("OK");

    }

    @GetMapping("/transferAsset")
    public ResponseEntity<?> transferAsset(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestParam String id, @RequestParam String newOwner) {

        try {
            String email = tokenUtils.getEmailFromToken(token);
            fabricTransactionService.transferAsset(email, id, newOwner);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return ResponseEntity.ok().body("OK");

    }

    @GetMapping("/transferAssetByColor")
    public ResponseEntity<?> transferAssetByColor(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestParam String color, @RequestParam String newOwner) {

        try {
            String email = tokenUtils.getEmailFromToken(token);
            fabricTransactionService.transferAssetByColor(email, color, newOwner);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return ResponseEntity.ok().body("OK");

    }

    @GetMapping("/queryAssetsByOwner")
    public ResponseEntity<?> queryAssetsByOwner(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestParam String owner) {

        try {
            String email = tokenUtils.getEmailFromToken(token);
            fabricTransactionService.queryAssetsByOwner(email, owner);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return ResponseEntity.ok().body("OK");

    }

    @GetMapping("/getAssetHistory")
    public ResponseEntity<?> getAssetHistory(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestParam String id) {

        try {
            String email = tokenUtils.getEmailFromToken(token);
            fabricTransactionService.getAssetHistory(email, id);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return ResponseEntity.ok().body("OK");

    }

    @GetMapping("/queryAssets")
    public ResponseEntity<?> queryAssets(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        try {
            String email = tokenUtils.getEmailFromToken(token);
            fabricTransactionService.queryAssets(email);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return ResponseEntity.ok().body("OK");

    }
}
