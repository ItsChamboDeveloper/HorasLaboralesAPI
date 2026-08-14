package HorasLaborales.demo.Services.WorkOrders;

import HorasLaborales.demo.Entities.Modules.ModuleEntity;
import HorasLaborales.demo.Entities.Observation.ObservationEntity;
import HorasLaborales.demo.Entities.Vehicles.VehicleEntity;
import HorasLaborales.demo.Entities.WorkOrders.WorkOrderEntity;
import HorasLaborales.demo.Exceptions.Modules.ExceptionModuleNotFound;
import HorasLaborales.demo.Exceptions.Vehicles.ExceptionVehicleNotFound;
import HorasLaborales.demo.Exceptions.WorkOrders.ExceptionWorkOrdernotRegistred;
import HorasLaborales.demo.Exceptions.WorkOrders.ExceptionWorkOrdernotfound;
import HorasLaborales.demo.Models.DTO.WorkOrders.WorkOrderDTO;
import HorasLaborales.demo.Repositories.Instructors.InstructorRepository;
import HorasLaborales.demo.Repositories.Modules.ModuleRepository;
import HorasLaborales.demo.Repositories.Observations.ObservationRepository;
import HorasLaborales.demo.Repositories.Vehicles.VehicleRepository;
import HorasLaborales.demo.Repositories.WorkOrders.WorkOrderRepository;
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

@Service
@Slf4j
public class WorkOrderService {

    private final WorkOrderRepository repo;
    private final VehicleRepository vehicleRepository;
    private final ModuleRepository moduleRepository;
    private final InstructorRepository instructorRepository;
    private final EmailService emailService;
    private final ObservationRepository observationRepository;

    @Autowired
    public WorkOrderService(
            WorkOrderRepository repo,
            VehicleRepository vehicleRepository,
            ModuleRepository moduleRepository,
            InstructorRepository instructorRepository,
            EmailService emailService,
            ObservationRepository observationRepository
    ) {
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
            log.info("Insert - DTO received: estimatedTime='{}', description='{}', vehicleId='{}', moduleId='{}', idStatus='{}'",
                    json.getEstimatedTime(), json.getDescription(), json.getVehicleId(), json.getModuleId(), json.getIdStatus());

            WorkOrderEntity objData = ConvertirAEntity(json);
            objData.setIdStatus(1L); // Estado 1: Aprobación del Animador

            WorkOrderEntity workOrderEntity = repo.save(objData);

            try {
                List<String> correosAnimadores = instructorRepository.findEmailByRolId(4L);
                String studentName = "";
                if (workOrderEntity.getVehicleId() != null && workOrderEntity.getVehicleId().getStudentId() != null) {
                    studentName = workOrderEntity.getVehicleId().getStudentId().getFirstName() + " " +
                            workOrderEntity.getVehicleId().getStudentId().getLastName();
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

    public WorkOrderDTO crearWorkOrder(WorkOrderDTO json) {
        if (json == null) {
            throw new IllegalArgumentException("La orden de trabajo debe ser llenada con cada campo requerido");
        }
        try {
            WorkOrderEntity objData = ConvertirAEntity(json);
            objData.setIdStatus(1L);
            WorkOrderEntity saved = repo.save(objData);

            List<String> correosAnimadores = instructorRepository.findEmailByRolId(4L);
            String studentName = "";
            if (saved.getVehicleId() != null && saved.getVehicleId().getStudentId() != null) {
                studentName = saved.getVehicleId().getStudentId().getFirstName() + " " +
                        saved.getVehicleId().getStudentId().getLastName();
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

    public void revisionAnimador(Long woId, boolean aprobado) {
        revisionAnimador(woId, aprobado, null);
    }

    public void revisionAnimador(Long woId, boolean aprobado, String motivo) {
        WorkOrderEntity wo = repo.findById(woId)
                .orElseThrow(() -> new ExceptionWorkOrdernotfound("Work Order no encontrada"));
        if (aprobado) {
            wo.setIdStatus(2L);
            List<String> correosCoord = instructorRepository.findEmailByRolId(3L);
            for (String correoCoord : correosCoord) {
                emailService.enviarNotificacionConDetalles(
                        correoCoord,
                        "Work Order aprobada por Animador",
                        "Requiere tu visto bueno final.",
                        motivo
                );
            }
        } else {
            wo.setIdStatus(5L);
            if (wo.getVehicleId() != null && wo.getVehicleId().getStudentId() != null) {
                emailService.enviarNotificacionConDetalles(
                        wo.getVehicleId().getStudentId().getEmail(),
                        "Work Order Rechazada",
                        "El animador rechazó tu Work Order.",
                        motivo
                );
            }
        }
        repo.save(wo);
    }

    public void revisionCoordinadora(Long woId, boolean aprobado) {
        revisionCoordinadora(woId, aprobado, null);
    }

    public void revisionCoordinadora(Long woId, boolean aprobado, String motivo) {
        WorkOrderEntity wo = repo.findById(woId)
                .orElseThrow(() -> new ExceptionWorkOrdernotfound("Work Order no encontrada"));
        if (aprobado) {
            wo.setIdStatus(3L);
            if (wo.getVehicleId() != null && wo.getVehicleId().getStudentId() != null) {
                emailService.enviarNotificacionConDetalles(
                        wo.getVehicleId().getStudentId().getEmail(),
                        "Work Order Aprobada Completamente",
                        "Tu orden está lista.",
                        motivo
                );
            }
        } else {
            wo.setIdStatus(5L);
            if (wo.getVehicleId() != null && wo.getVehicleId().getStudentId() != null) {
                emailService.enviarNotificacionConDetalles(
                        wo.getVehicleId().getStudentId().getEmail(),
                        "Work Order Rechazada por Coordinación",
                        "Revisa los comentarios de la Coordinadora.",
                        motivo
                );
            }
        }
        repo.save(wo);
    }

    public void registrarObservacionOFinalizar(Long woId, String accion, String comentario) {
        WorkOrderEntity wo = repo.findById(woId)
                .orElseThrow(() -> new ExceptionWorkOrdernotfound("Work Order no encontrada"));

        if (accion.equalsIgnoreCase("FINALIZAR")) {
            wo.setIdStatus(4L);
        } else {
            ObservationEntity obs = new ObservationEntity();
            obs.setWorkOrderId(wo);
            obs.setObservacion(comentario);
            if (wo.getVehicleId() != null && wo.getVehicleId().getStudentId() != null) {
                obs.setStudentId(wo.getVehicleId().getStudentId());
            }
            observationRepository.save(obs);
        }
        repo.save(wo);

        List<String> correosAnimadores = instructorRepository.findEmailByRolId(4L);
        List<String> correosCoordinadoras = instructorRepository.findEmailByRolId(3L);

        String studentName = "";
        if (wo.getVehicleId() != null && wo.getVehicleId().getStudentId() != null) {
            studentName = wo.getVehicleId().getStudentId().getFirstName() + " " +
                    wo.getVehicleId().getStudentId().getLastName();
        }

        String asunto = "Work Order Actualizada por Estudiante - Acción: " + accion;
        String cuerpo = "El estudiante " + studentName + " ejecutó la acción [" + accion + "] en la Work Order ID: " + woId +
                ".\nObservación: " + comentario;

        for (String correoAnimador : correosAnimadores) {
            emailService.enviarNotificacion(correoAnimador, asunto, cuerpo);
        }
        for (String correoCoordinadora : correosCoordinadoras) {
            emailService.enviarNotificacion(correoCoordinadora, asunto, cuerpo);
        }
    }

    public boolean delete(Long id) {
        WorkOrderEntity existencia = repo.findById(id).orElse(null);
        if (existencia != null) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    public WorkOrderDTO update(Long id, @Valid WorkOrderDTO json) {
        WorkOrderEntity workOrderExist = repo.findById(id)
                .orElseThrow(() -> new ExceptionWorkOrdernotfound("Orden de trabajo no encontrada."));

        workOrderExist.setWorkOrderId(json.getWorkOrderId());
        workOrderExist.setIdStatus(json.getIdStatus());
        workOrderExist.setDescription(json.getDescription());
        workOrderExist.setEstimatedTime(json.getEstimatedTime());

        WorkOrderEntity WorkOrderUpdated = repo.save(workOrderExist);
        return ConvertirADTO(WorkOrderUpdated);
    }

    public Map<String, Object> getWorkOrdersByStudentIdAndStatus(Long studentId, Long status) {
        List<WorkOrderEntity> orders = repo.findByVehicleId_StudentId_StudentIdAndIdStatus(studentId, status);
        List<WorkOrderDTO> dtos = orders.stream().map(this::ConvertirADTO).toList();
        return Map.of("workOrders", dtos, "cantidad", dtos.size());
    }

    public Map<String, Object> getWorkOrdersByStudentId(Long studentId) {
        List<WorkOrderEntity> orders = repo.findByVehicleId_StudentId_StudentId(studentId);
        List<WorkOrderDTO> dtos = orders.stream().map(this::ConvertirADTO).toList();
        return Map.of("workOrders", dtos, "cantidad", dtos.size());
    }

    public Map<String, Object> getWorkOrdersByPlate(String plateNumber) {
        List<WorkOrderEntity> orders = repo.findByVehicleId_PlateNumber(plateNumber);
        List<WorkOrderDTO> dtos = orders.stream().map(this::ConvertirADTO).toList();
        return Map.of("workOrders", dtos, "cantidad", dtos.size());
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
        log.info("UpdateStatus - saved workOrderId={}, previousStatus={}, newStatus={}",
                saved.getWorkOrderId(), previousStatus, saved.getIdStatus());

        try {
            String studentName = "";
            String studentEmail = "";
            if (saved.getVehicleId() != null && saved.getVehicleId().getStudentId() != null) {
                studentName = saved.getVehicleId().getStudentId().getFirstName() + " " +
                        saved.getVehicleId().getStudentId().getLastName();
                studentEmail = saved.getVehicleId().getStudentId().getEmail();
            }

            if (newStatus == 2L) {
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
                if (studentEmail != null && !studentEmail.isEmpty()) {
                    emailService.enviarNotificacionConDetalles(
                            studentEmail,
                            "Work Order Aprobada - En Progreso",
                            "Felicidades, tu orden de trabajo ha sido aprobada por la Coordinadora y se encuentra en progreso.",
                            motivo
                    );
                }
            } else if (newStatus == 5L) {
                if (studentEmail != null && !studentEmail.isEmpty()) {
                    emailService.enviarNotificacionConDetalles(
                            studentEmail,
                            "Work Order Rechazada",
                            "Tu Work Order ha sido rechazada en el flujo de aprobación. Por favor revisa el sistema.",
                            motivo
                    );
                }
            } else if (newStatus == 4L) {
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

    public Map<String, Object> getWorkOrdersByStatus(Long status) {
        List<WorkOrderEntity> orders = repo.findByIdStatus(status);
        List<WorkOrderDTO> dtos = orders.stream().map(this::ConvertirADTO).toList();
        return Map.of("workOrders", dtos, "cantidad", dtos.size());
    }

    public Map<String, Object> getWorkOrdersByStatus1() {
        return getWorkOrdersByStatus(1L);
    }

    public Map<String, Object> getWorkOrdersByStatus2() {
        return getWorkOrdersByStatus(2L);
    }

    private WorkOrderDTO ConvertirADTO(WorkOrderEntity workOrderEntity) {
        WorkOrderDTO dto = new WorkOrderDTO();
        dto.setWorkOrderId(workOrderEntity.getWorkOrderId());

        if (workOrderEntity.getVehicleId() != null) {
            VehicleEntity v = workOrderEntity.getVehicleId();

            dto.setVehiclePlateNumber(v.getPlateNumber());
            dto.setVehicleId(v.getVehicleId());
            dto.setVehicleBrand(v.getBrand());
            dto.setVehicleModel(v.getModel());
            dto.setVehicleYear(null); // no existe en entidad
            dto.setOwnerName(v.getOwnerName());
            dto.setOwnerEmail(v.getOwnerEmail());

            if (v.getStudentId() != null) {
                dto.setStudentName(v.getStudentId().getFirstName());
                dto.setStudentLastName(v.getStudentId().getLastName());
            }
        }

        if (workOrderEntity.getModuleId() != null) {
            dto.setModuleName(workOrderEntity.getModuleId().getModuleName());
            dto.setModuleId(workOrderEntity.getModuleId().getModuleId());
            dto.setModuleCode(workOrderEntity.getModuleId().getModuleCode());

            if (workOrderEntity.getModuleId().getInstructor() != null) {
                dto.setInstructorName(
                        workOrderEntity.getModuleId().getInstructor().getFirstName() + " " +
                                workOrderEntity.getModuleId().getInstructor().getLastName()
                );
            }
        }

        dto.setWorkOrderImage(workOrderEntity.getWorkOrdersImage());
        dto.setWorkOrderImageLeft(workOrderEntity.getWorkOrderImageLeft());
        dto.setWorkOrderImageRight(workOrderEntity.getWorkOrderImageRight());
        dto.setWorkOrderImageBack(workOrderEntity.getWorkOrderImageBack());
        dto.setIdStatus(workOrderEntity.getIdStatus());
        dto.setDescription(workOrderEntity.getDescription());
        dto.setEstimatedTime(workOrderEntity.getEstimatedTime());

        Long status = workOrderEntity.getIdStatus();
        String statusName = "";
        int progressPercent = 0;
        if (status != null) {
            switch (status.intValue()) {
                case 1 -> { statusName = "Pendiente"; progressPercent = 25; }
                case 2 -> { statusName = "Aprobado"; progressPercent = 50; }
                case 3 -> { statusName = "Aprobado - En Progreso"; progressPercent = 75; }
                case 4 -> { statusName = "Completado"; progressPercent = 100; }
                case 5 -> { statusName = "Rechazado"; progressPercent = 0; }
                case 6 -> { statusName = "Atrasado"; progressPercent = 50; }
                default -> { statusName = "Desconocido"; progressPercent = 0; }
            }
        }
        dto.setStatusName(statusName);
        dto.setProgressPercent(progressPercent);

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
        entity.setWorkOrderImageLeft(json.getWorkOrderImageLeft());
        entity.setWorkOrderImageRight(json.getWorkOrderImageRight());
        entity.setWorkOrderImageBack(json.getWorkOrderImageBack());
        entity.setIdStatus(json.getIdStatus());
        entity.setDescription(json.getDescription());
        entity.setEstimatedTime(json.getEstimatedTime());

        return entity;
    }
}