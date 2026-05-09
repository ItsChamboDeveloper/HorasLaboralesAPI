package HorasLaborales.demo.Repositories.Auth.StudentsAuth;

import HorasLaborales.demo.Entities.Students.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface StudentAuthenticationRepository extends JpaRepository<StudentEntity, Long> {
    StudentEntity findByEmail(String email);
}
