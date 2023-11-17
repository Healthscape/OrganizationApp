package healthscape.com.healthscape.users.repo;

import healthscape.com.healthscape.users.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepo extends JpaRepository<Role, Integer> {

    Role getByName(String name);
}
