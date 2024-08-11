package healthscape.com.healthscape.fabric.dao;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FabricItemDAO<T> {
    
    @JsonProperty("Key")
    private String Key;
    
    @JsonProperty("Record")
    private T Record;
    
}
