package HorasLaborales.demo.Repositories.Parents;

import HorasLaborales.demo.Entities.Parents.ParentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParentRepository extends JpaRepository<ParentEntity, Long> {

    Page<ParentEntity> findAll(Pageable pageable);

    Optional<ParentEntity> findByEmail(String email);
    Optional<ParentEntity> findByDui(String dui);

    boolean existsByEmail(String email);
    boolean existsByDui(String dui);

}
