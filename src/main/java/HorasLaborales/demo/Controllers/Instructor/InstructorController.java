package HorasLaborales.demo.Controllers.Instructor;

import HorasLaborales.demo.Exceptions.Instructors.ExceptionInstructorDontInsert;
import HorasLaborales.demo.Models.ApiResponse.ApiResponse;
import HorasLaborales.demo.Models.DTO.Instructors.InstructorDTO;
import HorasLaborales.demo.Services.Instructors.InstructorsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/instructors")
public class InstructorController {

    // Inyección automática del servicio de instructores
    @Autowired
    private InstructorsService instructorsService; // Servicio que maneja la lógica de negocio relacionada con los instructores

    //*** MÉTODO PARA OBTENER TODOS LOS INSTRUCTORES CON PAGINACIÓN ***\\

    /**
     * Obtiene todos los instructores del sistema con paginación.
     *
     * @param page Número de página solicitada.
     * @param size Cantidad de registros por página.
     * @return ResponseEntity con un ApiResponse que contiene una página de instructores (InstructorDTO).
     * Retorna error si los parámetros son inválidos o si ocurre un problema en el servicio.
     */

    @GetMapping("/getAllInstructors")
    public ResponseEntity<ApiResponse<Page<InstructorDTO>>> getAllInstructors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (size <= 0 || size > 50) {
            ResponseEntity.badRequest().body(Map.of(
                    "status", "La paginación de datos debe estar entre 1 y 50"
            ));
            return ResponseEntity.ok(null);
        }

        Page<InstructorDTO> instructor = instructorsService.getAllInstructors(page, size);

        if (instructor == null) {
            ResponseEntity.badRequest().body(Map.of(
                    "status", "Error al obtener los datos del instructor"
            ));
        }

        return ResponseEntity.ok(ApiResponse.success("Datos del instructor consultados correctamente", instructor));
    }

    //*** MÉTODO PARA OBTENER UN INSTRUCTOR POR ID ***\\
    /**
     * Obtiene un instructor por su ID.
     *
     * @param id ID del instructor a buscar.
     * @return ResponseEntity con un ApiResponse que contiene el instructor encontrado (InstructorDTO).
     * Si no se encuentra el instructor, retorna un mensaje de error con estado 404.
     */
    @GetMapping("/getInstructorById/{id}")
    public ResponseEntity<?> getInstructorById(@PathVariable Long id) {
        InstructorDTO instructor = instructorsService.getInstructorById(id);
        if (instructor == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", "NOT FOUND",
                            "message", "Instructor no encontrado"
                    ));
        }
        return ResponseEntity.ok(ApiResponse.success("Instructor encontrado", instructor));
    }

    //*** MÉTODO PARA OBTENER UN INSTRUCTOR POR EMAIL ***\\
    /**
     * Obtiene un instructor por su email.
     * @param email Email del instructor a buscar.
     * @return ResponseEntity con un ApiResponse que contiene el instructor encontrado (InstructorDTO).
     * Si no se encuentra el instructor, retorna un mensaje de error con estado 404.
     */
    @GetMapping("/getInstructorByEmail/{email}")
    public ResponseEntity<?> getInstructorByEmail(@PathVariable String email) {
        InstructorDTO instructor = instructorsService.getInstructorByEmail(email);
        if (instructor == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", "NOT FOUND",
                            "message", "Instructor no encontrado"
                    ));
        }
        return ResponseEntity.ok(ApiResponse.success("Instructor encontrado", instructor));
    }

    //*** MÉTODO PARA AGREGAR UN NUEVO INSTRUCTOR ***\\

    /**
     * Crea un nuevo instructor en el sistema.
     *
     * @param json Objeto InstructorDTO con los datos del instructor a registrar.
     * @return ResponseEntity con un ApiResponse que contiene el instructor creado.
     * Lanza excepción si el JSON es nulo o si falla el guardado.
     */

    @PostMapping("/newInstructor")
    public ResponseEntity<ApiResponse<InstructorDTO>> createInstructor(@Valid @RequestBody InstructorDTO json) {
        if (json == null) {
            throw new ExceptionInstructorDontInsert("Error al recibir y procesar la información del instructor");
        }

        InstructorDTO instructorSaved = instructorsService.createInstructor(json);

        if (instructorSaved == null) {
            throw new ExceptionInstructorDontInsert("El instructor no pudo ser registrado debido a un problema en los datos");
        }

        return ResponseEntity.ok(ApiResponse.success("Instructor registrado exitosamente", instructorSaved));
    }

    //*** MÉTODO PARA ACTUALIZAR UN INSTRUCTOR EXISTENTE ***\\

    /**
     * Actualiza los datos de un instructor existente según su ID.
     *
     * @param id            ID del instructor a actualizar.
     * @param json          Objeto InstructorDTO con los nuevos datos del instructor.
     * @param bindingResult Objeto que contiene errores de validación si los hay.
     * @return ResponseEntity con el instructor actualizado si la operación fue exitosa.
     * Si hay errores de validación, devuelve un mapa con los errores.
     * Si ocurre una excepción, devuelve un mensaje de error.
     */
    @PutMapping("/updateInstructor/{id}")
    public ResponseEntity<?> updateInstructor(@Valid @PathVariable Long id, @RequestBody InstructorDTO json, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            InstructorDTO instructorUpdated = instructorsService.updateInstructor(id, json);
            return ResponseEntity.ok(instructorUpdated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al modificar el instructor");
        }
    }

    //*** MÉTODO PARA ELIMINAR UN INSTRUCTOR POR ID ***\\

    /**
     * Elimina un instructor del sistema según su ID.
     *
     * @param id ID del instructor que se desea eliminar.
     * @return ResponseEntity con mensaje de éxito si se elimina correctamente.
     * Si no se encuentra el instructor, retorna un mensaje de error con estado 404.
     * Si ocurre un error inesperado, retorna un mensaje de error con estado 500.
     */
    @DeleteMapping("/deleteInstructor/{id}")
    public ResponseEntity<Map<String, Object>> deleteInstructor(
            @PathVariable Long id
    ) {
        try {
            if (!instructorsService.deleteInstructor(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .header("Error", "El instructor no fue encontrado")
                        .body(Map.of(
                                "Error", "NOT FOUND",
                                "Mensaje", "El instructor no fue encontrado",
                                "Fecha y hora", Instant.now().toString()
                        ));
            }

            return ResponseEntity.ok().body(Map.of(
                    "status", "Proceso completado",
                    "mensaje", "Instructor eliminado exitosamente"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "Error",
                    "message", "Error al eliminar el instructor",
                    "detail", e.getMessage()
            ));
        }
    }
}
