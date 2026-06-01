package HorasLaborales.demo.Services.Email;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailTemplateService templateService;

    @Value("${app.mail.from}")
    private String remitente;

    /**
     * Envía una notificación por correo con plantilla HTML decorada.
     * Se mantiene la firma original (String para, String asunto, String cuerpo)
     * para no romper las llamadas existentes.
     *
     * @param para   Correo del destinatario
     * @param asunto Asunto del correo
     * @param cuerpo Mensaje principal del correo
     */
    @Async
    public void enviarNotificacion(String para, String asunto, String cuerpo) {
        enviarNotificacionConDetalles(para, asunto, cuerpo, null);
    }

    /**
     * Envía una notificación por correo con plantilla HTML decorada y detalles adicionales.
     *
     * @param para     Correo del destinatario
     * @param asunto   Asunto del correo
     * @param cuerpo   Mensaje principal del correo
     * @param detalles Información adicional (puede ser null)
     */
    @Async
    public void enviarNotificacionConDetalles(String para, String asunto, String cuerpo, String detalles) {
        System.out.println("=================================================");
        System.out.println("[EMAIL-DEBUG] Iniciando proceso de envío de correo HTML");
        System.out.println("[EMAIL-DEBUG] Para: " + para);
        System.out.println("[EMAIL-DEBUG] Asunto: " + asunto);
        System.out.println("[EMAIL-DEBUG] Remitente (from): " + remitente);
        System.out.println("=================================================");
        try {
            if (para == null || para.trim().isEmpty()) {
                System.err.println("[EMAIL-DEBUG] ERROR: El correo del destinatario está vacío o nulo.");
                return;
            }

            // Determinar el tipo de notificación automáticamente
            String tipo = templateService.determinarTipo(asunto);

            // Generar el HTML decorado
            String htmlContent = templateService.generarPlantilla(asunto, cuerpo, tipo, detalles);

            // Crear MimeMessage para enviar HTML
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(remitente);
            helper.setTo(para);
            helper.setSubject(asunto);
            helper.setText(htmlContent, true); // true = es HTML

            // Intentar adjuntar el logo como imagen embebida
            try {
                ClassPathResource logoResource = new ClassPathResource("static/images/ricaldone logo.png");
                if (logoResource.exists()) {
                    helper.addInline("logoRicaldone", logoResource, "image/png");
                } else {
                    System.out.println("[EMAIL-DEBUG] Logo no encontrado en classpath, se enviará sin imagen embebida.");
                }
            } catch (Exception ex) {
                System.out.println("[EMAIL-DEBUG] No se pudo adjuntar el logo: " + ex.getMessage());
            }

            mailSender.send(mimeMessage);
            System.out.println("[EMAIL-DEBUG] ¡Correo HTML enviado exitosamente a: " + para + "!");
        } catch (Exception e) {
            System.err.println("[EMAIL-DEBUG] ERROR CRÍTICO al enviar el correo HTML a: " + para);
            System.err.println("[EMAIL-DEBUG] Excepción: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("=================================================");
    }
}
