package HorasLaborales.demo.Exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        Map<String, String> error = new HashMap<>();
        String message = ex.getMostSpecificCause().getMessage();
        
        if (message != null) {
            if (message.contains("Duplicate entry")) {
                if (message.contains("student_card")) {
                    error.put("message", "El número de carnet ya está registrado.");
                } else if (message.contains("email")) {
                    error.put("message", "El correo electrónico ya está registrado.");
                } else if (message.contains("plate_number") || message.contains("placa")) {
                    error.put("message", "El número de placa ya está registrado.");
                } else if (message.contains("circulation_card_number") || message.contains("tarjeta")) {
                    error.put("message", "El número de tarjeta de circulación ya está registrado.");
                } else if (message.contains("owner_dui") || message.contains("dui")) {
                    error.put("message", "El DUI ya está registrado.");
                } else if (message.contains("vin")) {
                    error.put("message", "El número VIN ya está registrado.");
                } else {
                    error.put("message", "El registro ya existe en el sistema.");
                }
            } else {
                error.put("message", "Error de integridad de datos: " + message);
            }
        } else {
            error.put("message", "Ocurrió un error en la base de datos.");
        }
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage() != null ? ex.getMessage() : "Error interno del servidor");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
