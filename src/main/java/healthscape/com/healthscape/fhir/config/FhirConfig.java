package healthscape.com.healthscape.fhir.config;

import ca.uhn.fhir.context.FhirContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.security.KeyStore;

@Component
@Configuration
@AllArgsConstructor
@Getter
public class FhirConfig {

    private final ApplicationContext context;
    private final KeyStore keyStore;

    @Bean
    public FhirContext getFhirContext() {
        FhirContext context = FhirContext.forR4();
        context.getRestfulClientFactory().setConnectTimeout(60 * 1000);
        context.getRestfulClientFactory().setSocketTimeout(60 * 1000);
        return context;
    }
}

