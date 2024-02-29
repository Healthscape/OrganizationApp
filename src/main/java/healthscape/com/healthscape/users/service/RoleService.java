package healthscape.com.healthscape.users.service;

import healthscape.com.healthscape.users.model.Role;
import healthscape.com.healthscape.users.repo.RoleRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RoleService {

    private final RoleRepo roleRepo;

    public Role getByName(String name) {
        return roleRepo.getByName(name);
    }

}
