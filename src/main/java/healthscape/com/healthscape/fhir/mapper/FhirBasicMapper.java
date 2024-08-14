package healthscape.com.healthscape.fhir.mapper;

import org.hl7.fhir.instance.model.api.IBase;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.parser.IParser;
import healthscape.com.healthscape.fhir.config.FhirConfig;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class FhirBasicMapper {
    
    private final FhirConfig fhirConfig;

    public <T extends Resource> T parseJSON(String data, Class<T> resourceClass) {
        IParser parser = fhirConfig.getFhirContext().newJsonParser();
        return parser.parseResource(resourceClass, data);
    }

    public <T extends IBaseResource> String toJSON(T resource, Class<T> resourceClass) {
        IParser parser = fhirConfig.getFhirContext().newJsonParser();
        return parser.encodeResourceToString(resource);
    }

    public <T extends IBase> String toJSON(T resource, Class<T> resourceClass) {
        IParser parser = fhirConfig.getFhirContext().newJsonParser();
        return parser.encodeToString(resource);
    }
}
