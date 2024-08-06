package healthscape.com.healthscape.ipfs;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.springframework.stereotype.Service;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class IPFSService implements FileServiceImpl {

    private final IPFSConfig ipfsConfig;

    @Override
    public String saveJSONObject(String json) {
        try{
            InputStream stream = new ByteArrayInputStream(json.getBytes());
            NamedStreamable.InputStreamWrapper inputStreamWrapper = new NamedStreamable.InputStreamWrapper(stream);
            IPFS ipfs = ipfsConfig.ipfs;

            MerkleNode merkleNode = ipfs.add(inputStreamWrapper).get(0);

            return merkleNode.hash.toBase58();
        }catch(Exception e){
            throw new RuntimeException("Error",e);
        }
    }

    public String getJSONObject(String hash) {
        try{
            IPFS ipfs = ipfsConfig.ipfs;
    
            Multihash pointer = Multihash.fromBase58(hash);
            byte[] contents = ipfs.cat(pointer);
            return new String(contents);
        }catch(Exception e){
            throw new RuntimeException("Error",e);
        }
        
    }
    
}
