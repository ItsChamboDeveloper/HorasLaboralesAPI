package HorasLaborales.demo.Controllers.Roles;

import HorasLaborales.demo.Exceptions.Grades.ExceptionGradeNotFound;
import HorasLaborales.demo.Models.ApiResponse.ApiResponse;
import HorasLaborales.demo.Models.DTO.Roles.RoleDTO;
import HorasLaborales.demo.Services.Roles.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin("*")
public class RoleController {

    @Autowired
    private RoleService roleService;

    //*** MÉTODO PARA OBTENER TODOS LOS ROLES ***\\
    @GetMapping("/getAllRoles")
    public ResponseEntity<ApiResponse<List<RoleDTO>>> getAllRoles() {
        List<RoleDTO> roles = roleService.getAllRoles();
        if (roles == null || roles.isEmpty()) {
            throw new ExceptionGradeNotFound("No se encontraron roles");
        }
        return ResponseEntity.ok(ApiResponse.success("Roles consultados correctamente", roles));
    }
}
