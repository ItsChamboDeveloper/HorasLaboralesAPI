package HorasLaborales.demo.Repositories.VehicleType;

import HorasLaborales.demo.Entities.VehiclesTypes.VehicleTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleTypeRepository extends JpaRepository <VehicleTypeEntity, Long> {
}
