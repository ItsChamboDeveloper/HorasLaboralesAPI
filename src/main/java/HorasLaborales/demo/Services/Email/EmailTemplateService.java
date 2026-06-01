package HorasLaborales.demo.Services.Email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Servicio para generar plantillas HTML de correo electrónico
 * con estilos profesionales y branding del Instituto Técnico Ricaldone.
 */
@Service
public class EmailTemplateService {

    @Value("${app.mail.from}")
    private String remitente;

    /**
     * Genera una plantilla HTML completa para notificaciones por correo.
     *
     * @param asunto      El asunto/título del correo
     * @param cuerpo      El mensaje principal del correo
     * @param tipo        Tipo de notificación: "success", "warning", "danger", "info"
     * @param detalles    Información adicional (puede ser null)
     * @return String con el HTML completo del correo
     */
    public String generarPlantilla(String asunto, String cuerpo, String tipo, String detalles) {
        String colorPrimario;
        String colorFondo;
        String icono;
        String colorTextoEstado;

        switch (tipo != null ? tipo.toLowerCase() : "info") {
            case "success":
                colorPrimario = "#10B981";
                colorFondo = "#ECFDF5";
                icono = "✅";
                colorTextoEstado = "#065F46";
                break;
            case "warning":
                colorPrimario = "#F59E0B";
                colorFondo = "#FFFBEB";
                icono = "⚠️";
                colorTextoEstado = "#92400E";
                break;
            case "danger":
                colorPrimario = "#EF4444";
                colorFondo = "#FEF2F2";
                icono = "❌";
                colorTextoEstado = "#991B1B";
                break;
            default: // info
                colorPrimario = "#3B82F6";
                colorFondo = "#EFF6FF";
                icono = "📋";
                colorTextoEstado = "#1E40AF";
                break;
        }

        String fechaActual = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy, hh:mm a", new Locale("es", "SV")));

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html lang=\"es\">");
        html.append("<head>");
        html.append("<meta charset=\"UTF-8\">");
        html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        html.append("<title>").append(escapeHtml(asunto)).append("</title>");
        html.append("</head>");
        html.append("<body style=\"margin:0;padding:0;background-color:#f3f4f6;font-family:'Segoe UI',Tahoma,Geneva,Verdana,sans-serif;\">");

        // Tabla contenedora principal
        html.append("<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:#f3f4f6;padding:20px 0;\">");
        html.append("<tr><td align=\"center\">");

        // Contenedor del email (max 600px)
        html.append("<table role=\"presentation\" width=\"600\" cellpadding=\"0\" cellspacing=\"0\" style=\"max-width:600px;width:100%;background-color:#ffffff;border-radius:16px;overflow:hidden;box-shadow:0 4px 24px rgba(0,0,0,0.08);\">");

        // ========== HEADER CON LOGO ==========
        html.append("<tr>");
        html.append("<td style=\"background: linear-gradient(135deg, #1E3A5F 0%, #2C5282 50%, #1E3A5F 100%);padding:32px 40px;text-align:center;\">");

        // Logo del Instituto (imagen inline)
        html.append("<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">");
        html.append("<tr><td align=\"center\" style=\"padding-bottom:16px;\">");
        html.append("<img src=\"cid:logoRicaldone\" alt=\"Instituto Técnico Ricaldone\" ");
        html.append("style=\"width:80px;height:80px;border-radius:50%;border:3px solid rgba(255,255,255,0.3);object-fit:cover;\" />");
        html.append("</td></tr>");

        // Nombre del instituto
        html.append("<tr><td align=\"center\">");
        html.append("<h1 style=\"margin:0;color:#ffffff;font-size:22px;font-weight:700;letter-spacing:0.5px;\">Instituto Técnico Ricaldone</h1>");
        html.append("<p style=\"margin:4px 0 0;color:rgba(255,255,255,0.8);font-size:13px;letter-spacing:1px;text-transform:uppercase;\">Bachillerato Técnico Vocacional en Mecánica Automotriz</p>");
        html.append("</td></tr>");
        html.append("</table>");

        html.append("</td>");
        html.append("</tr>");

        // ========== BARRA DE ESTADO ==========
        html.append("<tr>");
        html.append("<td style=\"background-color:").append(colorFondo).append(";padding:16px 40px;border-bottom:2px solid ").append(colorPrimario).append(";\">");
        html.append("<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">");
        html.append("<tr>");
        html.append("<td style=\"font-size:28px;vertical-align:middle;padding-right:12px;width:40px;\">").append(icono).append("</td>");
        html.append("<td style=\"vertical-align:middle;\">");
        html.append("<h2 style=\"margin:0;color:").append(colorTextoEstado).append(";font-size:18px;font-weight:700;\">").append(escapeHtml(asunto)).append("</h2>");
        html.append("</td>");
        html.append("</tr>");
        html.append("</table>");
        html.append("</td>");
        html.append("</tr>");

        // ========== CONTENIDO PRINCIPAL ==========
        html.append("<tr>");
        html.append("<td style=\"padding:32px 40px;\">");

        // Saludo
        html.append("<p style=\"margin:0 0 20px;color:#374151;font-size:15px;line-height:1.7;\">");
        html.append("Estimado/a usuario/a del sistema de <strong>Horas Laborales Automotriz</strong>,");
        html.append("</p>");

        // Mensaje principal
        html.append("<p style=\"margin:0 0 24px;color:#1F2937;font-size:15px;line-height:1.7;\">");
        html.append(escapeHtml(cuerpo));
        html.append("</p>");

        // Detalles adicionales (si hay)
        if (detalles != null && !detalles.isEmpty()) {
            html.append("<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin-bottom:24px;\">");
            html.append("<tr><td style=\"background-color:#F9FAFB;border-left:4px solid ").append(colorPrimario).append(";padding:16px 20px;border-radius:0 8px 8px 0;\">");
            html.append("<p style=\"margin:0 0 4px;color:#6B7280;font-size:12px;text-transform:uppercase;letter-spacing:0.5px;font-weight:600;\">Detalles Adicionales</p>");
            html.append("<p style=\"margin:0;color:#374151;font-size:14px;line-height:1.6;\">").append(escapeHtml(detalles)).append("</p>");
            html.append("</td></tr>");
            html.append("</table>");
        }

        // Fecha y hora
        html.append("<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin-bottom:24px;\">");
        html.append("<tr><td style=\"background-color:#F9FAFB;padding:12px 16px;border-radius:8px;\">");
        html.append("<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">");
        html.append("<tr>");
        html.append("<td style=\"color:#6B7280;font-size:13px;\">📅 Fecha de notificación</td>");
        html.append("<td align=\"right\" style=\"color:#1F2937;font-size:13px;font-weight:600;\">").append(fechaActual).append("</td>");
        html.append("</tr>");
        html.append("</table>");
        html.append("</td></tr>");
        html.append("</table>");

        // Separador
        html.append("<hr style=\"border:none;border-top:1px solid #E5E7EB;margin:24px 0;\">");

        // Aviso
        html.append("<p style=\"margin:0;color:#9CA3AF;font-size:12px;line-height:1.6;text-align:center;\">");
        html.append("Este es un mensaje automático del sistema de gestión de Horas Laborales Automotriz.<br>");
        html.append("Por favor, no responda directamente a este correo.");
        html.append("</p>");

        html.append("</td>");
        html.append("</tr>");

        // ========== FOOTER ==========
        html.append("<tr>");
        html.append("<td style=\"background-color:#1E3A5F;padding:24px 40px;text-align:center;\">");
        html.append("<p style=\"margin:0 0 8px;color:rgba(255,255,255,0.9);font-size:13px;font-weight:600;\">Instituto Técnico Ricaldone</p>");
        html.append("<p style=\"margin:0 0 4px;color:rgba(255,255,255,0.6);font-size:12px;\">Calle y Colonia Don Bosco, San Salvador, El Salvador</p>");
        html.append("<p style=\"margin:0;color:rgba(255,255,255,0.6);font-size:12px;\">📞 (503) 2251-4600 | 🌐 www.ricaldone.edu.sv</p>");
        html.append("<hr style=\"border:none;border-top:1px solid rgba(255,255,255,0.15);margin:16px 0;\">");
        html.append("<p style=\"margin:0;color:rgba(255,255,255,0.4);font-size:11px;\">© ").append(LocalDateTime.now().getYear()).append(" Horas Laborales Automotriz - Todos los derechos reservados</p>");
        html.append("</td>");
        html.append("</tr>");

        html.append("</table>"); // cierre contenedor email
        html.append("</td></tr>");
        html.append("</table>"); // cierre tabla contenedora

        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }

    /**
     * Determina el tipo de notificación basado en el asunto del correo.
     */
    public String determinarTipo(String asunto) {
        if (asunto == null) return "info";
        String lower = asunto.toLowerCase();
        if (lower.contains("aprobad") || lower.contains("completad") || lower.contains("felicidades") || lower.contains("éxito")) {
            return "success";
        } else if (lower.contains("rechazad")) {
            return "danger";
        } else if (lower.contains("pendiente") || lower.contains("requiere") || lower.contains("revisión") || lower.contains("firma")) {
            return "warning";
        }
        return "info";
    }

    /**
     * Escapa caracteres HTML para prevenir XSS.
     */
    private String escapeHtml(String input) {
        if (input == null) return "";
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
