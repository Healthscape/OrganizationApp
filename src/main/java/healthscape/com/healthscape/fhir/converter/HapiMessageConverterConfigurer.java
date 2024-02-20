package healthscape.com.healthscape.fhir.converter;

import ca.uhn.fhir.context.FhirContext;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
public class HapiMessageConverterConfigurer implements WebMvcConfigurer {
    private final FhirContext fhirContext;

    public HapiMessageConverterConfigurer(FhirContext fhirContext) {
        this.fhirContext = fhirContext;
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        messageConverters.add(0, new HapiHttpMessageConverter(this.fhirContext));
    }
}