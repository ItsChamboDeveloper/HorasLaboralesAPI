package HorasLaborales.demo.Config.Cors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {
    // ✅ ELIMINA UNO DE LOS BEANS - ELIGE ESTE (más completo)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // ✅ ORÍGENES PERMITIDOS COMPLETOS
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",  // React dev server
                "http://localhost:4200",  // Angular dev server
                "http://localhost:8080",  // Spring Boot
                "http://localhost",       // XAMPP
                "https://sistemaweb-sgma.vercel.app",  // Vercel production
                "https://sgma-66ec41075156.herokuapp.com",  // Heroku production
                "https://localhost/"
        ));

        // ✅ MÉTODOS PERMITIDOS
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));

        // ✅ CABECERAS PERMITIDAS - AGREGAR X-Student-Id
        configuration.setAllowedHeaders(Arrays.asList(
                "Origin", "Content-Type", "Accept", "Authorization",
                "X-Requested-With", "Access-Control-Request-Method",
                "Access-Control-Request-Headers", "Cookie", "Set-Cookie",
                "X-Student-Id"  // ← AGREGAR ESTA LÍNEA
        ));

        // ✅ CABECERAS EXPUESTAS (CRÍTICO para cookies)
        configuration.setExposedHeaders(Arrays.asList(
                "Set-Cookie", "Authorization", "Content-Disposition",
                "X-Student-Id"  // ← OPCIONAL: También puedes exponerlo
        ));

        // ✅ CONFIGURACIÓN CRÍTICA
        configuration.setAllowCredentials(true);  // Permite cookies
        configuration.setMaxAge(3600L);           // 1 hora

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
