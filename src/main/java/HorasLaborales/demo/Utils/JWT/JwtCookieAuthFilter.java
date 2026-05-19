package HorasLaborales.demo.Utils.JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
public class JwtCookieAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtCookieAuthFilter.class);
    private static final String AUTH_COOKIE_NAME = "authToken";
    private final JWTUtils jwtUtils;

    @Autowired
    public JwtCookieAuthFilter(JWTUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // CORREGIDO: Mejor lógica para endpoints públicos
        if (isPublicEndpoint(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = extractTokenFromCookies(request);

            if (token == null) {

                // Para endpoints no públicos, requerimos token
                if (!isPublicEndpoint(request)) {
                    sendError(response, "Token no encontrado", HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
                filterChain.doFilter(request, response);
                return;
            }

            Claims claims = jwtUtils.parseToken(token);

            // EXTRAER EL ROL REAL del token
            String role = jwtUtils.extractRole(token);

            // EXTRAER EL LEVEL REAL del token
            String level = jwtUtils.extractLevel(token);

            // CORREGIDO - Crear authorities de forma robusta mapeando roles de base de datos a Spring Security
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            if (role != null && !role.isBlank()) {
                String primaryRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                authorities.add(new SimpleGrantedAuthority(primaryRole));

                // Mapear compatibilidad de idioma y roles para base de datos
                String cleanRole = role.replace("ROLE_", "").trim();
                if (cleanRole.equalsIgnoreCase("Students") || cleanRole.equalsIgnoreCase("Student") || cleanRole.equalsIgnoreCase("Alumno")) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_Alumno"));
                } else if (cleanRole.equalsIgnoreCase("Instructors") || cleanRole.equalsIgnoreCase("Instructor") || cleanRole.equalsIgnoreCase("Docente")) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_Docente"));
                    authorities.add(new SimpleGrantedAuthority("ROLE_Animador"));
                    authorities.add(new SimpleGrantedAuthority("ROLE_Coordinador"));
                } else if (cleanRole.equalsIgnoreCase("Leader") || cleanRole.equalsIgnoreCase("Lider")) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_Coordinador"));
                    authorities.add(new SimpleGrantedAuthority("ROLE_Animador"));
                } else if (cleanRole.equalsIgnoreCase("Admin")) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_Coordinador"));
                    authorities.add(new SimpleGrantedAuthority("ROLE_Admin"));
                }
            } else {
                authorities.add(new SimpleGrantedAuthority("ROLE_Alumno"));
            }

            // CREAR AUTENTICACIÓN CON AUTHORITIES CORRECTOS
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            claims.getSubject(),  // principal (el email/username)
                            null,                 // credentials (null porque ya está autenticado vía JWT)
                            authorities           // los roles/permisos que ya extrajiste arriba
                    );

            // ESTABLECER AUTENTICACIÓN EN CONTEXTO
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            log.warn("Token expirado: {}", e.getMessage());
            sendError(response, "Token expirado", HttpServletResponse.SC_UNAUTHORIZED);
        } catch (MalformedJwtException e) {
            log.warn("Token malformado: {}", e.getMessage());
            sendError(response, "Token inválido", HttpServletResponse.SC_FORBIDDEN);
        } catch (Exception e) {
            log.error("Error de autenticación", e);
            sendError(response, "Error de autenticación", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private String extractTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        return Arrays.stream(cookies)
                .filter(c -> AUTH_COOKIE_NAME.equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    private void sendError(HttpServletResponse response, String message, int status) throws IOException {
        response.setContentType("application/json");
        response.setStatus(status);
        response.getWriter().write(String.format(
                "{\"error\": \"%s\", \"estado\": %d}", message, status));
    }

    //  TODOS los endpoints públicos de SecurityConfig
    private boolean isPublicEndpoint(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();



        return
                ("OPTIONS".equals(method)) ||
                (path.equals("/api/studentsAuth/studentLogin") && "POST".equals(method)) ||
                (path.equals("/api/instructorsAuth/instructorLogin") && "POST".equals(method));
    }

}
