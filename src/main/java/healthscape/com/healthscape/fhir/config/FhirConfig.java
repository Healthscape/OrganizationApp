package healthscape.com.healthscape.fhir.config;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import lombok.AllArgsConstructor;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Component
@AllArgsConstructor
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

    @Bean
    public IGenericClient getFhirClient(FhirContext fhirContext) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
//        IGenericClient fhirClient = fhirContext.newRestfulGenericClient("https://127.0.0.1:443/fhir");
//        fhirClient.registerInterceptor(customClientInterceptor);

//        KeyStore truststore = null;
//        KeyStore truststore = KeyStore.getInstance("JKS");
//        truststore.load(new FileInputStream("healthscape.p12"), "healthscape");
        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(keyStore, new TrustSelfSignedStrategy()).build();

        HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
        SSLConnectionSocketFactory sslFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);

        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslFactory).build();
        fhirContext.getRestfulClientFactory().setHttpClient(httpClient);

        IGenericClient fhirClient = fhirContext.newRestfulGenericClient("https://127.0.0.1:443/fhir");
        return fhirClient;
    }
}

