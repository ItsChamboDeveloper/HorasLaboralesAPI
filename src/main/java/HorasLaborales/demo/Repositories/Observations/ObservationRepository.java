package HorasLaborales.demo.Repositories.Observations;

import HorasLaborales.demo.Entities.Observation.ObservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObservationRepository extends JpaRepository <ObservationEntity, Long> {

    List<ObservationEntity> findByWorkOrderId_WorkOrderId(Long workOrderId);

}
