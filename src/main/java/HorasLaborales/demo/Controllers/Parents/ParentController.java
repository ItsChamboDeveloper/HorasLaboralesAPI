package HorasLaborales.demo.Controllers.Parents;

import HorasLaborales.demo.Exceptions.Parents.ExceptionParentDontInsert;
import HorasLaborales.demo.Models.ApiResponse.ApiResponse;
import HorasLaborales.demo.Models.DTO.Parents.ParentDTO;
import HorasLaborales.demo.Services.Parents.ParentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/parents")
@CrossOrigin("*")
public class ParentController {

    @Autowired
    private ParentService parentService;

    //*** MÉTODO PARA OBTENER TODOS LOS PAPÁS/MAMÁS CON PAGINACIÓN ***\\
    @GetMapping("/getAllParents")
    public ResponseEntity<ApiResponse<Page<ParentDTO>>> getAllParents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (size <= 0 || size > 50) {
            return ResponseEntity.badRequest().body(null);
        }

        Page<ParentDTO> parents = parentService.getAllParents(page, size);
        return ResponseEntity.ok(ApiResponse.success("Datos consultados correctamente", parents));
    }

    //*** MÉTODO PARA OBTENER UN PAPÁ/MAMÁ POR ID ***\\
    @GetMapping("/getParentById/{id}")
    public ResponseEntity<?> getParentById(@PathVariable Long id) {
        ParentDTO parentDTO = parentService.getParentById(id);
        if (parentDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", "NOT FOUND",
                            "message", "Papá/mamá no encontrado"
                    ));
        }
        return ResponseEntity.ok(ApiResponse.success("Papá/mamá encontrado", parentDTO));
    }

    //*** MÉTODO PARA REGISTRAR UN NUEVO PAPÁ/MAMÁ ***\\
    // NOTA: decide si este endpoint debe quedar abierto para que un papá se
    // autorregistre, o protegido para que solo Coordinación/Animador puedan
    // dar de alta cuentas. Tal como está en SecurityConfig, queda "authenticated()",
    // es decir, requiere sesión (de instructor) para crear una cuenta de papá.
    @PostMapping("/newParent")
    public ResponseEntity<ApiResponse<ParentDTO>> createParent(@Valid @RequestBody ParentDTO json) {
        if (json == null) {
            throw new ExceptionParentDontInsert("Error al recibir y procesar la información del papá/mamá");
        }

        ParentDTO parentSaved = parentService.createParent(json);

        if (parentSaved == null) {
            throw new ExceptionParentDontInsert("El papá/mamá no pudo ser registrado debido a un problema en los datos");
        }

        return ResponseEntity.ok(ApiResponse.success("Papá/mamá registrado exitosamente", parentSaved));
    }

    //*** MÉTODO PARA ACTUALIZAR UN PAPÁ/MAMÁ EXISTENTE ***\\
    @PutMapping("/updateParent/{id}")
    public ResponseEntity<?> updateParent(@PathVariable Long id, @Valid @RequestBody ParentDTO json) {
        try {
            ParentDTO updated = parentService.updateParent(id, json);
            return ResponseEntity.ok(ApiResponse.success("Papá/mamá actualizado exitosamente", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    //*** MÉTODO PARA ELIMINAR UN PAPÁ/MAMÁ ***\\
    @DeleteMapping("/deleteParent/{id}")
    public ResponseEntity<Map<String, Object>> deleteParent(@PathVariable Long id) {
        try {
            if (!parentService.deleteParent(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "Error", "NOT FOUND",
                                "Mensaje", "El papá/mamá no fue encontrado"
                        ));
            }
            return ResponseEntity.ok().body(Map.of(
                    "status", "Proceso completado",
                    "mensaje", "Papá/mamá eliminado exitosamente"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "Error",
                    "message", "Error al eliminar el papá/mamá",
                    "detail", e.getMessage()
            ));
        }
    }
}
