package HorasLaborales.demo.Config.Security;

import HorasLaborales.demo.Utils.JWT.JwtCookieAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    // ✅ RUTAS BASE DE CADA MÓDULO
    private static final String grades         = "/api/grades";
    private static final String students       = "/api/students";
    private static final String studentAuth    = "/api/studentsAuth";
    private static final String instructorAuth = "/api/instructorsAuth";
    private static final String entries        = "/api/entries";
    private static final String vehicles       = "/api/vehicles";
    private static final String modules        = "/api/modules";
    private static final String vehicleTypes   = "/api/vehicleTypes";
    private static final String levels         = "/api/levels";
    private static final String observations   = "/api/observations";
    private static final String workOrders     = "/api/workOrders";
    private static final String vehiculosBase  = "/vehiculos";
    private static final String vehiculosApi   = "/api/vehiculos";
    private static final String workOrdersBase = "/work-orders";
    private static final String workOrdersApi  = "/api/work-orders";

    // *** NUEVO: rutas para papás, marcas y modelos ***
    private static final String parentAuth     = "/api/parentsAuth";
    private static final String parents        = "/api/parents";
    private static final String brands         = "/api/brands";
    private static final String vehicleModels  = "/api/vehicleModels";

    private final JwtCookieAuthFilter jwtCookieAuthFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(JwtCookieAuthFilter jwtCookieAuthFilter,
                          CorsConfigurationSource corsConfigurationSource) {
        this.jwtCookieAuthFilter = jwtCookieAuthFilter;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(auth -> auth

                        // ✅ Preflight CORS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // ── GRADES ──────────────────────────────────────────────
                        .requestMatchers(HttpMethod.GET,  grades + "/getAllGrades").authenticated()

                        // ── STUDENTS ─────────────────────────────────────────────
                        .requestMatchers(HttpMethod.POST, students + "/newStudent").authenticated()
                        .requestMatchers(HttpMethod.PUT,  students + "/updateStudent/*").authenticated()

                        // ── STUDENT AUTH ──────────────────────────────────────────
                        .requestMatchers(HttpMethod.POST, studentAuth + "/studentLogin").permitAll()
                        .requestMatchers(studentAuth + "/meStudent").authenticated()
                        .requestMatchers(HttpMethod.POST, studentAuth + "/logoutStudent").authenticated()

                        // ── INSTRUCTOR AUTH ───────────────────────────────────────
                        .requestMatchers(HttpMethod.POST, instructorAuth + "/instructorLogin").permitAll()
                        .requestMatchers(instructorAuth + "/meInstructor").authenticated()
                        .requestMatchers(HttpMethod.POST, instructorAuth + "/logoutInstructor").authenticated()

                        // ── PARENT AUTH (NUEVO) ───────────────────────────────────
                        .requestMatchers(HttpMethod.POST, parentAuth + "/parentLogin").permitAll()
                        .requestMatchers(HttpMethod.POST, parentAuth + "/registerParent").permitAll()
                        .requestMatchers(parentAuth + "/meParent").authenticated()
                        .requestMatchers(HttpMethod.POST, parentAuth + "/logoutParent").authenticated()

                        // ── PARENTS (NUEVO) ────────────────────────────────────────
                        .requestMatchers(HttpMethod.POST, parents + "/newParent").authenticated()
                        .requestMatchers(HttpMethod.GET,  parents + "/getAllParents").authenticated()

                        // ── BRANDS / VEHICLE MODELS (NUEVO, combobox dependiente) ──
                        .requestMatchers(HttpMethod.GET, brands + "/getAllBrands").authenticated()
                        .requestMatchers(HttpMethod.GET, vehicleModels + "/getAllModels").authenticated()
                        .requestMatchers(HttpMethod.GET, vehicleModels + "/getModelsByBrand/*").authenticated()

                        // ── ENTRIES ───────────────────────────────────────────────
                        .requestMatchers(HttpMethod.POST, entries + "/newEntry").authenticated()
                        .requestMatchers(HttpMethod.GET,  entries + "/getAllEntries").authenticated()

                        // ── VEHICLES ──────────────────────────────────────────────
                        .requestMatchers(HttpMethod.POST, vehicles + "/newVehicle").authenticated()
                        .requestMatchers(HttpMethod.GET,  vehicles + "/getVehiclesByStudent/*").authenticated()
                        .requestMatchers(HttpMethod.GET,  vehicles + "/getVehiclesByParentId/*").authenticated()
                        .requestMatchers(HttpMethod.GET,  vehicles + "/myVehicles").hasAuthority("ROLE_Padre")

                        // ── MODULES ───────────────────────────────────────────────
                        .requestMatchers(HttpMethod.GET, modules + "/getAllModules").authenticated()

                        // ── VEHICLE TYPES ─────────────────────────────────────────
                        .requestMatchers(HttpMethod.GET, vehicleTypes + "/getAllVehicleTypes").authenticated()

                        // ── LEVELS ────────────────────────────────────────────────
                        .requestMatchers(HttpMethod.GET, levels + "/getAllLevels").authenticated()

                        // ── OBSERVATIONS ──────────────────────────────────────────
                        .requestMatchers(HttpMethod.POST, observations + "/newObservation").authenticated()
                        .requestMatchers(HttpMethod.GET,  observations + "/getObservationsByWorkOrder/*").authenticated()

                        // ── WORK ORDERS ───────────────────────────────────────────
                        .requestMatchers(HttpMethod.POST,   workOrders + "/{workOrderId}/status")
                        .hasAnyAuthority("ROLE_Alumno", "ROLE_Docente", "ROLE_Animador", "ROLE_Coordinador")
                        .requestMatchers(HttpMethod.POST,   workOrders + "/newWorkOrder").hasAuthority("ROLE_Alumno")
                        .requestMatchers(HttpMethod.PUT,    workOrders + "/updateWorkOrder/*").hasAuthority("ROLE_Alumno")
                        .requestMatchers(HttpMethod.DELETE, workOrders + "/deleteWorkOrder/*").hasAuthority("ROLE_Alumno")

                        // ── WORKFLOW VEHICULOS ────────────────────────────────────
                        .requestMatchers(HttpMethod.POST, vehiculosBase, vehiculosApi).hasAuthority("ROLE_Alumno")
                        .requestMatchers(HttpMethod.POST, vehiculosBase + "/*/revision-animador", vehiculosApi + "/*/revision-animador").hasAnyAuthority("ROLE_Animador", "ROLE_Docente", "ROLE_Coordinador")
                        .requestMatchers(HttpMethod.POST, vehiculosBase + "/*/revision-coordinadora", vehiculosApi + "/*/revision-coordinadora").hasAnyAuthority("ROLE_Coordinador", "ROLE_Animador")

                        // ── WORKFLOW WORK ORDERS ──────────────────────────────────
                        .requestMatchers(HttpMethod.POST, workOrdersBase, workOrdersApi).hasAuthority("ROLE_Alumno")
                        .requestMatchers(HttpMethod.POST, workOrdersBase + "/*/revision-animador", workOrdersApi + "/*/revision-animador").hasAnyAuthority("ROLE_Animador", "ROLE_Docente", "ROLE_Coordinador")
                        .requestMatchers(HttpMethod.POST, workOrdersBase + "/*/revision-coordinadora", workOrdersApi + "/*/revision-coordinadora").hasAnyAuthority("ROLE_Coordinador", "ROLE_Animador")
                        .requestMatchers(HttpMethod.POST, workOrdersBase + "/*/accion-estudiante", workOrdersApi + "/*/accion-estudiante").hasAuthority("ROLE_Alumno")

                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtCookieAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}
