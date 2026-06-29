package HorasLaborales.demo.Controllers.Students;

import HorasLaborales.demo.Exceptions.Students.ExceptionStudentDontInsert;
import HorasLaborales.demo.Models.ApiResponse.ApiResponse;
import HorasLaborales.demo.Models.DTO.Students.StudentDTO;
import HorasLaborales.demo.Services.Students.StudentService;
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
@RequestMapping("/api/students")
public class StudentController {

    @Autowired // Inyección automática del servicio de estudiantes
    private StudentService studentService; // Servicio que maneja la lógica de negocio relacionada con los estudiantes

    // *** MÉTODO PARA OBTENER TODOS LOS ESTUDIANTES CON PAGINACIÓN ***\\

    /**
     * Obtiene todos los estudiantes del sistema con paginación.
     *
     * @param page Número de página solicitada.
     * @param size Cantidad de registros por página.
     * @return ResponseEntity con un ApiResponse que contiene una página de
     *         estudiantes (StudentDTO).
     *         Retorna error si los parámetros son inválidos o si ocurre un problema
     *         en el servicio.
     */

    @GetMapping("/getAllStudents")
    public ResponseEntity<ApiResponse<Page<StudentDTO>>> getAllStudents(
            @RequestParam(defaultValue = "0") int page, // Página por defecto 0
            @RequestParam(defaultValue = "10") int size // Tamaño por defecto 10
    ) {
        // Validación del tamaño de página: debe estar entre 1 y 50
        if (size <= 0 || size > 50) {
            ResponseEntity.badRequest().body(Map.of(
                    "status", "La paginación de datos debe estar entre 1 y 50"));
            return ResponseEntity.ok(null); // Devuelve nulo si la validación falla
        }

        // Obtiene los estudiantes paginados desde el servicio
        Page<StudentDTO> students = studentService.getAllStudents(page, size);

        // Si ocurre un error al obtener los datos
        if (students == null) {
            ResponseEntity.badRequest().body(Map.of(
                    "status", "Error al obtener los datos del estudiante"));
        }

        // Retorna respuesta exitosa con los datos
        return ResponseEntity.ok(ApiResponse.success("Datos consultados correctamente", students));
    }

    // *** MÉTODO PARA OBTENER UN ESTUDIANTE POR ID ***\\
    /**
     * Obtiene un estudiante por su ID.
     *
     * @param id ID del estudiante a buscar.
     * @return ResponseEntity con un ApiResponse que contiene el estudiante
     *         (StudentDTO) si se encuentra.
     *         Retorna error 404 si no se encuentra el estudiante.
     */
    @GetMapping("/getStudentById/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable Long id) {
        StudentDTO student = studentService.getStudentById(id);
        if (student == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", "NOT FOUND",
                            "message", "Estudiante no encontrado"));
        }
        return ResponseEntity.ok(ApiResponse.success("Estudiante encontrado", student));
    }

    // *** MÉTODO PARA OBTENER UN ESTUDIANTE POR CARNET ***\\
    /**
     * Obtiene un estudiante por su carnet.
     * 
     * @param studentCard Carnet del estudiante a buscar.
     * @return ResponseEntity con un ApiResponse que contiene el estudiante
     *         encontrado (StudentDTO).
     *         Si no se encuentra el estudiante, retorna un mensaje de error con
     *         estado 404.
     */
    @GetMapping("/getStudentByStudenCard/{studentCard}")
    public ResponseEntity<?> getStudentByStudentCard(@PathVariable String studentCard) {
        StudentDTO student = studentService.getStudentByStudentCard(studentCard);
        if (student == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", "NOT FOUND",
                            "message", "Estudiante no encontrado"));
        }
        return ResponseEntity.ok(ApiResponse.success("Estudiante encontrado", student));
    }

    // *** MÉTODO PARA AGREGAR UN NUEVO ESTUDIANTE ***\\

    /**
     * Crea un nuevo estudiante en el sistema.
     *
     * @param json Objeto StudentDTO con los datos del estudiante a registrar.
     * @return ResponseEntity con un ApiResponse que contiene el estudiante creado.
     *         Lanza excepción si el JSON es nulo o si falla el guardado.
     */

    @PostMapping("/newStudent")
    public ResponseEntity<ApiResponse<StudentDTO>> createStudent(@Valid @RequestBody StudentDTO json) {
        // Verifica si el JSON recibido es nulo
        if (json == null) {
            throw new ExceptionStudentDontInsert("Error al recibir y procesar la información del estudiante");
        }

        // Intenta guardar el estudiante usando el servicio
        StudentDTO studentSaved = studentService.createStudent(json);

        // Si el estudiante no se guarda correctamente
        if (studentSaved == null) {
            throw new ExceptionStudentDontInsert(
                    "El estudiante no pudo ser registrado debido a un problema en los datos");
        }

        // Retorna respuesta exitosa con el estudiante guardado
        return ResponseEntity.ok(ApiResponse.success("Estudiante registrado exitosamente", studentSaved));
    }

    // *** MÉTODO PARA ACTUALIZAR UN ESTUDIANTE EXISTENTE ***\\

    /**
     * Actualiza los datos de un estudiante existente según su ID.
     *
     * @param id            ID del estudiante a actualizar.
     * @param json          Objeto StudentDTO con los nuevos datos del estudiante.
     * @param bindingResult Objeto que contiene errores de validación si los hay.
     * @return ResponseEntity con el estudiante actualizado si la operación fue
     *         exitosa.
     *         Si hay errores de validación, devuelve un mapa con los errores.
     *         Si ocurre una excepción, devuelve un mensaje de error.
     */
    @PutMapping("/updateStudent/{id}")
    public ResponseEntity<?> updateStudent(@Valid @PathVariable Long id, @RequestBody StudentDTO json,
            BindingResult bindingResult) {
        // Si hay errores de validación en el DTO recibido
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();

            // Recorre los errores y los guarda en un mapa con nombre del campo y mensaje
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

            // Retorna los errores al cliente
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            // Intenta actualizar el estudiante con el ID proporcionado
            StudentDTO studentUpdated = studentService.updateStudent(id, json);
            return ResponseEntity.ok(studentUpdated); // Respuesta exitosa
        } catch (Exception e) {
            // Si ocurre algún error durante la actualización
            return ResponseEntity.badRequest().body("Error al modificar el estudiante");
        }
    }

    // *** MÉTODO PARA ELIMINAR UN ESTUDIANTE POR ID ***\\

    /**
     * Elimina un estudiante del sistema según su ID.
     *
     * @param id ID del estudiante que se desea eliminar.
     * @return ResponseEntity con mensaje de éxito si se elimina correctamente.
     *         Si no se encuentra el estudiante, retorna un mensaje de error con
     *         estado 404.
     *         Si ocurre un error inesperado, retorna un mensaje de error con estado
     *         500.
     */
    @DeleteMapping("/deleteStudent/{id}")
    public ResponseEntity<Map<String, Object>> deleteStudent(
            @PathVariable Long id) {
        try {
            // Intenta eliminar el estudiante usando el servicio
            if (!studentService.deleteStudent(id)) {
                // Si el estudiante no fue encontrado o no se pudo eliminar
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .header("Error", "El estudiante no fue encontrado")
                        .body(Map.of(
                                "Error", "NOT FOUND",
                                "Mensaje", "El estudiante no fue encontrado",
                                "Fecha y hora", Instant.now().toString()));
            }

            // Si se elimina correctamente, devuelve mensaje de éxito
            return ResponseEntity.ok().body(Map.of(
                    "status", "Proceso completado",
                    "mensaje", "Estudiante eliminado exitosamente"));
        } catch (Exception e) {
            // Captura cualquier error inesperado durante la eliminación
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "Error", // Estado general
                    "message", "Error al eliminar el estudiante", // Mensaje para el usuario
                    "detail", e.getMessage() // Detalles técnicos (para debugging)
            ));
        }
    }
}
