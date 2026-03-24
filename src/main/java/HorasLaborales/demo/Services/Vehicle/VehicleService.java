package HorasLaborales.demo.Services.Vehicle;

import HorasLaborales.demo.Entities.Students.StudentEntity;
import HorasLaborales.demo.Entities.Vehicles.VehicleEntity;
import HorasLaborales.demo.Entities.VehiclesTypes.VehicleTypeEntity;
import HorasLaborales.demo.Exceptions.Students.ExceptionStudentNotFound;
import HorasLaborales.demo.Exceptions.Vehicles.ExceptionPlateNumberDuplicated;
import HorasLaborales.demo.Exceptions.VehiclesType.ExceptionVehicleTypeIdNotFound;
import HorasLaborales.demo.Models.DTO.Vehicles.VehicleDTO;
import HorasLaborales.demo.Repositories.Students.StudentsRepository;
import HorasLaborales.demo.Repositories.VehicleType.VehicleTypeRepository;
import HorasLaborales.demo.Repositories.Vehicles.VehicleRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service @Slf4j
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private VehicleTypeRepository vehicleTypeRepository;

    @Autowired
    private StudentsRepository studentsRepository;

    //*** MÉTODOS PRINCIPALES ***\\

    // Método para obtener todos los vehículos con paginación
    public Page<VehicleDTO> getAllVehicles(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<VehicleEntity> vehicleEntityPage = vehicleRepository.findAll(pageable);
        return vehicleEntityPage.map(this::convertToDTO);
    }

    // Métodos para obtener vehículos por diferentes criterios
    public VehicleDTO getByPlateNumber(String plateNumber) {
        Optional<VehicleEntity> vehicleOptional = vehicleRepository.findByPlateNumber(plateNumber);
        return vehicleOptional
                .map(this::convertToDTO)
                .orElse(null);
    }

    public VehicleDTO getByCirculationCardNumber(String circulationCardNumber) {
        Optional<VehicleEntity> vehicleOptional = vehicleRepository.findByCirculationCardNumber(circulationCardNumber);
        return vehicleOptional
                .map(this::convertToDTO)
                .orElse(null);
    }

    public VehicleDTO getByOwnerPhone(String ownerPhone) {
        Optional<VehicleEntity> vehicleOptional = vehicleRepository.findByOwnerPhone(ownerPhone);
        return vehicleOptional
                .map(this::convertToDTO)
                .orElse(null);
    }

    // Método para crear un nuevo vehículo
    public VehicleDTO createVehicle(@Valid VehicleDTO json) {
        if (verifyVehicleExist(json.getPlateNumber())) {
            throw new ExceptionPlateNumberDuplicated("El número de placa ya existe en el sistema");
        }

        VehicleEntity objEntity = convertToEntity(json);
        VehicleEntity saveVehicle = vehicleRepository.save(objEntity);

        return convertToDTO(saveVehicle);
    }
    // Método para actualizar el estado de un vehículo
    public VehicleDTO updateVehicleStatus(Long vehicleId, Long newStatus) {
        VehicleEntity vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));
        vehicle.setStatus(newStatus);
        VehicleEntity updated = vehicleRepository.save(vehicle);
        return convertToDTO(updated);
    }


    //*** MÉTODOS COMPLEMENTARIOS***\\

    public boolean verifyVehicleExist(String plateNumber) {

        return vehicleRepository.existsByPlateNumber(plateNumber);
    }

    private VehicleDTO convertToDTO(VehicleEntity entity) {
        VehicleDTO dto = new VehicleDTO();
        dto.setVehicleId(entity.getVehicleId());
        dto.setPlateNumber(entity.getPlateNumber());
        dto.setBrand(entity.getBrand());
        dto.setModel(entity.getModel());

        // Asigna el nombre y ID del tipo de vehículo si el vehículo tiene uno asociado
        if (entity.getTypeId() != null) {
            dto.setTypeName(entity.getTypeId().getTypeName());
            dto.setTypeId(entity.getTypeId().getTypeId());
        }

        dto.setColor(entity.getColor());
        dto.setCirculationCardNumber(entity.getCirculationCardNumber());
        dto.setOwnerName(entity.getOwnerName());
        dto.setOwnerDui(entity.getOwnerDui());
        dto.setOwnerPhone(entity.getOwnerPhone());
        dto.setVehicleImage(entity.getVehicleImage());

        // Asigna el nombre y ID del estudiante si el vehículo está asociado a un estudiante
        if (entity.getStudentId() != null) {
            dto.setStudentName(entity.getStudentId().getLastName());
            dto.setStudentLastName(entity.getStudentId().getLastName());
            dto.setStudentId(entity.getStudentId().getStudentId());
        }

        dto.setMaintenanceEXPO(entity.getMaintenanceExpo());
        dto.setIdStatus(entity.getStatus());
        return dto;
    }

    private VehicleEntity convertToEntity(@Valid VehicleDTO json) {
        VehicleEntity entity = new VehicleEntity();

        entity.setPlateNumber(json.getPlateNumber());
        entity.setBrand(json.getBrand());
        entity.setModel(json.getModel());

        // Asigna el año académico si se proporciona un ID de año académico
        if (json.getTypeId() != null) {
            VehicleTypeEntity vehicleTypeEntity = vehicleTypeRepository.findById(json.getTypeId())
                    .orElseThrow(() -> new ExceptionVehicleTypeIdNotFound("ID del tipo de vehículo no encontrado"));
            entity.setTypeId(vehicleTypeEntity);
        }

        entity.setColor(json.getColor());
        entity.setCirculationCardNumber(json.getCirculationCardNumber());
        entity.setOwnerName(json.getOwnerName());
        entity.setOwnerDui(json.getOwnerDui());
        entity.setOwnerPhone(json.getOwnerPhone());
        entity.setVehicleImage(json.getVehicleImage());

        if (json.getStudentId() != null) {
            StudentEntity studentEntity = studentsRepository.findById(json.getStudentId())
                    .orElseThrow(() -> new ExceptionStudentNotFound("ID del estudiante no encontrado " + json.getStudentId()));
            entity.setStudentId(studentEntity);
        }

        entity.setMaintenanceExpo(json.getMaintenanceEXPO());
        entity.setStatus(json.getIdStatus());

        return entity;
    }

    // Método para obtener vehículos por el ID del estudiante
    public Map<String, Object> getVehiclesByStudentId(Long studentId) {
        List<VehicleEntity> vehicles = vehicleRepository.findByStudentId_StudentId(studentId);
        List<VehicleDTO> vehicleDTOs = vehicles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return Map.of(
                "vehiculos", vehicleDTOs,
                "cantidad", vehicleDTOs.size()
        );
    }

}
