package HorasLaborales.demo.Repositories.Instructors;

import HorasLaborales.demo.Entities.Instructors.InstructorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface InstructorRepository extends JpaRepository<InstructorEntity, Long> {

    Page<InstructorEntity> findAll(Pageable pageable);

    //Opcionales
    Optional<InstructorEntity> findByFirstName(String firstName);
    Optional<InstructorEntity> findByEmail(String email);

    boolean existsByFirstName(String firstName);
    boolean existsByEmail(String email);

    @Query("SELECT i.email FROM InstructorEntity i WHERE i.roleId.roleId = :roleId")
    List<String> findEmailByRolId(@Param("roleId") Long roleId);

}
