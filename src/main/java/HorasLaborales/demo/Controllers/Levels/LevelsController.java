package HorasLaborales.demo.Controllers.Levels;

import HorasLaborales.demo.Models.ApiResponse.ApiResponse;
import HorasLaborales.demo.Services.Levels.LevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController @RequestMapping("/api/levels")
@CrossOrigin("*")
public class LevelsController {

    @Autowired
    private LevelService levelService;

    //*** OBTENER TODOS LOS NIVELES ***//
    @GetMapping("/getAllLevels")
    public ResponseEntity<?> getAllLevels() {
        try {
            return ResponseEntity.ok(ApiResponse.success("Datos consultados correctamente", levelService.getAllLevels()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "Error",
                    "message", "No se pudieron obtener los niveles",
                    "detail", e.getMessage()
            ));
        }
    }

//    //*** CREAR UN NUEVO NIVEL ***//
//    @PostMapping("/newLevel")
//    public ResponseEntity<?> createLevel(@Valid @RequestBody LevelsDTO json, BindingResult bindingResult) {
//        // Validación de campos
//        if (bindingResult.hasErrors()) {
//            Map<String, String> errors = new HashMap<>();
//            bindingResult.getFieldErrors().forEach(error ->
//                    errors.put(error.getField(), error.getDefaultMessage()));
//            return ResponseEntity.badRequest().body(errors);
//        }
//
//        try {
//            LevelDTO levelSaved = levelService.insert(json);
//            return ResponseEntity.ok(ApiResponse.success("Nivel registrado exitosamente", levelSaved));
//        } catch (ExceptionLevelDontRegister e) {
//            return ResponseEntity.badRequest().body(Map.of(
//                    "status", "Error",
//                    "message", e.getMessage()
//            ));
//        }
//    }
//
//    //*** ACTUALIZAR UN NIVEL EXISTENTE ***//
//    @PutMapping("/updateLevel/{id}")
//    public ResponseEntity<?> updateLevel(@Valid @PathVariable Long id, @RequestBody LevelDTO json, BindingResult bindingResult) {
//        if (bindingResult.hasErrors()) {
//            Map<String, String> errors = new HashMap<>();
//            bindingResult.getFieldErrors().forEach(error ->
//                    errors.put(error.getField(), error.getDefaultMessage()));
//            return ResponseEntity.badRequest().body(errors);
//        }
//
//        try {
//            LevelDTO levelUpdated = levelService.update(id, json);
//            return ResponseEntity.ok(ApiResponse.success("Nivel actualizado correctamente", levelUpdated));
//        } catch (ExceptionLevelDontRegister e) {
//            return ResponseEntity.badRequest().body(Map.of(
//                    "status", "Error",
//                    "message", e.getMessage()
//            ));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(Map.of(
//                    "status", "Error",
//                    "message", "Error al modificar el nivel"
//            ));
//        }
//    }
//
//    //*** ELIMINAR UN NIVEL POR ID ***//
//    @DeleteMapping("/deleteLevel/{id}")
//    public ResponseEntity<?> deleteLevel(@PathVariable Long id) {
//        try {
//            boolean deleted = levelService.delete(id);
//            if (!deleted) {
//                return ResponseEntity.badRequest().body(Map.of(
//                        "status", "Error",
//                        "message", "Nivel no encontrado"
//                ));
//            }
//            return ResponseEntity.ok(Map.of(
//                    "status", "Proceso completado",
//                    "message", "Nivel eliminado exitosamente"
//            ));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(Map.of(
//                    "status", "Error",
//                    "message", "Error al eliminar el nivel",
//                    "detail", e.getMessage()
//            ));
//        }
//    }

}
