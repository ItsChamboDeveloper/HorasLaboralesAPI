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
import HorasLaborales.demo.Repositories.Instructors.InstructorRepository;
import HorasLaborales.demo.Services.Email.EmailService;
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

    @Autowired
    private EmailService emailService;

    @Autowired
    private InstructorRepository instructorRepository;

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
        objEntity.setStatus(1L); // Estado 1: Aprobación del Animador
        VehicleEntity saveVehicle = vehicleRepository.save(objEntity);

        // Buscar correo del Animador (Rol 4) y notificar
        try {
            List<String> correosAnimadores = instructorRepository.findEmailByRolId(4L);
            String studentName = "";
            if (saveVehicle.getStudentId() != null) {
                studentName = saveVehicle.getStudentId().getFirstName() + " " + saveVehicle.getStudentId().getLastName();
            }
            for (String correoAnimador : correosAnimadores) {
                emailService.enviarNotificacion(
                    correoAnimador,
                    "Nuevo Vehículo requiere aprobación",
                    "El estudiante " + studentName + " ha registrado un vehículo y requiere tu revisión."
                );
            }
        } catch (Exception e) {
            log.error("Error al enviar notificación al animador en createVehicle", e);
        }

        return convertToDTO(saveVehicle);
    }

    // 1. Estudiante crea vehículo (con flujo de aprobación inicial y correo)
    public VehicleDTO crearVehiculo(VehicleDTO json) {
        if (verifyVehicleExist(json.getPlateNumber())) {
            throw new ExceptionPlateNumberDuplicated("El número de placa ya existe en el sistema");
        }

        VehicleEntity objEntity = convertToEntity(json);
        objEntity.setStatus(1L); // Estado 1: Aprobación del Animador
        VehicleEntity saveVehicle = vehicleRepository.save(objEntity);

        // Buscar correo del Animador (Rol 4)
        List<String> correosAnimadores = instructorRepository.findEmailByRolId(4L);
        String studentName = "";
        if (saveVehicle.getStudentId() != null) {
            studentName = saveVehicle.getStudentId().getFirstName() + " " + saveVehicle.getStudentId().getLastName();
        }
        for (String correoAnimador : correosAnimadores) {
            emailService.enviarNotificacion(
                correoAnimador,
                "Nuevo Vehículo requiere aprobación",
                "El estudiante " + studentName + " ha registrado un vehículo y requiere tu revisión."
            );
        }
        return convertToDTO(saveVehicle);
    }

    public void revisionAnimador(Long vehiculoId, boolean aprobado) {
        revisionAnimador(vehiculoId, aprobado, null);
    }

    // 2. Animador revisa (Estado 1) con motivo personalizado para correo
    public void revisionAnimador(Long vehiculoId, boolean aprobado, String motivo) {
        VehicleEntity vehiculo = vehicleRepository.findById(vehiculoId)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));
        
        if (aprobado) {
            vehiculo.setStatus(2L); // Pasa a Estado 2: Aprobación de Coordinadora
            List<String> correosCoordinadora = instructorRepository.findEmailByRolId(3L); // Rol 3: Admin (Coordinadora)
            
            String studentName = "";
            if (vehiculo.getStudentId() != null) {
                studentName = vehiculo.getStudentId().getFirstName() + " " + vehiculo.getStudentId().getLastName();
            }
            for (String correoCoordinadora : correosCoordinadora) {
                emailService.enviarNotificacionConDetalles(
                    correoCoordinadora,
                    "Vehículo aprobado por Animador - Requiere tu firma",
                    "El vehículo de " + studentName + " fue aprobado por el Animador y espera tu aprobación final.",
                    motivo
                );
            }
        } else {
            vehiculo.setStatus(4L); // Id asignado a Rechazado en tu BD
            if (vehiculo.getStudentId() != null) {
                emailService.enviarNotificacionConDetalles(
                    vehiculo.getStudentId().getEmail(),
                    "Vehículo Rechazado por el Animador",
                    "Tu vehículo ha sido rechazado por el Animador. Por favor revisa el sistema.",
                    motivo
                );
            }
        }
        vehicleRepository.save(vehiculo);
    }

    public void revisionCoordinadora(Long vehiculoId, boolean aprobado) {
        revisionCoordinadora(vehiculoId, aprobado, null);
    }

    // 3. Coordinadora revisa (Estado 2) con motivo personalizado para correo
    public void revisionCoordinadora(Long vehiculoId, boolean aprobado, String motivo) {
        VehicleEntity vehiculo = vehicleRepository.findById(vehiculoId)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));
        
        if (aprobado) {
            vehiculo.setStatus(3L); // Id asignado a Aprobado Final en tu BD (3L = Aprobado)
            if (vehiculo.getStudentId() != null) {
                emailService.enviarNotificacionConDetalles(
                    vehiculo.getStudentId().getEmail(),
                    "¡Vehículo Aprobado por Coordinación!",
                    "Felicidades, tu vehículo ha completado el flujo de aprobación exitosamente.",
                    motivo
                );
            }
        } else {
            vehiculo.setStatus(4L); // Rechazado (4L = Rechazado)
            if (vehiculo.getStudentId() != null) {
                emailService.enviarNotificacionConDetalles(
                    vehiculo.getStudentId().getEmail(),
                    "Vehículo Rechazado por Coordinación",
                    "Tu vehículo ha sido rechazado en la fase final por la Coordinadora.",
                    motivo
                );
            }
        }
        vehicleRepository.save(vehiculo);
    }

    // Método para actualizar el estado de un vehículo
    public VehicleDTO updateVehicleStatus(Long vehicleId, Long newStatus) {
        return updateVehicleStatus(vehicleId, newStatus, null);
    }

    public VehicleDTO updateVehicleStatus(Long vehicleId, Long newStatus, String motivo) {
        VehicleEntity vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));
        
        Long previousStatus = vehicle.getStatus();
        vehicle.setStatus(newStatus);
        VehicleEntity updated = vehicleRepository.save(vehicle);

        // Disparar correos según la transición de estado
        try {
            String studentName = "";
            if (updated.getStudentId() != null) {
                studentName = updated.getStudentId().getFirstName() + " " + updated.getStudentId().getLastName();
            }

            if (newStatus == 2L) {
                // Animador aprueba -> Correo a Coordinadora (Rol 3)
                List<String> correosCoordinadora = instructorRepository.findEmailByRolId(3L);
                for (String correoCoordinadora : correosCoordinadora) {
                    emailService.enviarNotificacionConDetalles(
                        correoCoordinadora,
                        "Vehículo aprobado por Animador - Requiere tu firma",
                        "El vehículo de " + studentName + " fue aprobado por el Animador y espera tu aprobación final.",
                        motivo
                    );
                }
            } else if (newStatus == 3L) {
                // Coordinadora aprueba / Aprobado -> Correo a Estudiante
                if (updated.getStudentId() != null) {
                    emailService.enviarNotificacionConDetalles(
                        updated.getStudentId().getEmail(),
                        "¡Vehículo Aprobado!",
                        "Felicidades, tu vehículo ha completado el flujo de aprobación exitosamente.",
                        motivo
                    );
                }
            } else if (newStatus == 4L) {
                // Rechazado (por Animador o Coordinadora) -> Correo a Estudiante
                if (updated.getStudentId() != null) {
                    emailService.enviarNotificacionConDetalles(
                        updated.getStudentId().getEmail(),
                        "Vehículo Rechazado",
                        "Tu vehículo ha sido rechazado en el flujo de aprobación. Por favor revisa el sistema.",
                        motivo
                    );
                }
            }
        } catch (Exception e) {
            log.error("Error al enviar correo en updateVehicleStatus", e);
        }

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
