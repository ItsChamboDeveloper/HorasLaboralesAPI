package HorasLaborales.demo.Services.Email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String remitente;

    @Async
    public void enviarNotificacion(String para, String asunto, String cuerpo) {
        System.out.println("=================================================");
        System.out.println("[EMAIL-DEBUG] Iniciando proceso de envío de correo");
        System.out.println("[EMAIL-DEBUG] Para: " + para);
        System.out.println("[EMAIL-DEBUG] Asunto: " + asunto);
        System.out.println("[EMAIL-DEBUG] Remitente (from): " + remitente);
        System.out.println("=================================================");
        try {
            if (para == null || para.trim().isEmpty()) {
                System.err.println("[EMAIL-DEBUG] ERROR: El correo del destinatario está vacío o nulo.");
                return;
            }
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom(remitente);
            mensaje.setTo(para);
            mensaje.setSubject(asunto);
            mensaje.setText(cuerpo);
            
            mailSender.send(mensaje);
            System.out.println("[EMAIL-DEBUG] ¡Correo enviado exitosamente a: " + para + "!");
        } catch (Exception e) {
            System.err.println("[EMAIL-DEBUG] ERROR CRÍTICO al enviar el correo a través de Gmail a: " + para);
            System.err.println("[EMAIL-DEBUG] Excepción: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("=================================================");
    }
}
