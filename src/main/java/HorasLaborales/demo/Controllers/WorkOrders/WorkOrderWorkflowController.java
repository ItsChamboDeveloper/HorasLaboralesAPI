package HorasLaborales.demo.Controllers.WorkOrders;

import HorasLaborales.demo.Models.DTO.WorkOrders.WorkOrderDTO;
import HorasLaborales.demo.Services.WorkOrders.WorkOrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping({"/work-orders", "/api/work-orders"})
@CrossOrigin("*")
public class WorkOrderWorkflowController {

    @Autowired
    private WorkOrderService workOrderService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearWorkOrder(@Valid @RequestBody WorkOrderDTO json) {
        WorkOrderDTO saved = workOrderService.crearWorkOrder(json);
        return ResponseEntity.ok(Map.of(
                "Estado", "Completado",
                "data", saved
        ));
    }

    @PostMapping("/{id}/revision-animador")
    public ResponseEntity<Map<String, Object>> revisionAnimador(
            @PathVariable Long id,
            @RequestParam boolean aprobado
    ) {
        workOrderService.revisionAnimador(id, aprobado);
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
        workOrderService.revisionCoordinadora(id, aprobado);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Revisión de coordinadora procesada exitosamente"
        ));
    }

    @PostMapping("/{id}/accion-estudiante")
    public ResponseEntity<Map<String, Object>> registrarAccionEstudiante(
            @PathVariable Long id,
            @RequestParam String accion,
            @RequestParam String comentario
    ) {
        workOrderService.registrarObservacionOFinalizar(id, accion, comentario);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Acción de estudiante procesada exitosamente"
        ));
    }
}
