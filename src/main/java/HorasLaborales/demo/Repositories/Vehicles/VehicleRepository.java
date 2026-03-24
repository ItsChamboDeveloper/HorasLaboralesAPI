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

    boolean existsByPlateNumber(String plateNumber);

}
