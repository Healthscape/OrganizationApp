package healthscape.com.healthscape.fhir.config;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import healthscape.com.healthscape.util.Config;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

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

    // @Bean
    // public IGenericClient getFhirClient(FhirContext fhirContext) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
    //     SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(keyStore, new TrustSelfSignedStrategy()).build();

    //     HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
    //     SSLConnectionSocketFactory sslFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);

    //     CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslFactory).build();
    //     fhirContext.getRestfulClientFactory().setHttpClient(httpClient);

    //     return fhirContext.newRestfulGenericClient(Config.FHIR_SERVER);
    // }
}

