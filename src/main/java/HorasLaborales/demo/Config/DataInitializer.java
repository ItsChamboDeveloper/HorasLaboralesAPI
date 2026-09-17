package HorasLaborales.demo.Config;

import HorasLaborales.demo.Config.Crypto.Argon2Password;
import HorasLaborales.demo.Entities.Instructors.InstructorEntity;
import HorasLaborales.demo.Entities.Levels.LevelEntity;
import HorasLaborales.demo.Entities.Roles.RoleEntity;
import HorasLaborales.demo.Repositories.Instructors.InstructorRepository;
import HorasLaborales.demo.Repositories.Levels.LevelRepository;
import HorasLaborales.demo.Repositories.Roles.RoleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final InstructorRepository instructorRepository;
    private final RoleRepository roleRepository;
    private final LevelRepository levelRepository;
    private final Argon2Password argon2;

    @Value("${ADMIN_EMAIL}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;

    @Value("${ADMIN_FIRST_NAME:Administrador}")
    private String adminFirstName;

    @Value("${ADMIN_LAST_NAME:Sistema}")
    private String adminLastName;

    public DataInitializer(
            InstructorRepository instructorRepository,
            RoleRepository roleRepository,
            LevelRepository levelRepository,
            Argon2Password argon2
    ) {
        this.instructorRepository = instructorRepository;
        this.roleRepository = roleRepository;
        this.levelRepository = levelRepository;
        this.argon2 = argon2;
    }

    @Override
    public void run(String... args) {

        // Verificar si el administrador ya existe
        if (instructorRepository.existsByEmail(adminEmail)) {
            System.out.println("El usuario administrador ya existe.");
            return;
        }

        // Buscar rol Admin con ID 3
        RoleEntity adminRole = roleRepository.findById(3L)
                .orElseThrow(() ->
                        new RuntimeException("No se encontró el rol Admin con ID 3"));

        // Buscar nivel Tercer Año con ID 3
        LevelEntity thirdYear = levelRepository.findById(3L)
                .orElseThrow(() ->
                        new RuntimeException("No se encontró el nivel Tercer Año con ID 3"));

        // Crear administrador
        InstructorEntity admin = new InstructorEntity();

        admin.setFirstName(adminFirstName);
        admin.setLastName(adminLastName);
        admin.setEmail(adminEmail);

        // Encriptar contraseña con Argon2
        admin.setPassword(argon2.EncryptPassword(adminPassword));

        admin.setRoleId(adminRole);
        admin.setLevelId(thirdYear);
        admin.setInstructorImage(null);

        instructorRepository.save(admin);

        System.out.println("========================================");
        System.out.println("Usuario administrador creado correctamente");
        System.out.println("Correo: " + adminEmail);
        System.out.println("Rol: Admin");
        System.out.println("Nivel: Tercer Año");
        System.out.println("========================================");
    }
}