package healthscape.com.healthscape.users.service;

import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.users.model.Specialty;
import healthscape.com.healthscape.users.repo.SpecialtyRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SpecialtyService {

    private final SpecialtyRepo specialtyRepo;

    public List<Specialty> getAllSpecialties() {
        return specialtyRepo.findAll();
    }
}
