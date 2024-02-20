package healthscape.com.healthscape.fhir.converter;

import ca.uhn.fhir.context.FhirContext;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractGenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;

public class HapiHttpMessageConverter extends AbstractGenericHttpMessageConverter<IBaseResource> {
    private final FhirContext fhirContext;

    public HapiHttpMessageConverter(FhirContext fhirContext) {
        super(MediaType.APPLICATION_JSON, new MediaType("application", "*+json"));
        this.fhirContext = fhirContext;
    }

    @Override
    protected boolean supports(@NonNull Class<?> clazz) {
        return IBaseResource.class.isAssignableFrom(clazz);
    }

    @Override
    @NonNull
    protected IBaseResource readInternal(@NonNull Class<? extends IBaseResource> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        return fhirContext.newJsonParser().parseResource(inputMessage.getBody());
    }

    @Override
    protected void writeInternal(@NonNull IBaseResource iBaseResource, Type type, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        fhirContext.newJsonParser().encodeResourceToWriter(iBaseResource, new OutputStreamWriter(outputMessage.getBody()));
    }

    @Override
    @NonNull
    public IBaseResource read(@NonNull Type type, Class<?> contextClass, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        return fhirContext.newJsonParser().parseResource(inputMessage.getBody());
    }

}