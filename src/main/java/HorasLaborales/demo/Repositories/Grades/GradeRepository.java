package HorasLaborales.demo.Repositories.Grades;

import HorasLaborales.demo.Entities.Grades.GradeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeRepository extends JpaRepository<GradeEntity, Long> {
}
