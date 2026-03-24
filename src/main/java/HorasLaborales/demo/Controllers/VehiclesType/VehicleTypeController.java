package HorasLaborales.demo.Controllers.VehiclesType;

import HorasLaborales.demo.Exceptions.Grades.ExceptionGradeNotFound;
import HorasLaborales.demo.Models.ApiResponse.ApiResponse;
import HorasLaborales.demo.Models.DTO.VehicleType.VehicleTypeDTO;
import HorasLaborales.demo.Services.VehicleType.VehicleTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/vehicleTypes")
@CrossOrigin("*")
public class VehicleTypeController {

    @Autowired
    private VehicleTypeService vehicleTypeService;


    //*** MÉTODO PARA OBTENER TODOS LOS TIPOS DE VEHÍCULOS ***\\
    @GetMapping("/getAllVehiclesTypes")
    public ResponseEntity<ApiResponse<List<VehicleTypeDTO>>> getAllVehiclesTypes() {
        List<VehicleTypeDTO> vehicleTypeDTO = vehicleTypeService.getAllVehicleTypes();
        if (vehicleTypeDTO == null || vehicleTypeDTO.isEmpty()) {
            throw new ExceptionGradeNotFound("No se encontraron ningún tipo de auto");
        }
        return ResponseEntity.ok(ApiResponse.success("Tipo de auto consultados correctamente", vehicleTypeDTO));
    }

//    public ResponseEntity<List<VehicleTypeDTO>> getAllRoles() {
//        return ResponseEntity.ok(service.getAllRoles());
//    }

//    @GetMapping("/page")
//    public ResponseEntity<Page<VehicleTypeDTO>> getAllPaged(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size
//    ) {
//        return ResponseEntity.ok(service.getAllRoles(page, size));
//    }
//
//
//    @PostMapping
//    public ResponseEntity<VehicleTypeDTO> insert(@Valid @RequestBody VehicleTypeDTO dto) {
//        return ResponseEntity.ok(service.insert(dto));
//    }
//
//
//    @PutMapping("/{id}")
//    public ResponseEntity<VehicleTypeDTO> update(
//            @PathVariable Long id,
//            @Valid @RequestBody VehicleTypeDTO dto
//    ) {
//        return ResponseEntity.ok(service.update(id, dto));
//    }
//
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<String> delete(@PathVariable Long id) {
//        boolean deleted = service.delete(id);
//        if (deleted) {
//            return ResponseEntity.ok("Tipo de vehículo eliminado correctamente");
//        } else {
//            return ResponseEntity.badRequest().body("No se encontró el tipo de vehículo con ID: " + id);
//        }
//    }

}
