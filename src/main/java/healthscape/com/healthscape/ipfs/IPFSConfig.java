package healthscape.com.healthscape.ipfs;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

// import healthscape.com.healthscape.util.Config;
// import io.ipfs.api.IPFS;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class IPFSConfig {

    // IPFS ipfs;

    // public IPFSConfig(){
    //     ipfs = new IPFS(Config.IPFS_URL);
    // }
    
}
