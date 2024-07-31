package healthscape.com.healthscape.ipfs;

// import java.io.ByteArrayInputStream;
// import java.io.InputStream;

// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// import io.ipfs.api.IPFS;
// import io.ipfs.api.MerkleNode;
// import io.ipfs.api.NamedStreamable;
// import io.ipfs.multihash.Multihash;

@Service
public class IPFSService implements FileServiceImpl {

    @Override
    public String saveJSONObject(String json) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'saveJSONObject'");
    }

    @Override
    public byte[] getJSONObject(String hash) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getJSONObject'");
    }

    // // @Autowired
    // private IPFSConfig ipfsConfig;

    // @Override
    // public String saveJSONObject(String json) {
    //     try{
    //         InputStream stream = new ByteArrayInputStream(json.getBytes());
    //         NamedStreamable.InputStreamWrapper inputStreamWrapper = new NamedStreamable.InputStreamWrapper(stream);
    //         IPFS ipfs = ipfsConfig.ipfs;

    //         MerkleNode merkleNode = ipfs.add(inputStreamWrapper).get(0);

    //         return merkleNode.hash.toBase58();
    //     }catch(Exception e){
    //         throw new RuntimeException("Error",e);
    //     }
    // }

    // @Override
    // public byte[] getJSONObject(String hash) {
    //     try{
    //         IPFS ipfs = ipfsConfig.ipfs;
    
    //         Multihash pointer = Multihash.fromBase58(hash);
    //         byte[] contents = ipfs.cat(pointer);
    //         return contents;
    //     }catch(Exception e){
    //         throw new RuntimeException("Error",e);
    //     }
        
    // }
    
}
