package HorasLaborales.demo.Repositories.VehicleModels;

import HorasLaborales.demo.Entities.VehicleModels.VehicleModelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleModelRepository extends JpaRepository<VehicleModelEntity, Long> {

    // Esto es lo que llena el combobox de "modelo" filtrado por la marca elegida
    List<VehicleModelEntity> findByBrandId_BrandId(Long brandId);

}
