package healthscape.com.healthscape.users.repo;

import healthscape.com.healthscape.users.model.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecialtyRepo  extends JpaRepository<Specialty, Integer> {
}
