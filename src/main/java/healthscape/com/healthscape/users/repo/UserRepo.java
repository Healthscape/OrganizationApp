package healthscape.com.healthscape.users.repo;

import healthscape.com.healthscape.users.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<AppUser, UUID> {
    AppUser findByEmail(String email);
}
