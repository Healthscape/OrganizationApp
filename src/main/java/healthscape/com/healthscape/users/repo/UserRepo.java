package healthscape.com.healthscape.users.repo;

import healthscape.com.healthscape.users.model.AppUser;
import healthscape.com.healthscape.users.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<AppUser, UUID> {
    AppUser findByEmail(String email);
    AppUser findAppUserByRole(Role role);
}
