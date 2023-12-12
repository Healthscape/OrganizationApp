package healthscape.com.healthscape.fabric.controller;

import healthscape.com.healthscape.fabric.service.FabricService;
import healthscape.com.healthscape.util.Config;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/fabric", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class FabricController {

    private final FabricService fabricService;

    @GetMapping("")
    public ResponseEntity<?> run() {

        try {
            fabricService.run();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return ResponseEntity.ok().body("OK");

    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        System.out.println(Config.CERT_PATH);
        System.out.println(Config.PEER_ENDPOINT);
        System.out.println("Unutar kontejnera sam");
        return ResponseEntity.ok().body("OK");

    }
}
