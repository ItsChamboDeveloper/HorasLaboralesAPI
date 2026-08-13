package HorasLaborales.demo.Controllers.Vehicles;

import HorasLaborales.demo.Exceptions.Vehicles.ExceptionVehicleDontInsert;
import HorasLaborales.demo.Models.ApiResponse.ApiResponse;
import HorasLaborales.demo.Models.DTO.Vehicles.VehicleDTO;
import HorasLaborales.demo.Services.Vehicle.VehicleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin("*")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @GetMapping("/getAllVehicles")
    public ResponseEntity<ApiResponse<Page<VehicleDTO>>> getAllVehicles(
            @RequestParam(defaultValue = "0") int page, // Página por defecto 0
            @RequestParam(defaultValue = "10") int size // Tamaño por defecto 10
    ) {
        // Validación del tamaño de página: debe estar entre 1 y 50
        if (size <= 0 || size > 50) {
            ResponseEntity.badRequest().body(Map.of(
                    "status", "La paginación de datos debe estar entre 1 y 50"
            ));
            return ResponseEntity.ok(null); // Devuelve nulo si la validación falla
        }

        // Obtiene los estudiantes paginados desde el servicio
        Page<VehicleDTO> vehicles = vehicleService.getAllVehicles(page, size);

        // Si ocurre un error al obtener los datos
        if (vehicles == null) {
            ResponseEntity.badRequest().body(Map.of(
                    "status", "Error al obtener los datos del vehículos"
            ));
        }
        // Retorna respuesta exitosa con los datos
        return ResponseEntity.ok(ApiResponse.success("Datos consultados correctamente", vehicles));
    }

    @GetMapping("/getVehicleByPlateNumber/{plateNumber}")
    public ResponseEntity<?> getVehicleByPlateNumber(@PathVariable String plateNumber) {
        VehicleDTO vehicleDTO = vehicleService.getByPlateNumber(plateNumber);
        if (vehicleDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", "NOT FOUND",
                            "message", "Vehículo no encontrado"
                    ));
        }
        return ResponseEntity.ok(ApiResponse.success("Vehículo encontrado", vehicleDTO));
    }

    @GetMapping("/getVehicleByCirculationCardNumber/{CirculationCardNumber}")
    public ResponseEntity<?> getVehicleCirculationCardNumber(@PathVariable String CirculationCardNumber) {
        VehicleDTO vehicleDTO = vehicleService.getByPlateNumber(CirculationCardNumber);
        if (vehicleDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", "NOT FOUND",
                            "message", "Vehículo no encontrado"
                    ));
        }
        return ResponseEntity.ok(ApiResponse.success("Vehículo encontrado", vehicleDTO));
    }

    @GetMapping("/getVehicleByOwnerPhone/{ownerPhone}")
    public ResponseEntity<?> getByOwnerPhone(@PathVariable String ownerPhone) {
        VehicleDTO vehicleDTO = vehicleService.getByPlateNumber(ownerPhone);
        if (vehicleDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", "NOT FOUND",
                            "message", "Vehículo no encontrado"
                    ));
        }
        return ResponseEntity.ok(ApiResponse.success("Vehículo encontrado", vehicleDTO));
    }

    @GetMapping("/getVehiclesByStudentId/{studentId}")
    public ResponseEntity<?> getVehiclesByStudentId(@PathVariable Long studentId) {
        Map<String, Object> result = vehicleService.getVehiclesByStudentId(studentId);
        return ResponseEntity.ok(ApiResponse.success("Vehículos consultados correctamente", result));
    }

    // *** NUEVO: para que el papá/mamá vea SOLO los vehículos de su cuenta ***
    @GetMapping("/getVehiclesByParentId/{parentId}")
    public ResponseEntity<?> getVehiclesByParentId(@PathVariable Long parentId) {
        Map<String, Object> result = vehicleService.getVehiclesByParentId(parentId);
        return ResponseEntity.ok(ApiResponse.success("Vehículos consultados correctamente", result));
    }


    @PostMapping("/newVehicle")
    public ResponseEntity<ApiResponse<VehicleDTO>> createVehicle(@Valid @RequestBody VehicleDTO json
    ) {
        // Verifica si el JSON recibido es nulo
        if (json == null) {
            throw new ExceptionVehicleDontInsert("Error al recibir y procesar la información del vehículo");
        }

        // Intenta guardar el estudiante usando el servicio
        VehicleDTO vehicleSaved= vehicleService.createVehicle(json);

        // Si el estudiante no se guarda correctamente
        if (vehicleSaved == null) {
            throw new ExceptionVehicleDontInsert("El vehículo no pudo ser registrado debido a un problema en los datos");
        }

        // Retorna respuesta exitosa con el estudiante guardado
        return ResponseEntity.ok(ApiResponse.success("Vehículo registrado exitosamente", vehicleSaved));
    }

    @PutMapping("/updateStatusVehicle/{vehicleId}")
    public ResponseEntity<ApiResponse<VehicleDTO>> updateVehicleStatus(
            @PathVariable Long vehicleId,
            @RequestParam Long newStatus,
            @RequestParam(required = false) String motivo
    ) {
        VehicleDTO updated = vehicleService.updateVehicleStatus(vehicleId, newStatus, motivo);
        return ResponseEntity.ok(ApiResponse.success("Estado actualizado correctamente", updated));
    }

}
