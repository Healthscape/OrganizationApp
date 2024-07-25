package healthscape.com.healthscape.users.service;

import healthscape.com.healthscape.users.model.Role;
import healthscape.com.healthscape.users.repo.RoleRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepo roleRepo;

    public Role getByName(String name) {
        return roleRepo.getByName(name);
    }

}
