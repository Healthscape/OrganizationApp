package healthscape.com.healthscape.fabric.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Asset {
    Integer appraisedValue;
    String assetID;
    String color;
    String owner;
    Integer size;

}
