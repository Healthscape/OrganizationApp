package healthscape.com.healthscape.ipfs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class IPFSController {

    @Autowired
    private IPFSService ipfsService;
    
    @GetMapping(value = "file/{hash}")
    public ResponseEntity<String> saveFile(@PathVariable("hash") String hash){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-type", MediaType.ALL_VALUE);
        String bytes = ipfsService.getJSONObject(hash);

        return ResponseEntity.status(HttpStatus.OK).headers(httpHeaders).body(bytes);
    } 
}
