package HorasLaborales.demo.Services.Auth.ParentsAuth;

import HorasLaborales.demo.Config.Crypto.Argon2Password;
import HorasLaborales.demo.Entities.Parents.ParentEntity;
import HorasLaborales.demo.Services.Auth.PasswordRecoveryService;
import HorasLaborales.demo.Services.Email.EmailService;
import HorasLaborales.demo.Repositories.Parents.ParentRepository;
import HorasLaborales.demo.Utils.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ParentAuthenticationService {

    /**
     * Repositorio para acceder a los datos de los papás/mamás.
     */
    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordRecoveryService passwordRecoveryService;

    /**
     * Verifica las credenciales de un papá/mamá usando Argon2.
     * Busca por correo electrónico y compara la contraseña proporcionada
     * con el hash almacenado.
     *
     * @param email    Correo con el que se registró
     * @param password Contraseña en texto plano ingresada por el usuario
     * @return true si las credenciales son válidas, false en caso contrario
     */
    public boolean parentLogin(String email, String password) {
        Argon2Password objHash = new Argon2Password();
        Optional<ParentEntity> parentOptional = parentRepository.findByEmail(email);
        if (parentOptional.isPresent()) {
            ParentEntity parent = parentOptional.get();

            System.out.println("Papá/mamá encontrado, ID: " + parent.getParentId() +
                    ", correo: " + parent.getEmail());

            String hashDB = parent.getPassword();
            return objHash.VerifyPassword(hashDB, password);
        }
        return false;
    }

    /**
     * Obtiene el papá/mamá completo por correo electrónico.
     *
     * @param email Correo con el que se registró
     * @return Optional con la entidad ParentEntity si existe
     */
    public Optional<ParentEntity> getParent(String email) {
        return parentRepository.findByEmail(email);
    }

    /**
     * Cambia la contraseña del papá/mamá.
     */
    public boolean changeParentPassword(Long id, String oldPassword, String newPassword) {
        Argon2Password objHash = new Argon2Password();
        ParentEntity parent = parentRepository.findById(id).orElse(null);
        if (parent == null) return false;

        if (!objHash.VerifyPassword(parent.getPassword(), oldPassword)) {
            return false;
        }

        parent.setPassword(objHash.EncryptPassword(newPassword));
        parentRepository.save(parent);
        return true;
    }

    public boolean recoverParentPassword(String email) {
        ParentEntity parent = parentRepository.findByEmail(email).orElse(null);
        if (parent == null) return false;

        String tempPassword = PasswordGenerator.generateSecurePassword(10);
        parent.setPassword(new Argon2Password().EncryptPassword(tempPassword));
        parentRepository.save(parent);

        String subject = "Recuperación de contraseña";
        String body = "Se generó una contraseña temporal para tu cuenta de papá/mamá.";
        String details = "Usuario: " + parent.getEmail() + "<br>Contraseña temporal: <b>" + tempPassword + "</b><br><br>Te recomendamos cambiarla después de iniciar sesión.";
        emailService.enviarNotificacionConDetalles(parent.getEmail(), subject, body, details);
        return true;
    }

    public boolean requestParentPasswordOtp(String email) {
        ParentEntity parent = parentRepository.findByEmail(email).orElse(null);
        if (parent == null) return false;

        passwordRecoveryService.sendOtp("parent", parent.getEmail(), "padre/madre");
        return true;
    }

    public boolean resetParentPasswordWithOtp(String email, String otp, String newPassword) {
        if (newPassword == null || newPassword.length() < 8) return false;

        ParentEntity parent = parentRepository.findByEmail(email).orElse(null);
        if (parent == null) return false;
        if (!passwordRecoveryService.verifyAndConsume("parent", parent.getEmail(), otp)) return false;

        parent.setPassword(new Argon2Password().EncryptPassword(newPassword));
        parentRepository.save(parent);
        return true;
    }
}
