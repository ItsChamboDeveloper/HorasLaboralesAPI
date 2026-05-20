package HorasLaborales.demo.Controllers.Vehicles;

import HorasLaborales.demo.Models.ApiResponse.ApiResponse;
import HorasLaborales.demo.Models.DTO.Vehicles.VehicleDTO;
import HorasLaborales.demo.Services.Vehicle.VehicleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/vehiculos", "/api/vehiculos"})
@CrossOrigin("*")
public class VehiculoWorkflowController {

    @Autowired
    private VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<ApiResponse<VehicleDTO>> crearVehiculo(@Valid @RequestBody VehicleDTO json) {
        VehicleDTO saved = vehicleService.crearVehiculo(json);
        return ResponseEntity.ok(ApiResponse.success("Vehículo registrado exitosamente", saved));
    }

    @PostMapping("/{id}/revision-animador")
    public ResponseEntity<Map<String, Object>> revisionAnimador(
            @PathVariable Long id,
            @RequestParam boolean aprobado
    ) {
        vehicleService.revisionAnimador(id, aprobado);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Revisión de animador procesada exitosamente"
        ));
    }

    @PostMapping("/{id}/revision-coordinadora")
    public ResponseEntity<Map<String, Object>> revisionCoordinadora(
            @PathVariable Long id,
            @RequestParam boolean aprobado
    ) {
        vehicleService.revisionCoordinadora(id, aprobado);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Revisión de coordinadora procesada exitosamente"
        ));
    }
}
