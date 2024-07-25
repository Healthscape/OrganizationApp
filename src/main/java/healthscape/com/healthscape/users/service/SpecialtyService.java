package healthscape.com.healthscape.users.service;

import healthscape.com.healthscape.users.model.Specialty;
import healthscape.com.healthscape.users.repo.SpecialtyRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SpecialtyService {

    private final SpecialtyRepo specialtyRepo;

    public List<Specialty> getAllSpecialties() {
        return specialtyRepo.findAll();
    }

    public Specialty getByCode(String specialtyCode) {
        return specialtyRepo.findByCode(specialtyCode);
    }
}
