package healthscape.com.healthscape;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ca.uhn.fhir.context.FhirContext;

@SpringBootApplication
@EnableWebSecurity
public class HealthscapeApplication {

    @Bean
    public FhirContext fhirContext() {
        return FhirContext.forR4();
    }
    public static void main(String[] args) {
        SpringApplication.run(HealthscapeApplication.class, args);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
