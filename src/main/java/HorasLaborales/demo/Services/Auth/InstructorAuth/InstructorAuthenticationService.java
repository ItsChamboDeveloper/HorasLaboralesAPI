package HorasLaborales.demo.Services.Auth.InstructorAuth;

import HorasLaborales.demo.Config.Crypto.Argon2Password;
import HorasLaborales.demo.Entities.Instructors.InstructorEntity;
import HorasLaborales.demo.Repositories.Instructors.InstructorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InstructorAuthenticationService {

    /**
     * Repositorio para acceder a los datos de los instructores.
     */
    @Autowired
    private InstructorRepository instructorRepository;

    /**
     * Verifica las credenciales de un instructor usando Argon2.
     * Busca el instructor por correo electrónico y compara la contraseña proporcionada con el hash almacenado.
     *
     * @param email    Correo institucional del instructor
     * @param password Contraseña en texto plano ingresada por el usuario
     * @return true si las credenciales son válidas, false en caso contrario
     */
    public boolean instructorLogin(String email, String password) {
        Argon2Password objHash = new Argon2Password();
        Optional<InstructorEntity> instructorList = instructorRepository.findByEmail(email)
                .stream()
                .findFirst();
        if (instructorList.isPresent()) {
            InstructorEntity instructor = instructorList.get();
            String rolName = instructor.getRoleId().getRoleName(); // Nombre del rol del instructor
            String levelName = instructor.getLevelId().getLevelName(); // Nombre del nivel del instructor

            // Log de información básica del usuario encontrado
            System.out.println("Instructor encontrado, ID: " + instructor.getInstructorId() + ", correo: " +
                    instructor.getEmail() + ", rol: " +
                    rolName + ", nivel: " + levelName);

            String HashDB = instructor.getPassword(); // Hash de la contraseña almacenada
            boolean verify = objHash.VerifyPassword(HashDB, password); // Verifica la contraseña
            return verify;
        }
        return false;
    }

    /**
     * Obtiene el instructor completo por correo electrónico.
     *
     * @param email Correo institucional del instructor
     * @return Optional con la entidad InstructorEntity si existe, null si no se encuentra
     */
    public Optional<InstructorEntity> getInstructor(String email) {
        // Buscar instructor completo en la base de datos
        Optional<InstructorEntity> instructoOpt = instructorRepository.findByEmail(email);
        return (instructoOpt != null) ? instructoOpt : null;
    }

}
