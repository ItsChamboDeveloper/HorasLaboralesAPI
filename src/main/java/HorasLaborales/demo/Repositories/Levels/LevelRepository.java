package HorasLaborales.demo.Repositories.Levels;

import HorasLaborales.demo.Entities.Levels.LevelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LevelRepository extends JpaRepository <LevelEntity, Long> {

}
