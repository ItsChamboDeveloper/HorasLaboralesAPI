package HorasLaborales.demo.Controllers.VehicleModels;

import HorasLaborales.demo.Models.ApiResponse.ApiResponse;
import HorasLaborales.demo.Models.DTO.VehicleModels.VehicleModelDTO;
import HorasLaborales.demo.Services.VehicleModels.VehicleModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicleModels")
@CrossOrigin("*")
public class VehicleModelController {

    @Autowired
    private VehicleModelService vehicleModelService;

    //*** MÉTODO PARA OBTENER TODOS LOS MODELOS (por si hace falta en algún listado general) ***\\
    @GetMapping("/getAllModels")
    public ResponseEntity<ApiResponse<List<VehicleModelDTO>>> getAllModels() {
        return ResponseEntity.ok(ApiResponse.success("Modelos consultados correctamente", vehicleModelService.getAllModels()));
    }

    //*** MÉTODO QUE LLENA EL SEGUNDO COMBOBOX (modelo) SEGÚN LA MARCA ELEGIDA ***\\
    @GetMapping("/getModelsByBrand/{brandId}")
    public ResponseEntity<ApiResponse<List<VehicleModelDTO>>> getModelsByBrand(@PathVariable Long brandId) {
        List<VehicleModelDTO> models = vehicleModelService.getModelsByBrandId(brandId);
        return ResponseEntity.ok(ApiResponse.success("Modelos consultados correctamente", models));
    }
}
