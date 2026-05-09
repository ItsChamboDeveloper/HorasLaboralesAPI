package HorasLaborales.demo.Utils.JWT;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;

@Component
public class JWTUtils {

    @Value("${security.jwt.secret-key}")
    private String jwtSecret;
    @Value("${security.jwt.issuer}")
    private String issuer;
    @Value("${security.jwt.expiration}")
    private long msExpiration;

    private final Logger log = LoggerFactory.getLogger(JWTUtils.class);

    /**
     * Metodo para crear JWT
     *
     * @param id
     * @param email
     * @param role
     * @param level
     * @param grade puede ser null si no aplica (ej: Instructor)
     * @return token JWT como String
     */
    // ✅ CORREGIDO - Compatible con JJWT 0.12.6
    public String create(String id, String email, String role, String level, String grade) {
        SecretKey signingKey = getSecretKey();
        Date now = new Date();
        Date expiration = new Date(now.getTime() + msExpiration);

        var builder = Jwts.builder()
                .id(id)  // ← Método correcto en 0.12.6
                .issuedAt(now)
                .subject(email)
                .claim("id", id)
                .claim("role", role)
                .claim("level", level)
                .issuer(issuer)
                .expiration(msExpiration >= 0 ? expiration : null)
                .signWith(signingKey);

        // ✅ Agregar grade solo si no es null
        if (grade != null) {
            builder.claim("grade", grade);
        }

        return builder.compact();
    }

    // Método para extraer grade del token
    public String extractGrade(String token) {
        Claims claims = parseToken(token);
        return claims.get("grade", String.class);  // devuelve null si no existe
    }

    // Métodos existentes
    public String extractRole(String token) {
        return parseToken(token).get("role", String.class);
    }

    public String extractEmail(String token){
        return parseClaims(token).getSubject();
    }

    public String extractLevel(String token) {
        return parseToken(token).get("level", String.class);
    }

    public String getValue(String jwt) {
        return parseClaims(jwt).getSubject();
    }

    public String getKey(String jwt) {
        return parseClaims(jwt).getId();
    }

    public Claims parseToken(String jwt) {
        return parseClaims(jwt);
    }

    public boolean validate(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Token inválido: {}", e.getMessage());
            return false;
        }
    }



    //######################## METODOS COMPLEMENTARIOS ########################

    /**
     * Obtiene la clave secreta de forma segura.
     * Intenta decodificar como base64, si falla, hace hash del string simple.
     */
    private SecretKey getSecretKey() {
        try {
            // Intenta decodificar como base64
            byte[] decodedKey = Decoders.BASE64.decode(jwtSecret);
            return Keys.hmacShaKeyFor(decodedKey);
        } catch (Exception e) {
            // Si falla la decodificación base64, usa la clave directamente
            log.warn("Clave JWT no está en formato base64, usando directamente: {}", e.getMessage());
            try {
                // Generar una clave de 32 bytes haciendo hash SHA-256 de la contraseña
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(jwtSecret.getBytes(StandardCharsets.UTF_8));
                return new SecretKeySpec(hash, 0, hash.length, "HmacSHA256");
            } catch (Exception ex) {
                throw new RuntimeException("Error al procesar la clave JWT", ex);
            }
        }
    }

    private Claims parseClaims(String jwt) {
        return Jwts.parser()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

}
