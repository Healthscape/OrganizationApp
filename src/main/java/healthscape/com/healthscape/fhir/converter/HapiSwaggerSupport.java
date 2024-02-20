package healthscape.com.healthscape.fhir.converter;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.introspect.*;
import com.fasterxml.jackson.databind.type.SimpleType;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Meta;

import java.lang.reflect.Method;
import java.util.Iterator;

public class HapiSwaggerSupport implements ModelConverter {

    // For HAPI resources, only inspect non-private fields - ignore getters/setters
    private static class HapiVisibilityChecker extends VisibilityChecker.Std {

        public HapiVisibilityChecker() {
            super(
                    JsonAutoDetect.Visibility.PUBLIC_ONLY, // getter
                    JsonAutoDetect.Visibility.PUBLIC_ONLY, // is-getter
                    JsonAutoDetect.Visibility.PUBLIC_ONLY, // setter
                    JsonAutoDetect.Visibility.ANY, // creator -- legacy, to support single-arg ctors
                    JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC // field

            );
        }

        @Override
        public boolean isGetterVisible(Method m) {
            return isNotHapiClass(m.getDeclaringClass());
        }

        @Override
        public boolean isIsGetterVisible(Method m) {
            return isNotHapiClass(m.getDeclaringClass());
        }

        @Override
        public boolean isSetterVisible(Method m) {
            return isNotHapiClass(m.getDeclaringClass());
        }

        private boolean isNotHapiClass(Class<?> clazz) {
            return !clazz.getPackage().getName().startsWith("org.hl7.fhir");
        }
    }

    public HapiSwaggerSupport() {
        Json.mapper()
                .setVisibility(new HapiVisibilityChecker());
    }

    @Override
    public Schema<?> resolve(AnnotatedType annotatedType, ModelConverterContext modelConverterContext, Iterator<ModelConverter> chain) {

        // Keep schema's manageable by truncating less relevant fields
        if (isTruncated(annotatedType)) {
            return new ObjectSchema()
                    .format(
                            ((SimpleType) annotatedType.getType())
                                    .getRawClass()
                                    .getSimpleName()
                    );
        }
        return (chain.hasNext())
                ? chain.next().resolve(annotatedType, modelConverterContext, chain)
                : null;
    }

    private static boolean isTruncated(AnnotatedType annotatedType) {
        return annotatedType.getType() instanceof SimpleType && (
                Meta.class.isAssignableFrom(((SimpleType) annotatedType.getType()).getRawClass()) ||
                        IdType.class.isAssignableFrom(((SimpleType) annotatedType.getType()).getRawClass()) ||
                        Extension.class.isAssignableFrom(((SimpleType) annotatedType.getType()).getRawClass())
        );
    }

}