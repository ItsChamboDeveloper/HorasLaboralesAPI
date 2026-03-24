package HorasLaborales.demo.Controllers.Observation;

import HorasLaborales.demo.Models.DTO.Observations.ObservationDTO;
import HorasLaborales.demo.Services.Observations.ObservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/observations")
@CrossOrigin
public class ObservationController {

    @Autowired
    private ObservationService observationService;

    @PostMapping("/create")
    public ResponseEntity<?> createObservation(@RequestBody ObservationDTO body){
        try{
            ObservationDTO created = observationService.createObservation(body);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Observación creada correctamente",
                    "observation", created
            ));
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/workOrder/{workOrderId}")
    public ResponseEntity<?> getObservationsByWorkOrder(@PathVariable Long workOrderId){
        try{
            List<ObservationDTO> list = observationService.getObservationsByWorkOrderId(workOrderId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "observations", list
            ));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

}
