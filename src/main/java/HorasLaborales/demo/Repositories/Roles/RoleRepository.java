package HorasLaborales.demo.Repositories.Roles;

import HorasLaborales.demo.Entities.Roles.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {



}
