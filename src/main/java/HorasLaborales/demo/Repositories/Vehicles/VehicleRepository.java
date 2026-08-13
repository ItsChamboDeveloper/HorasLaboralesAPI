package HorasLaborales.demo.Repositories.Vehicles;

import HorasLaborales.demo.Entities.Vehicles.VehicleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<VehicleEntity, Long> {


    Page<VehicleEntity> findAll(Pageable pageable);

    Optional<VehicleEntity> findByPlateNumber(String plateNumber);
    Optional<VehicleEntity> findByCirculationCardNumber(String circulationCardNumber);
    Optional<VehicleEntity> findByOwnerPhone(String ownerPhone);
    List<VehicleEntity> findByStudentId_StudentId(Long studentId);

    // *** NUEVO: para que el papá/mamá vea solo sus vehículos ***
    List<VehicleEntity> findByParentId_ParentId(Long parentId);

    boolean existsByPlateNumber(String plateNumber);

}
