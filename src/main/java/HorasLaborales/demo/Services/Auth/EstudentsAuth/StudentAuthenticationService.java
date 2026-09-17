package HorasLaborales.demo.Services.Auth.EstudentsAuth;

import HorasLaborales.demo.Config.Crypto.Argon2Password;
import HorasLaborales.demo.Entities.Students.StudentEntity;
import HorasLaborales.demo.Services.Auth.PasswordRecoveryService;
import HorasLaborales.demo.Services.Email.EmailService;
import HorasLaborales.demo.Repositories.Students.StudentsRepository;
import HorasLaborales.demo.Utils.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentAuthenticationService {

    /**
     * Repositorio para acceder a los datos de los estudiantes.
     */
    @Autowired
    private StudentsRepository studentsRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordRecoveryService passwordRecoveryService;

    /**
     * Verifica las credenciales de un estudiante usando Argon2.
     * Busca el estudiante por correo electrónico y compara la contraseña proporcionada con el hash almacenado.
     *
     * @param email Correo institucional del estudiante
     * @param password Contraseña en texto plano ingresada por el usuario
     * @return true si las credenciales son válidas, false en caso contrario
     */
    public boolean studentLogin(String email, String password) {
        Argon2Password objHash = new Argon2Password();
        Optional<StudentEntity> studentList = studentsRepository.findByEmail(email)
                .stream()
                .findFirst();
        if (studentList.isPresent()) {
            StudentEntity student = studentList.get();
            Long studentGrade = student.getGradeId().getGradeGroup(); // Grupo de grado del estudiante

            // Log de información básica del estudiante encontrado
            System.out.println("Estudiante encontrado, ID: " + student.getStudentId() +
                    ", correo: " + student.getEmail() +
                    ", grupo de grado: " + studentGrade);
            String HashDB = student.getPassword(); // Hash de la contraseña almacenada
            boolean verify = objHash.VerifyPassword(HashDB, password); // Verifica la contraseña
            return verify;
        }
        return false;
    }

    /**
     * Obtiene el estudiante completo por correo electrónico.
     *
     * @param email Correo institucional del estudiante
     * @return Optional con la entidad StudentEntity si existe, null si no se encuentra
     */
    public Optional<StudentEntity> getStudents(String email) {
        // Buscar estudiante completo en la base de datos
        Optional<StudentEntity> studentOpt = studentsRepository.findByEmail(email);
        return (studentOpt != null) ? studentOpt : null;
    }

    /**
     * Cambia la contraseña del estudiante.
     */
    public boolean changeStudentPassword(Long id, String oldPassword, String newPassword) {
        Argon2Password objHash = new Argon2Password();
        StudentEntity student = studentsRepository.findById(id).orElse(null);
        if (student == null) return false;

        if (!objHash.VerifyPassword(student.getPassword(), oldPassword)) {
            return false;
        }

        student.setPassword(objHash.EncryptPassword(newPassword));
        studentsRepository.save(student);
        return true;
    }

    public boolean recoverStudentPassword(String email) {
        StudentEntity student = studentsRepository.findByEmail(email).orElse(null);
        if (student == null) return false;

        String tempPassword = PasswordGenerator.generateSecurePassword(10);
        student.setPassword(new Argon2Password().EncryptPassword(tempPassword));
        studentsRepository.save(student);

        String subject = "Recuperación de contraseña";
        String body = "Se generó una contraseña temporal para tu cuenta de estudiante.";
        String details = "Usuario: " + student.getEmail() + "<br>Contraseña temporal: <b>" + tempPassword + "</b><br><br>Te recomendamos cambiarla después de iniciar sesión.";
        emailService.enviarNotificacionConDetalles(student.getEmail(), subject, body, details);
        return true;
    }

    public boolean requestStudentPasswordOtp(String email) {
        StudentEntity student = studentsRepository.findByEmail(email).orElse(null);
        if (student == null) return false;

        passwordRecoveryService.sendOtp("student", student.getEmail(), "estudiante");
        return true;
    }

    public boolean resetStudentPasswordWithOtp(String email, String otp, String newPassword) {
        if (newPassword == null || newPassword.length() < 8) return false;

        StudentEntity student = studentsRepository.findByEmail(email).orElse(null);
        if (student == null) return false;
        if (!passwordRecoveryService.verifyAndConsume("student", student.getEmail(), otp)) return false;

        student.setPassword(new Argon2Password().EncryptPassword(newPassword));
        studentsRepository.save(student);
        return true;
    }
}
