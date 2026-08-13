package HorasLaborales.demo.Controllers.Brands;

import HorasLaborales.demo.Exceptions.Brands.ExceptionBrandNotFound;
import HorasLaborales.demo.Models.ApiResponse.ApiResponse;
import HorasLaborales.demo.Models.DTO.Brands.BrandDTO;
import HorasLaborales.demo.Services.Brands.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
@CrossOrigin("*")
public class BrandController {

    @Autowired
    private BrandService brandService;

    //*** MÉTODO PARA OBTENER TODAS LAS MARCAS (primer combobox) ***\\
    @GetMapping("/getAllBrands")
    public ResponseEntity<ApiResponse<List<BrandDTO>>> getAllBrands() {
        List<BrandDTO> brands = brandService.getAllBrands();
        if (brands == null || brands.isEmpty()) {
            throw new ExceptionBrandNotFound("No se encontraron marcas registradas");
        }
        return ResponseEntity.ok(ApiResponse.success("Marcas consultadas correctamente", brands));
    }
}
