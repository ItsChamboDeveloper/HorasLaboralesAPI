package HorasLaborales.demo.Controllers.Auth;

import HorasLaborales.demo.Entities.Parents.ParentEntity;
import HorasLaborales.demo.Models.DTO.Parents.ParentDTO;
import HorasLaborales.demo.Services.Auth.ParentsAuth.ParentAuthenticationService;
import HorasLaborales.demo.Services.Parents.ParentService;
import HorasLaborales.demo.Utils.JWT.JWTUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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
@RequestMapping("/api/parentsAuth")
public class ParentAuthenticationController {

    /**
     * Servicio para la autenticación de papás/mamás.
     */
    @Autowired
    private ParentAuthenticationService parentAuthenticationService;

    /**
     * Servicio para crear la cuenta de papá/mamá durante el autoregistro.
     */
    @Autowired
    private ParentService parentService;

    /**
     * Utilidad para la generación y validación de tokens JWT.
     */
    @Autowired
    private JWTUtils jwtUtils;

    /**
     * Endpoint público de autoregistro para papás/mamás. A diferencia de
     * POST /api/parents/newParent (que requiere sesión de instructor), este
     * permite que el propio dueño del vehículo cree su cuenta sin depender
     * de que alguien más la haya dado de alta antes. Al terminar, deja al
     * papá/mamá con sesión iniciada (misma cookie que usa el login).
     */
    @PostMapping("/registerParent")
    public ResponseEntity<String> registerParent(@Valid @RequestBody ParentDTO data, HttpServletResponse response) {
        if (data.getEmail() == null || data.getEmail().isBlank() ||
                data.getPassword() == null || data.getPassword().isBlank() ||
                data.getFirstName() == null || data.getFirstName().isBlank() ||
                data.getLastName() == null || data.getLastName().isBlank() ||
                data.getDui() == null || data.getDui().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Datos incompletos para el registro");
        }

        parentService.createParent(data);
        addTokenCookie(response, data.getEmail());

        return ResponseEntity.ok("Registro exitoso");
    }

    /**
     * Endpoint para el inicio de sesión de papás/mamás.
     * Valida las credenciales y genera un token JWT si son correctas.
     */
    @PostMapping("/parentLogin")
    private ResponseEntity<String> parentLogin(@RequestBody ParentDTO data, HttpServletResponse response) {
        System.out.println("Se está intentando iniciar sesión (papá/mamá) con: " + data.getEmail());

        if (data.getEmail() == null ||
                data.getEmail().isBlank() ||
                data.getPassword() == null ||
                data.getPassword().isBlank()) {
            return ResponseEntity.status(401).body("Error: Credenciales incompletas");
        }

        if (parentAuthenticationService.parentLogin(data.getEmail(), data.getPassword())) {
            addTokenCookie(response, data.getEmail());
            return ResponseEntity.ok("Inicio de sesión exitoso");
        }

        return ResponseEntity.status(401).body("Credenciales incorrectas");
    }

    /**
     * Genera el token JWT y lo guarda en una cookie segura.
     */
    private void addTokenCookie(HttpServletResponse response, String email) {
        Optional<ParentEntity> parentOpt = parentAuthenticationService.getParent(email);

        if (parentOpt.isPresent()) {
            ParentEntity parent = parentOpt.get();

            // Los papás no tienen fila en TBROLES, así que se manda un rol fijo
            // "Padre". El JwtCookieAuthFilter arma la autoridad ROLE_Padre a
            // partir de este valor (no requiere tocar el mapeo de roles).
            String token = jwtUtils.create(
                    String.valueOf(parent.getParentId()),
                    parent.getEmail(),
                    "Padre",
                    null, // Nivel no aplica para papás
                    null  // Grupo/grado no aplica para papás
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

            System.out.println("Cookie creada (papá/mamá) " + cookieValue);
            response.addHeader("Set-Cookie", cookieValue);
            response.addHeader("Access-Control-Expose-Headers", "Set-Cookie");
        }
    }

    /**
     * Obtiene los datos del papá/mamá actualmente autenticado.
     */
    @GetMapping("/meParent")
    public ResponseEntity<?> getCurrentParent(Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                                "authenticated", false,
                                "message", "No autenticado"
                        ));
            }

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

            Optional<ParentEntity> parentOpt = parentAuthenticationService.getParent(username);

            if (parentOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "authenticated", false,
                                "message", "Papá/mamá no encontrado"
                        ));
            }

            ParentEntity parent = parentOpt.get();

            return ResponseEntity.ok(Map.of(
                    "authenticated", true,
                    "parent", Map.of(
                            "id", parent.getParentId(),
                            "names", parent.getFirstName(),
                            "lastNames", parent.getLastName(),
                            "email", parent.getEmail(),
                            "authorities", authorities.stream()
                                    .map(GrantedAuthority::getAuthority)
                                    .collect(Collectors.toList())
                    )
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "authenticated", false,
                            "message", "Error obteniendo datos del papá/mamá"
                    ));
        }
    }

    @PostMapping("/logoutParent")
    public ResponseEntity<String> logoutParent(HttpServletRequest request, HttpServletResponse response) {
        String cookieValue = "authToken=; Path=/; HttpOnly; Secure=false; SameSite=None; MaxAge=0;";

        response.addHeader("Set-Cookie", cookieValue);
        response.addHeader("Access-Control-Expose-Headers", "Set-Cookie");

        return ResponseEntity.ok()
                .body("Logout exitoso");
    }

    @PutMapping("/changePassword/{id}")
    public ResponseEntity<?> changeParentPassword(@PathVariable Long id, @RequestBody Map<String, String> passwords) {
        try {
            String oldPassword = passwords.get("oldPassword");
            String newPassword = passwords.get("newPassword");

            if (oldPassword == null || newPassword == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Faltan datos de contraseña"));
            }

            boolean success = parentAuthenticationService.changeParentPassword(id, oldPassword, newPassword);
            if (success) {
                return ResponseEntity.ok(Map.of("message", "Contraseña actualizada exitosamente"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Contraseña actual incorrecta"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error al cambiar la contraseña"));
        }
    }

}
