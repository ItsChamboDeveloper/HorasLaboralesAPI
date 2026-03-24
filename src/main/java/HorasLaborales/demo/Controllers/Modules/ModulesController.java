package HorasLaborales.demo.Controllers.Modules;

import HorasLaborales.demo.Exceptions.Modules.ExceptionModuleDontRegister;
import HorasLaborales.demo.Exceptions.Modules.ExceptionModuleNotFound;
import HorasLaborales.demo.Models.ApiResponse.ApiResponse;
import HorasLaborales.demo.Models.DTO.Modules.ModuleDTO;
import HorasLaborales.demo.Services.Modules.ModuleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController @RequestMapping("/api/modules")
@CrossOrigin("*")
public class ModulesController {
    @Autowired
    private ModuleService moduleService; // Servicio que contiene la lógica de negocio de los módulos

    //*** MÉTODO PARA OBTENER TODOS LOS MÓDULOS CON PAGINACIÓN ***\\

    /**
     * Obtiene todos los módulos registrados en el sistema con soporte de paginación.
     *
     * @param page Número de página solicitada (valor por defecto = 0).
     * @param size Cantidad de registros por página (valor por defecto = 10, máximo permitido = 50).
     * @return ResponseEntity con un ApiResponse que contiene los módulos en formato paginado.
     * @throws ExceptionModuleNotFound si no se encuentran módulos en la base de datos.
     */
    @GetMapping("/getAllModules")
    public ResponseEntity<ApiResponse<Page<ModuleDTO>>> getAllModules(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (size <= 0 || size > 50) {
            return ResponseEntity.badRequest().body(null);
        }

        Page<ModuleDTO> modules = moduleService.getAllModules(page, size);
        // El DTO ya incluye moduleCode e instructor
        if (modules == null || modules.isEmpty()) {
            throw new ExceptionModuleNotFound("No se encontraron módulos");
        }

        return ResponseEntity.ok(ApiResponse.success("Módulos consultados correctamente", modules));
    }

    //*** MÉTODO PARA INSERTAR UN NUEVO MÓDULO ***\\

    /**
     * Registra un nuevo módulo en el sistema.
     *
     * @param dto Objeto ModuleDTO con la información del módulo a crear.
     * @return ResponseEntity con un ApiResponse que contiene el módulo registrado.
     * @throws ExceptionModuleDontRegister si el módulo no puede ser registrado.
     */
    @PostMapping("/newModule")
    public ResponseEntity<ApiResponse<ModuleDTO>> createNewModule(@Valid @RequestBody ModuleDTO dto) {
        if (dto == null) {
            throw new ExceptionModuleDontRegister("Error al recibir la información del módulo");
        }
        // El DTO debe incluir moduleCode e instructorId
        ModuleDTO savedModule = moduleService.insertModule(dto);
        if (savedModule == null) {
            throw new ExceptionModuleDontRegister("No se pudo registrar el módulo");
        }
        return ResponseEntity.ok(ApiResponse.success("Módulo registrado exitosamente", savedModule));
    }

    //*** MÉTODO PARA ACTUALIZAR UN MÓDULO EXISTENTE ***\\

    /**
     * Actualiza los datos de un módulo existente identificado por su ID.
     *
     * @param id            Identificador único del módulo a actualizar.
     * @param dto           Objeto ModuleDTO con los datos actualizados.
     * @param bindingResult Objeto que contiene posibles errores de validación.
     * @return ResponseEntity con un ApiResponse que contiene el módulo actualizado
     * o un mapa de errores si la validación falla.
     * @throws ExceptionModuleNotFound     si el módulo no existe.
     * @throws ExceptionModuleDontRegister si ocurre un error durante la actualización.
     */
    @PutMapping("/updateModule/{id}")
    public ResponseEntity<?> updateModule(@PathVariable Long id, @Valid @RequestBody ModuleDTO dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            ModuleDTO updated = moduleService.updateModule(id, dto);
            return ResponseEntity.ok(ApiResponse.success("Módulo actualizado correctamente", updated));
        } catch (ExceptionModuleNotFound e) {
            return ResponseEntity.status(404).body(Map.of(
                    "Error", "Not Found",
                    "Message", e.getMessage(),
                    "Timestamp", Instant.now().toString()
            ));
        } catch (ExceptionModuleDontRegister | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "Error", "No se pudo actualizar",
                    "Message", e.getMessage(),
                    "Timestamp", Instant.now().toString()
            ));
        }
    }

    //*** MÉTODO PARA ELIMINAR UN MÓDULO POR ID ***\\

    /**
     * Elimina un módulo del sistema a partir de su ID.
     *
     * @param id Identificador único del módulo que se desea eliminar.
     * @return ResponseEntity con un mapa que indica el resultado de la operación:
     * - 200 si el módulo fue eliminado exitosamente.
     * - 404 si el módulo no existe.
     * - 500 si ocurre un error inesperado en el servidor.
     */
    @DeleteMapping("/deleteModule/{id}")
    public ResponseEntity<Map<String, Object>> deleteModule(@PathVariable Long id) {
        try {
            boolean deleted = moduleService.deleteModule(id);
            if (!deleted) {
                return ResponseEntity.status(404).body(Map.of(
                        "Error", "Not Found",
                        "Message", "Módulo no encontrado",
                        "Timestamp", Instant.now().toString()
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "Status", "Proceso completado",
                    "Message", "Módulo eliminado exitosamente"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "Status", "Error",
                    "Message", "Error al eliminar el módulo",
                    "Detail", e.getMessage()
            ));
        }
    }
}
