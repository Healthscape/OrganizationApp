package healthscape.com.healthscape.ipfs;

public interface FileServiceImpl {

    String saveJSONObject(String json);

    byte[] getJSONObject(String hash);
    
}
