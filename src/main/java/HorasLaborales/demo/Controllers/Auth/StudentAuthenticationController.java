package HorasLaborales.demo.Controllers.Auth;

import HorasLaborales.demo.Entities.Students.StudentEntity;
import HorasLaborales.demo.Models.DTO.Students.StudentDTO;
import HorasLaborales.demo.Services.Auth.EstudentsAuth.StudentAuthenticationService;
import HorasLaborales.demo.Utils.JWT.JWTUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/studentsAuth")
public class StudentAuthenticationController {

    /**
     * Servicio para la autenticación de estudiantes.
     */
    @Autowired
    private StudentAuthenticationService studentAuthenticationService;

    /**
     * Utilidad para la generación y validación de tokens JWT.
     */
    @Autowired
    private JWTUtils jwtUtils;

    /**
     * Endpoint para el inicio de sesión de estudiantes.
     * Valida las credenciales y genera un token JWT si son correctas.
     *
     * @param data     DTO con los datos de inicio de sesión del estudiante (email y password)
     * @param response HttpServletResponse para agregar la cookie del token
     * @return ResponseEntity con el resultado del inicio de sesión
     */
    @PostMapping("/studentLogin")
    private ResponseEntity<String> studentLogin(@RequestBody StudentDTO data, HttpServletResponse response) {
        System.out.println("Se está intentando iniciar sesión con: " + data.getEmail());

        // Validación de credenciales
        if (data.getEmail() == null ||
                data.getEmail().isBlank() ||
                data.getPassword() == null ||
                data.getPassword().isBlank()) {
            return ResponseEntity.status(401).body("Error: Credenciales incompletas");
        }

        // Verificación de credenciales y generación de token
        if (studentAuthenticationService.studentLogin(data.getEmail(), data.getPassword())) {
            addTokenCookie(response, data.getEmail()); // ← Pasar solo el correo
            return ResponseEntity.ok("Inicio de sesión exitoso");
        }

        return ResponseEntity.status(401).body("Credenciales incorrectas");
    }

    /**
     * Genera el token JWT y lo guarda en una cookie segura.
     *
     * @param response HttpServletResponse para agregar la cookie
     * @param email    Correo institucional del estudiante
     */
    private void addTokenCookie(HttpServletResponse response, String email) {
        // Obtener el usuario completo de la base de datos
        Optional<StudentEntity> studentOpt = studentAuthenticationService.getStudents(email);

        if (studentOpt.isPresent()) {
            StudentEntity student = studentOpt.get();

            // Obtener roleName de forma segura (como se hace en el auth de instructors)
            String roleName = null;
            if (student.getRoleId() != null) {
                try {
                    roleName = student.getRoleId().getRoleName();
                } catch (Exception ignored) {
                    roleName = null;
                }
            }

            // Obtener group (grade) de forma segura
            String group = null;
            if (student.getGradeId() != null) {
                try {
                    group = String.valueOf(student.getGradeId().getGradeGroup());
                } catch (Exception ignored) {
                    group = null;
                }
            }

            String token = jwtUtils.create(
                    String.valueOf(student.getStudentId()),
                    student.getEmail(),
                    roleName, // ahora pasamos el nombre del rol como en instructors
                    null, // Nivel no utilizado para estudiantes
                    group  // Grupo del estudiante (puede ser null)
            );

            String cookieValue = String.format(
                    "authToken=%s; " +
                            "Path=/; " +
                            "HttpOnly; " +
                            "Secure=false; " +
                            "SameSite=None; " +
                            "MaxAge=86400; ",
                    token
            );

            System.out.println("Cookie creada" + cookieValue);
            response.addHeader("Set-Cookie", cookieValue);
            response.addHeader("Access-Control-Expose-Headers", "Set-Cookie");
        }
    }

    /**
     * Obtiene los datos del estudiante actualmente autenticado.
     *
     * @param authentication Objeto de autenticación proporcionado por Spring Security
     * @return ResponseEntity con los datos del estudiante autenticado o mensaje de error
     */
    @GetMapping("/meStudent")
    public ResponseEntity<?> getCurrentStudent(Authentication authentication) {
        try {
            // Verifica si el usuario está autenticado
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                                "authenticated", false,
                                "message", "No autenticado"
                        ));
            }

            // Maneja diferentes tipos de principal (UserDetails o nombre de usuario)
            String username;
            Collection<? extends GrantedAuthority> authorities;
            if (authentication.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                username = userDetails.getUsername();
                authorities = userDetails.getAuthorities();
            } else {
                username = authentication.getName();
                authorities = authentication.getAuthorities();
            }

            // Busca el estudiante por su nombre de usuario (correo)
            Optional<StudentEntity> studentOpt = studentAuthenticationService.getStudents(username);

            if (studentOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "authenticated", false,
                                "message", "Estudiante no encontrado"
                        ));
            }

            StudentEntity student = studentOpt.get();

            // Devuelve los datos del estudiante autenticado
            return ResponseEntity.ok(Map.of(
                    "authenticated", true,
                    "student", Map.of(
                            "id", student.getStudentId(),
                            "studentCard", student.getStudentCard(),
                            "names", student.getFirstName(),
                            "lastNames", student.getLastName(),
                            "email", student.getEmail(),
                            "group", student.getGradeId().getGradeGroup(),
                            "authorities", authorities.stream()
                                    .map(GrantedAuthority::getAuthority)
                                    .collect(Collectors.toList())
                    )
            ));
        } catch (Exception e) {
            // Manejo de errores inesperados
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "authenticated", false,
                            "message", "Error obteniendo datos del estudiante"
                    ));
        }
    }

    @PostMapping("/logoutStudent")
    public ResponseEntity<String> logoutStudent(HttpServletRequest request, HttpServletResponse response) {
        // Crear cookie de expiración
        String cookieValue = "authToken=; Path=/; HttpOnly; Secure=false; SameSite=None; MaxAge=0;";

        response.addHeader("Set-Cookie", cookieValue);
        response.addHeader("Access-Control-Expose-Headers", "Set-Cookie");

        return ResponseEntity.ok()
                .body("Logout exitoso");
    }

}
