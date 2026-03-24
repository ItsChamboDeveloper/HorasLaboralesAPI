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

    @Autowired
    public WorkOrderService(WorkOrderRepository repo, VehicleRepository vehicleRepository, ModuleRepository moduleRepository) {
        this.repo = repo;
        this.vehicleRepository = vehicleRepository;
        this.moduleRepository = moduleRepository;
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
            return ConvertirADTO(workOrderEntity);
        } catch (Exception e) {
            log.error("Error al registrar una Orden de Trabajo " + e.getMessage(), e);
            throw new ExceptionWorkOrdernotRegistred("La orden de trabajo no pudo ser registrada");
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
        log.info("UpdateStatus - workOrderId={}, newStatus={}", workOrderId, newStatus);
        WorkOrderEntity workOrder = repo.findById(workOrderId)
                .orElseThrow(() -> new ExceptionWorkOrdernotfound("Orden de trabajo no encontrada."));

        if (newStatus == null) {
            throw new IllegalArgumentException("El nuevo estado no puede ser nulo");
        }

        // Aplicar y persistir sin restricciones adicionales
        Long previousStatus = workOrder.getIdStatus();
        workOrder.setIdStatus(newStatus);
        WorkOrderEntity saved = repo.save(workOrder);
        log.info("UpdateStatus - saved workOrderId={}, previousStatus={}, newStatus={}", saved.getWorkOrderId(), previousStatus, saved.getIdStatus());
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
