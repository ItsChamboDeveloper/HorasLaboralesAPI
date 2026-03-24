package HorasLaborales.demo.Controllers.Entries;

import HorasLaborales.demo.Models.DTO.Entries.EntryDTO;
import HorasLaborales.demo.Services.Entries.EntryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController @RequestMapping("/api/entries")
@CrossOrigin("*")
public class EntryController {
    @Autowired
    private EntryService entryService;

    /**
     * Obtiene todas las entradas (Entry) del sistema con paginación.
     *
     * @param page Número de página solicitada.
     * @param size Cantidad de registros por página (máximo 50).
     * @return ResponseEntity con una página de EntryDTO. Si los parámetros son inválidos o no hay datos, retorna error.
     */
    @GetMapping("/getAllEntries")
    private ResponseEntity<Page<EntryDTO>> getDataVehicleEntries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        if (size <= 0 || size > 50) {
            ResponseEntity.badRequest().body(Map.of(
                    "status", "El tamaño de la página debe estar entre 1 y 50"
            ));
            return ResponseEntity.ok(null);
        }
        Page<EntryDTO> entries = entryService.getAllEntries(page, size);
        if (entries == null) {
            ResponseEntity.badRequest().body(Map.of(
                    "status", "No hay entradas registradas"
            ));
        }
        return ResponseEntity.ok(entries);
    }

    /**
     * Crea una nueva entrada (Entry) en el sistema.
     *
     * @param json    Objeto EntryDTO con los datos de la entrada a registrar.
     * @param request Objeto HttpServletRequest para información adicional de la petición.
     * @return ResponseEntity con un mapa que contiene el estado y los datos de la entrada creada.
     * Si ocurre un error, retorna un mensaje de error y detalles de la excepción.
     */
    @PostMapping("/newEntry")
    private ResponseEntity<Map<String, Object>> createEntry(@Valid @RequestBody EntryDTO json, HttpServletRequest request) {
        try {
            EntryDTO response = entryService.createEntry(json);
            if (response == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "Error", "Inserción incorrecta",
                        "Estatus", "Inserción incorrecta",
                        "Descripción", "Verifique que los campos estén correctamente ingresados"
                ));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "Estado", "Completado",
                    "data", response
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "message", "error al registrar la entrada",
                            "detail", e.getMessage()
                    ));
        }
    }
}
