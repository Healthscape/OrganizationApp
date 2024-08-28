package healthscape.com.healthscape.util;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.users.model.Role;
import healthscape.com.healthscape.users.repo.UserRepo;
import healthscape.com.healthscape.users.service.RoleService;
import lombok.AllArgsConstructor;

import java.util.*;

@Component
@AllArgsConstructor
public class CustomApplicationRunner implements ApplicationRunner {

    private final UserRepo userRepo;
    private final RoleService roleService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Role patient = roleService.getByName("ROLE_PATIENT");
        Role practitioner = roleService.getByName("ROLE_PRACTITIONER");
        
        List<AppUser> allUsers = new ArrayList<AppUser>();

        AppUser patient1 = new AppUser();
        patient1.setName("Lea");
        patient1.setSurname("Kalmar");
        patient1.setEmail("lea@gmail.com");
        patient1.setPassword("$2a$10$W7nm2gSkUtyAsIRF0W6odOrLUEawSXE3KsXz45Gc656BujQpxkH3e");
        patient1.setImagePath("patient-default.png");
        patient1.setRole(patient);
        allUsers.add(patient1);

        AppUser patient2 = new AppUser();
        patient2.setName("Jeremy");
        patient2.setSurname("Simon");
        patient2.setEmail("jeremy@gmail.com");
        patient2.setPassword("$2a$10$W7nm2gSkUtyAsIRF0W6odOrLUEawSXE3KsXz45Gc656BujQpxkH3e");
        patient2.setImagePath("patient-default.png");
        patient2.setRole(patient);
        allUsers.add(patient2);

        AppUser patient3 = new AppUser();
        patient3.setName("Randolph");
        patient3.setSurname("Jackson");
        patient3.setEmail("randolph@gmail.com");
        patient3.setPassword("$2a$10$W7nm2gSkUtyAsIRF0W6odOrLUEawSXE3KsXz45Gc656BujQpxkH3e");
        patient3.setImagePath("patient-default.png");
        patient3.setRole(patient);
        allUsers.add(patient3);

        AppUser patient4 = new AppUser();
        patient4.setName("Eva");
        patient4.setSurname("Barrett");
        patient4.setEmail("eva@gmail.com");
        patient4.setPassword("$2a$10$W7nm2gSkUtyAsIRF0W6odOrLUEawSXE3KsXz45Gc656BujQpxkH3e");
        patient4.setImagePath("patient-default.png");
        patient4.setRole(patient);
        allUsers.add(patient4);

        AppUser patient5 = new AppUser();
        patient5.setName("Emily");
        patient5.setSurname("Banks");
        patient5.setEmail("emily@gmail.com");
        patient5.setPassword("$2a$10$W7nm2gSkUtyAsIRF0W6odOrLUEawSXE3KsXz45Gc656BujQpxkH3e");
        patient5.setImagePath("patient-default.png");
        patient5.setRole(patient);
        allUsers.add(patient5);

        AppUser practitioner1 = new AppUser();
        practitioner1.setName("John");
        practitioner1.setSurname("Doe");
        practitioner1.setEmail("john@gmail.com");
        practitioner1.setPassword("$2a$10$W7nm2gSkUtyAsIRF0W6odOrLUEawSXE3KsXz45Gc656BujQpxkH3e");
        practitioner1.setImagePath("practitioner-default.png");
        practitioner1.setSpecialty("NEURO");
        practitioner1.setRole(practitioner);
        allUsers.add(practitioner1);

        AppUser practitioner2 = new AppUser();
        practitioner2.setName("Melanie");
        practitioner2.setSurname("Reed");
        practitioner2.setEmail("melanie@gmail.com");
        practitioner2.setPassword("$2a$10$W7nm2gSkUtyAsIRF0W6odOrLUEawSXE3KsXz45Gc656BujQpxkH3e");
        practitioner2.setImagePath("practitioner-default.png");
        practitioner2.setSpecialty("PEDS");
        practitioner2.setRole(practitioner);
        allUsers.add(practitioner2);

        for(AppUser user: allUsers){
            userRepo.save(user);
        }
    }
}
