package HorasLaborales.demo.Repositories.Auth.ParentsAuth;

import HorasLaborales.demo.Entities.Parents.ParentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParentAuthenticationRepository extends JpaRepository<ParentEntity, Long> {
    ParentEntity findByEmail(String email);
}
