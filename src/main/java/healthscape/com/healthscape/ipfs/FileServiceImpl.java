package healthscape.com.healthscape.ipfs;

public interface FileServiceImpl {

    String saveJSONObject(String json);
    String getJSONObject(String hash);
    
}
