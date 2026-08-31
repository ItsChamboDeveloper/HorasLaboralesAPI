package HorasLaborales.demo.Services.Auth;

import HorasLaborales.demo.Services.Email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PasswordRecoveryService {

    private static final int OTP_EXPIRATION_MINUTES = 10;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Autowired
    private EmailService emailService;

    private final Map<String, OtpData> otpStore = new ConcurrentHashMap<>();

    public void sendOtp(String scope, String email, String accountLabel) {
        String cleanEmail = normalizeEmail(email);
        String code = String.format("%06d", RANDOM.nextInt(1_000_000));
        otpStore.put(buildKey(scope, cleanEmail), new OtpData(code, Instant.now().plus(OTP_EXPIRATION_MINUTES, ChronoUnit.MINUTES)));

        String subject = "Código de recuperación de contraseña";
        String body = "Usa este código para recuperar tu cuenta de " + accountLabel + ".";
        String details = "Correo: " + cleanEmail + "<br>Código OTP: <b>" + code + "</b><br><br>Este código vence en " + OTP_EXPIRATION_MINUTES + " minutos.";
        emailService.enviarNotificacionConDetalles(cleanEmail, subject, body, details);
    }

    public boolean verifyAndConsume(String scope, String email, String code) {
        if (code == null || code.isBlank()) return false;

        String key = buildKey(scope, normalizeEmail(email));
        OtpData otpData = otpStore.get(key);

        if (otpData == null || otpData.expiresAt.isBefore(Instant.now()) || !otpData.code.equals(code.trim())) {
            return false;
        }

        otpStore.remove(key);
        return true;
    }

    private String buildKey(String scope, String email) {
        return scope + ":" + email;
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

    private static class OtpData {
        private final String code;
        private final Instant expiresAt;

        private OtpData(String code, Instant expiresAt) {
            this.code = code;
            this.expiresAt = expiresAt;
        }
    }
}
