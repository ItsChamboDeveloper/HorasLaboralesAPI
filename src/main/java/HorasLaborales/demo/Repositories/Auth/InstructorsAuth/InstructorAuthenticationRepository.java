package HorasLaborales.demo.Repositories.Auth.InstructorsAuth;

import HorasLaborales.demo.Entities.Instructors.InstructorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface InstructorAuthenticationRepository  extends JpaRepository<InstructorEntity, Long> {
    InstructorEntity findByEmail(String email);
}
