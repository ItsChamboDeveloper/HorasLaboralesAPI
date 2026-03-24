package HorasLaborales.demo.Repositories.Students;

import HorasLaborales.demo.Entities.Students.StudentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentsRepository extends JpaRepository<StudentEntity, Long> {

    Page<StudentEntity> findAll(Pageable pageable);

    //Opcionales
    Optional<StudentEntity> findByFirstName(String firstName);
    Optional<StudentEntity> findByEmail(String email);
    Optional<StudentEntity> findByStudentCard(String studentCard);

    boolean existsByEmail(String email);

    boolean existsByStudentCard(String studentCard);

}
