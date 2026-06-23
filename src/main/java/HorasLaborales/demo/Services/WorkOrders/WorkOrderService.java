package HorasLaborales.demo.Services.WorkOrders;

import HorasLaborales.demo.Entities.Modules.ModuleEntity;
import HorasLaborales.demo.Entities.Vehicles.VehicleEntity;
import HorasLaborales.demo.Entities.WorkOrders.WorkOrderEntity;
import HorasLaborales.demo.Exceptions.Modules.ExceptionModuleNotFound;
import HorasLaborales.demo.Exceptions.Vehicles.ExceptionVehicleNotFound;
import HorasLaborales.demo.Exceptions.WorkOrders.ExceptionWorkOrdernotRegistred;
import HorasLaborales.demo.Exceptions.WorkOrders.ExceptionWorkOrdernotfound;
import HorasLaborales.demo.Models.DTO.WorkOrders.WorkOrderDTO;
import HorasLaborales.demo.Repositories.Modules.ModuleRepository;
import HorasLaborales.demo.Repositories.Vehicles.VehicleRepository;
import HorasLaborales.demo.Repositories.WorkOrders.WorkOrderRepository;
import HorasLaborales.demo.Repositories.Instructors.InstructorRepository;
import HorasLaborales.demo.Repositories.Observations.ObservationRepository;
import HorasLaborales.demo.Services.Email.EmailService;
import HorasLaborales.demo.Entities.Observation.ObservationEntity;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service @Slf4j
public class WorkOrderService {

    private final WorkOrderRepository repo;
    private final VehicleRepository vehicleRepository;
    private final ModuleRepository moduleRepository;
    private final InstructorRepository instructorRepository;
    private final EmailService emailService;
    private final ObservationRepository observationRepository;

    @Autowired
    public WorkOrderService(WorkOrderRepository repo, VehicleRepository vehicleRepository, ModuleRepository moduleRepository, InstructorRepository instructorRepository, EmailService emailService, ObservationRepository observationRepository) {
        this.repo = repo;
        this.vehicleRepository = vehicleRepository;
        this.moduleRepository = moduleRepository;
        this.instructorRepository = instructorRepository;
        this.emailService = emailService;
        this.observationRepository = observationRepository;
    }

    public Page<WorkOrderDTO> getAllWorkOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<WorkOrderEntity> pageEntity = repo.findAll(pageable);
        return pageEntity.map(this::ConvertirADTO);
    }

    public WorkOrderDTO insert(@Valid WorkOrderDTO json) {
        if (json == null) {
            throw new IllegalArgumentException("La orden de trabajo debe ser llenada con cada campo requerido");
        }
        try {
            // Log del DTO recibido para depuración
            log.info("Insert - DTO received: estimatedTime='{}', description='{}', vehicleId='{}', moduleId='{}', idStatus='{}'",
                    json.getEstimatedTime(), json.getDescription(), json.getVehicleId(), json.getModuleId(), json.getIdStatus());

            WorkOrderEntity objData = ConvertirAEntity(json);
            objData.setIdStatus(1L); // Estado 1: Aprobación del Animador

            // Log de la entidad antes de guardar
            log.info("Insert - Entity to save: estimatedTime='{}', description='{}', vehicleId='{}', moduleId='{}', idStatus='{}'",
                    objData.getEstimatedTime(), objData.getDescription(),
                    objData.getVehicleId() != null ? objData.getVehicleId().getVehicleId() : null,
                    objData.getModuleId() != null ? objData.getModuleId().getModuleId() : null,
                    objData.getIdStatus());

            WorkOrderEntity workOrderEntity = repo.save(objData);

            // Log de la entidad retornada por JPA después de guardar
            log.info("Insert - Entity saved: workOrderId='{}', estimatedTime='{}', description='{}', idStatus='{}'",
                    workOrderEntity.getWorkOrderId(), workOrderEntity.getEstimatedTime(), workOrderEntity.getDescription(), workOrderEntity.getIdStatus());

            // Buscar correo del Animador (Rol 4) y notificar
            try {
                List<String> correosAnimadores = instructorRepository.findEmailByRolId(4L);
                String studentName = "";
                if (workOrderEntity.getVehicleId() != null && workOrderEntity.getVehicleId().getStudentId() != null) {
                    studentName = workOrderEntity.getVehicleId().getStudentId().getFirstName() + " " + workOrderEntity.getVehicleId().getStudentId().getLastName();
                }
                for (String correoAnimador : correosAnimadores) {
                    emailService.enviarNotificacion(
                        correoAnimador,
                        "Nueva Work Order Creada",
                        "El estudiante " + studentName + " creó una nueva orden de trabajo pendiente de revisión."
                    );
                }
            } catch (Exception e) {
                log.error("Error al enviar notificación al animador en insert", e);
            }

            return ConvertirADTO(workOrderEntity);
        } catch (Exception e) {
            log.error("Error al registrar una Orden de Trabajo " + e.getMessage(), e);
            throw new ExceptionWorkOrdernotRegistred("La orden de trabajo no pudo ser registrada");
        }

    }

    // 1. Estudiante crea Work Order
    public WorkOrderDTO crearWorkOrder(WorkOrderDTO json) {
        if (json == null) {
            throw new IllegalArgumentException("La orden de trabajo debe ser llenada con cada campo requerido");
        }
        try {
            WorkOrderEntity objData = ConvertirAEntity(json);
            objData.setIdStatus(1L); // Estado 1: Aprobación del Animador
            WorkOrderEntity saved = repo.save(objData);

            // Buscar correo del Animador (Rol 4)
            List<String> correosAnimadores = instructorRepository.findEmailByRolId(4L);
            String studentName = "";
            if (saved.getVehicleId() != null && saved.getVehicleId().getStudentId() != null) {
                studentName = saved.getVehicleId().getStudentId().getFirstName() + " " + saved.getVehicleId().getStudentId().getLastName();
            }

            for (String correoAnimador : correosAnimadores) {
                emailService.enviarNotificacion(
                    correoAnimador,
                    "Nueva Work Order Creada",
                    "El estudiante " + studentName + " creó una nueva orden de trabajo pendiente de revisión."
                );
            }
            return ConvertirADTO(saved);
        } catch (Exception e) {
            log.error("Error al registrar una Orden de Trabajo " + e.getMessage(), e);
            throw new ExceptionWorkOrdernotRegistred("La orden de trabajo no pudo ser registrada");
        }
    }

    // 2. Animador revisa (Estado 1)
    public void revisionAnimador(Long woId, boolean aprobado) {
        revisionAnimador(woId, aprobado, null);
    }

    public void revisionAnimador(Long woId, boolean aprobado, String motivo) {
        WorkOrderEntity wo = repo.findById(woId)
                .orElseThrow(() -> new ExceptionWorkOrdernotfound("Work Order no encontrada"));
        if (aprobado) {
            wo.setIdStatus(2L); // Estado 2: Aprobación de Coordinadora
            List<String> correosCoord = instructorRepository.findEmailByRolId(3L);
            for (String correoCoord : correosCoord) {
                emailService.enviarNotificacionConDetalles(correoCoord, "Work Order aprobada por Animador", "Requiere tu visto bueno final.", motivo);
            }
        } else {
            wo.setIdStatus(5L); // Estado 5: Rechazado
            if (wo.getVehicleId() != null && wo.getVehicleId().getStudentId() != null) {
                emailService.enviarNotificacionConDetalles(wo.getVehicleId().getStudentId().getEmail(), "Work Order Rechazada", "El animador rechazó tu Work Order.", motivo);
            }
        }
        repo.save(wo);
    }

    // 3. Coordinadora revisa (Estado 2)
    public void revisionCoordinadora(Long woId, boolean aprobado) {
        revisionCoordinadora(woId, aprobado, null);
    }

    public void revisionCoordinadora(Long woId, boolean aprobado, String motivo) {
        WorkOrderEntity wo = repo.findById(woId)
                .orElseThrow(() -> new ExceptionWorkOrdernotfound("Work Order no encontrada"));
        if (aprobado) {
            wo.setIdStatus(3L); // Estado 3: Aprobado - En Progreso
            if (wo.getVehicleId() != null && wo.getVehicleId().getStudentId() != null) {
                emailService.enviarNotificacionConDetalles(wo.getVehicleId().getStudentId().getEmail(), "Work Order Aprobada Completamente", "Tu orden está lista.", motivo);
            }
        } else {
            wo.setIdStatus(5L); // Estado 5: Rechazado
            if (wo.getVehicleId() != null && wo.getVehicleId().getStudentId() != null) {
                emailService.enviarNotificacionConDetalles(wo.getVehicleId().getStudentId().getEmail(), "Work Order Rechazada por Coordinación", "Revisa los comentarios de la Coordinadora.", motivo);
            }
        }
        repo.save(wo);
    }

    // 4. Observación o Finalización por parte del Estudiante
    public void registrarObservacionOFinalizar(Long woId, String accion, String comentario) {
        WorkOrderEntity wo = repo.findById(woId)
                .orElseThrow(() -> new ExceptionWorkOrdernotfound("Work Order no encontrada"));
        
        if (accion.equalsIgnoreCase("FINALIZAR")) {
            wo.setIdStatus(4L); // Estado 4: Completado
        } else {
            // Guardar observación en la BD
            ObservationEntity obs = new ObservationEntity();
            obs.setWorkOrderId(wo);
            obs.setObservacion(comentario);
            if (wo.getVehicleId() != null && wo.getVehicleId().getStudentId() != null) {
                obs.setStudentId(wo.getVehicleId().getStudentId());
            }
            observationRepository.save(obs);
        }
        repo.save(wo);

        // Buscar correos para duplicar la notificación
        List<String> correosAnimadores = instructorRepository.findEmailByRolId(4L);
        List<String> correosCoordinadoras = instructorRepository.findEmailByRolId(3L);
        
        String studentName = "";
        if (wo.getVehicleId() != null && wo.getVehicleId().getStudentId() != null) {
            studentName = wo.getVehicleId().getStudentId().getFirstName() + " " + wo.getVehicleId().getStudentId().getLastName();
        }
        
        String asunto = "Work Order Actualizada por Estudiante - Acción: " + accion;
        String cuerpo = "El estudiante " + studentName + " ejecutó la acción [" + accion + "] en la Work Order ID: " + woId + ".\nObservación: " + comentario;

        for (String correoAnimador : correosAnimadores) {
            emailService.enviarNotificacion(correoAnimador, asunto, cuerpo);
        }
        for (String correoCoordinadora : correosCoordinadoras) {
            emailService.enviarNotificacion(correoCoordinadora, asunto, cuerpo);
        }
    }

    public boolean delete(Long id) {
        //1. Verificar la existencia del producto
        WorkOrderEntity existencia = repo.findById(id).orElse(null);
        //2. Si el valor existe lo elimina
        if (existencia != null) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    public WorkOrderDTO update(Long id, @Valid WorkOrderDTO json) {
        // 1. Verificar existencia de la orden de trabajo
        WorkOrderEntity workOrderExist = repo.findById(id).orElseThrow(() -> new ExceptionWorkOrdernotfound("Orden de trabajo no encontrada."));
        // 2. Actuaización de campos
        workOrderExist.setWorkOrderId(json.getWorkOrderId());

        workOrderExist.setIdStatus(json.getIdStatus());
        // Actualizar descripción si se proporciona (puede ser null)
        workOrderExist.setDescription(json.getDescription());
        // Actualizar estimatedTime si se proporciona
        workOrderExist.setEstimatedTime(json.getEstimatedTime());
        //3. Actualización del registro
        WorkOrderEntity WorkOrderUpdated = repo.save(workOrderExist);
        //4. Convertir a DTO
        return ConvertirADTO(WorkOrderUpdated);
    }

    // Obtener órdenes por estudiante y estado
    public Map<String, Object> getWorkOrdersByStudentIdAndStatus(Long studentId, Long status) {
        List<WorkOrderEntity> orders = repo.findByVehicleId_StudentId_StudentIdAndIdStatus(studentId, status);
        List<WorkOrderDTO> dtos = orders.stream().map(this::ConvertirADTO).toList();
        return Map.of(
                "workOrders", dtos,
                "cantidad", dtos.size()
        );
    }

    // Obtener todas las órdenes por estudiante (sin filtrar por estado)
    public Map<String, Object> getWorkOrdersByStudentId(Long studentId) {
        List<WorkOrderEntity> orders = repo.findByVehicleId_StudentId_StudentId(studentId);
        List<WorkOrderDTO> dtos = orders.stream().map(this::ConvertirADTO).toList();
        return Map.of(
                "workOrders", dtos,
                "cantidad", dtos.size()
        );
    }

    // Obtener todas las órdenes por número de placa del vehículo
    public Map<String, Object> getWorkOrdersByPlate(String plateNumber) {
        List<WorkOrderEntity> orders = repo.findByVehicleId_PlateNumber(plateNumber);
        List<WorkOrderDTO> dtos = orders.stream().map(this::ConvertirADTO).toList();
        return Map.of(
                "workOrders", dtos,
                "cantidad", dtos.size()
        );
    }

    public WorkOrderDTO updateWorkOrderStatus(Long workOrderId, Long newStatus) {
        return updateWorkOrderStatus(workOrderId, newStatus, null);
    }

    public WorkOrderDTO updateWorkOrderStatus(Long workOrderId, Long newStatus, String motivo) {
        log.info("UpdateStatus - workOrderId={}, newStatus={}, motivo={}", workOrderId, newStatus, motivo);
        WorkOrderEntity workOrder = repo.findById(workOrderId)
                .orElseThrow(() -> new ExceptionWorkOrdernotfound("Orden de trabajo no encontrada."));

        if (newStatus == null) {
            throw new IllegalArgumentException("El nuevo estado no puede ser nulo");
        }

        Long previousStatus = workOrder.getIdStatus();
        workOrder.setIdStatus(newStatus);
        WorkOrderEntity saved = repo.save(workOrder);
        log.info("UpdateStatus - saved workOrderId={}, previousStatus={}, newStatus={}", saved.getWorkOrderId(), previousStatus, saved.getIdStatus());

        // Disparar correos según la transición de estado
        try {
            String studentName = "";
            String studentEmail = "";
            if (saved.getVehicleId() != null && saved.getVehicleId().getStudentId() != null) {
                studentName = saved.getVehicleId().getStudentId().getFirstName() + " " + saved.getVehicleId().getStudentId().getLastName();
                studentEmail = saved.getVehicleId().getStudentId().getEmail();
            }

            if (newStatus == 2L) {
                // Animador aprueba -> Correo a Coordinadora (Rol 3)
                List<String> correosCoord = instructorRepository.findEmailByRolId(3L);
                for (String correoCoord : correosCoord) {
                    emailService.enviarNotificacionConDetalles(
                        correoCoord,
                        "Work Order aprobada por Animador",
                        "La Work Order del estudiante " + studentName + " ha sido aprobada por el Animador y requiere tu aprobación final.",
                        motivo
                    );
                }
            } else if (newStatus == 3L) {
                // Coordinadora aprueba / Aprobado - En Progreso -> Correo a Estudiante
                if (studentEmail != null && !studentEmail.isEmpty()) {
                    emailService.enviarNotificacionConDetalles(
                        studentEmail,
                        "Work Order Aprobada - En Progreso",
                        "Felicidades, tu orden de trabajo ha sido aprobada por la Coordinadora y se encuentra en progreso.",
                        motivo
                    );
                }
            } else if (newStatus == 5L) {
                // Rechazado -> Correo a Estudiante
                if (studentEmail != null && !studentEmail.isEmpty()) {
                    emailService.enviarNotificacionConDetalles(
                        studentEmail,
                        "Work Order Rechazada",
                        "Tu Work Order ha sido rechazada en el flujo de aprobación. Por favor revisa el sistema.",
                        motivo
                    );
                }
            } else if (newStatus == 4L) {
                // Completado -> Correo a Estudiante
                if (studentEmail != null && !studentEmail.isEmpty()) {
                    emailService.enviarNotificacionConDetalles(
                        studentEmail,
                        "Work Order Completada",
                        "Tu orden de trabajo ha sido completada con éxito.",
                        motivo
                    );
                }
            }
        } catch (Exception e) {
            log.error("Error al enviar correo en updateWorkOrderStatus", e);
        }

        return ConvertirADTO(saved);
    }

    // Obtener órdenes por estado (idStatus)
    public Map<String, Object> getWorkOrdersByStatus(Long status) {
        List<WorkOrderEntity> orders = repo.findByIdStatus(status);
        List<WorkOrderDTO> dtos = orders.stream().map(this::ConvertirADTO).toList();
        return Map.of(
                "workOrders", dtos,
                "cantidad", dtos.size()
        );
    }

    // Conveniencia: obtener órdenes con estado = 1
    public Map<String, Object> getWorkOrdersByStatus1() {
        return getWorkOrdersByStatus(1L);
    }

    // Conveniencia: obtener órdenes con estado = 2
    public Map<String, Object> getWorkOrdersByStatus2() {
        return getWorkOrdersByStatus(2L);
    }

    private WorkOrderDTO ConvertirADTO(WorkOrderEntity workOrderEntity) {
        WorkOrderDTO dto = new WorkOrderDTO();
        dto.setWorkOrderId(workOrderEntity.getWorkOrderId());

        if (workOrderEntity.getVehicleId() != null) {
            dto.setVehiclePlateNumber(workOrderEntity.getVehicleId().getPlateNumber());
            dto.setVehicleId(workOrderEntity.getVehicleId().getVehicleId());
            // Nuevo: brand/model
            dto.setVehicleBrand(workOrderEntity.getVehicleId().getBrand());
            dto.setVehicleModel(workOrderEntity.getVehicleId().getModel());
            // VehicleYear no existe en la entidad; mantener null
            dto.setVehicleYear(null);
        }

        if (workOrderEntity.getModuleId() != null) {
            dto.setModuleName(workOrderEntity.getModuleId().getModuleName());
            dto.setModuleId(workOrderEntity.getModuleId().getModuleId());
            dto.setModuleCode(workOrderEntity.getModuleId().getModuleCode());
        }

        dto.setWorkOrderImage(workOrderEntity.getWorkOrdersImage());
        dto.setIdStatus(workOrderEntity.getIdStatus());
        // Mapear descripción
        dto.setDescription(workOrderEntity.getDescription());
        // Mapear estimatedTime
        dto.setEstimatedTime(workOrderEntity.getEstimatedTime());

        // Mapear statusName (mapeo simple)
        Long status = workOrderEntity.getIdStatus();
        String statusName = "";
        if (status != null) {
            switch (status.intValue()) {
                case 1 -> statusName = "Pendiente";
                case 2 -> statusName = "Aprobado";
                case 3 -> statusName = "Aprobado - En Progreso";
                case 4 -> statusName = "Completado";
                case 5 -> statusName = "Rechazado";
                case 6 -> statusName = "Atrasado";
                default -> statusName = "Desconocido";
            }
        }
        dto.setStatusName(statusName);

        return dto;
    }

    private WorkOrderEntity ConvertirAEntity(@Valid WorkOrderDTO json) {
        WorkOrderEntity entity = new WorkOrderEntity();
        entity.setWorkOrderId(json.getWorkOrderId());

        if (json.getVehicleId() != null) {
            VehicleEntity vehicleEntity = vehicleRepository.findById(json.getVehicleId())
                    .orElseThrow(() -> new ExceptionVehicleNotFound("ID del vehiculo no encontrado"));
            entity.setVehicleId(vehicleEntity);
        }

        if (json.getModuleId() != null) {
            ModuleEntity moduleEntity = moduleRepository.findById(json.getModuleId())
                    .orElseThrow(() -> new ExceptionModuleNotFound("ID del modulo no encontrado"));
            entity.setModuleId(moduleEntity);
        }

        entity.setWorkOrdersImage(json.getWorkOrderImage());
        entity.setIdStatus(json.getIdStatus());
        // Mapear descripción
        entity.setDescription(json.getDescription());
        // Mapear estimatedTime
        entity.setEstimatedTime(json.getEstimatedTime());

        return entity;
    }

}
