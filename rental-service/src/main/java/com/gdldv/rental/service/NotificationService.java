package com.gdldv.rental.service;

import com.gdldv.rental.dto.EmailNotification;
import com.gdldv.rental.entity.Rental;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * GDLDV-548: Service de notifications par email
 * Gère l'envoi d'emails pour différents événements (confirmations, rappels, alertes)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@gdldv.com}")
    private String fromEmail;

    @Value("${app.admin.email:admin@gdldv.com}")
    private String adminEmail;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm");

    /**
     * Envoyer une notification email
     */
    public EmailNotification sendEmail(EmailNotification notification) {
        log.info("Envoi d'email à: {} | Sujet: {}", notification.getTo(), notification.getSubject());

        try {
            if (notification.getTemplateName() != null) {
                // Envoyer avec template HTML
                sendHtmlEmail(notification);
            } else {
                // Envoyer email texte simple
                sendSimpleEmail(notification);
            }

            notification.setStatus("SENT");
            notification.setSentAt(LocalDateTime.now());
            log.info("Email envoyé avec succès à: {}", notification.getTo());

        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email à {}: {}", notification.getTo(), e.getMessage());
            notification.setStatus("FAILED");
            notification.setErrorMessage(e.getMessage());
        }

        return notification;
    }

    /**
     * Envoyer un email simple (texte uniquement)
     */
    private void sendSimpleEmail(EmailNotification notification) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(notification.getTo());
        message.setSubject(notification.getSubject());

        // Construire le corps du message à partir des variables du template
        String body = buildEmailBody(notification.getTemplateVariables());
        message.setText(body);

        if (notification.getCc() != null) {
            message.setCc(notification.getCc());
        }

        mailSender.send(message);
    }

    /**
     * Envoyer un email HTML avec template
     */
    private void sendHtmlEmail(EmailNotification notification) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(notification.getTo());
        helper.setSubject(notification.getSubject());

        if (notification.getCc() != null) {
            helper.setCc(notification.getCc());
        }

        // Générer le contenu HTML
        String htmlContent = generateHtmlTemplate(notification.getTemplateName(), notification.getTemplateVariables());
        helper.setText(htmlContent, true);

        mailSender.send(mimeMessage);
    }

    /**
     * GDLDV-548: Envoyer une confirmation de réservation
     */
    public void sendBookingConfirmation(Long userId, Rental rental) {
        log.info("Envoi de la confirmation de réservation pour la location #{}", rental.getId());

        EmailNotification notification = EmailNotification.builder()
                .to("user" + userId + "@example.com") // TODO: Récupérer l'email via UserClient
                .subject("Confirmation de votre réservation #" + rental.getId())
                .templateName("BOOKING_CONFIRMATION")
                .templateVariables(Map.of(
                        "rentalId", rental.getId(),
                        "vehicleId", rental.getVehicleId(),
                        "startDate", rental.getStartDate().format(DATE_FORMATTER),
                        "endDate", rental.getEndDate().format(DATE_FORMATTER),
                        "totalAmount", rental.getTotalAmount() != null ? rental.getTotalAmount() : 0.0
                ))
                .priority("NORMAL")
                .sendImmediately(true)
                .build();

        sendEmail(notification);
    }

    /**
     * GDLDV-548: Envoyer un rappel de début de location
     */
    public void sendRentalStartReminder(Long userId, Rental rental) {
        log.info("Envoi du rappel de début de location pour la location #{}", rental.getId());

        EmailNotification notification = EmailNotification.builder()
                .to("user" + userId + "@example.com") // TODO: Récupérer l'email via UserClient
                .subject("Rappel: Votre location commence demain")
                .templateName("RENTAL_START_REMINDER")
                .templateVariables(Map.of(
                        "rentalId", rental.getId(),
                        "startDate", rental.getStartDate().format(DATE_FORMATTER),
                        "pickupLocation", "Agence principale" // TODO: Ajouter l'info de pickup
                ))
                .priority("HIGH")
                .sendImmediately(true)
                .build();

        sendEmail(notification);
    }

    /**
     * GDLDV-548: Envoyer un rappel de fin de location
     */
    public void sendRentalEndReminder(Long userId, Rental rental) {
        log.info("Envoi du rappel de fin de location pour la location #{}", rental.getId());

        EmailNotification notification = EmailNotification.builder()
                .to("user" + userId + "@example.com") // TODO: Récupérer l'email via UserClient
                .subject("Rappel: Fin de location prévue demain")
                .templateName("RENTAL_END_REMINDER")
                .templateVariables(Map.of(
                        "rentalId", rental.getId(),
                        "endDate", rental.getEndDate().format(DATE_FORMATTER),
                        "returnLocation", "Agence principale" // TODO: Ajouter l'info de retour
                ))
                .priority("HIGH")
                .sendImmediately(true)
                .build();

        sendEmail(notification);
    }

    /**
     * GDLDV-548: Envoyer une alerte de location en retard
     */
    public void sendOverdueAlert(Long userId, Rental rental) {
        log.info("Envoi de l'alerte de retard pour la location #{}", rental.getId());

        // Email au client
        EmailNotification clientNotification = EmailNotification.builder()
                .to("user" + userId + "@example.com") // TODO: Récupérer l'email via UserClient
                .subject("URGENT: Votre location est en retard")
                .templateName("OVERDUE_ALERT")
                .templateVariables(Map.of(
                        "rentalId", rental.getId(),
                        "endDate", rental.getEndDate().format(DATE_FORMATTER),
                        "lateFee", 5000.0 // 5000 FCFA/heure
                ))
                .priority("HIGH")
                .sendImmediately(true)
                .build();

        sendEmail(clientNotification);

        // Email à l'admin
        EmailNotification adminNotification = EmailNotification.builder()
                .to(adminEmail)
                .subject("Alerte: Location en retard #" + rental.getId())
                .templateName("ADMIN_OVERDUE_ALERT")
                .templateVariables(Map.of(
                        "rentalId", rental.getId(),
                        "userId", userId,
                        "vehicleId", rental.getVehicleId(),
                        "endDate", rental.getEndDate().format(DATE_FORMATTER)
                ))
                .priority("HIGH")
                .sendImmediately(true)
                .build();

        sendEmail(adminNotification);
    }

    /**
     * GDLDV-548: Envoyer le rapport financier mensuel
     */
    public void sendMonthlyFinancialReport(byte[] pdfReport, String reportMonth) {
        log.info("Envoi du rapport financier pour: {}", reportMonth);

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(adminEmail);
            helper.setSubject("Rapport financier mensuel - " + reportMonth);

            String htmlContent = String.format(
                    "<html><body>" +
                            "<h2>Rapport Financier - %s</h2>" +
                            "<p>Veuillez trouver ci-joint le rapport financier mensuel.</p>" +
                            "<p>Ce rapport a été généré automatiquement par le système GDLDV.</p>" +
                            "</body></html>",
                    reportMonth
            );

            helper.setText(htmlContent, true);
            helper.addAttachment("rapport_financier_" + reportMonth + ".pdf", () -> new java.io.ByteArrayInputStream(pdfReport));

            mailSender.send(mimeMessage);
            log.info("Rapport financier envoyé avec succès");

        } catch (MessagingException e) {
            log.error("Erreur lors de l'envoi du rapport financier: {}", e.getMessage());
        }
    }

    /**
     * GDLDV-548: Envoyer les recommandations analytics
     */
    public void sendAnalyticsRecommendations(String recommendations) {
        log.info("Envoi des recommandations analytics");

        EmailNotification notification = EmailNotification.builder()
                .to(adminEmail)
                .subject("Recommandations Analytics - " + LocalDateTime.now().format(DATE_FORMATTER))
                .templateName("ANALYTICS_RECOMMENDATIONS")
                .templateVariables(Map.of(
                        "generatedAt", LocalDateTime.now().format(DATE_FORMATTER),
                        "recommendations", recommendations
                ))
                .priority("NORMAL")
                .sendImmediately(true)
                .build();

        sendEmail(notification);
    }

    /**
     * Construire le corps de l'email à partir des variables
     */
    private String buildEmailBody(Map<String, Object> variables) {
        if (variables == null || variables.isEmpty()) {
            return "";
        }

        StringBuilder body = new StringBuilder();
        variables.forEach((key, value) ->
                body.append(key).append(": ").append(value).append("\n")
        );
        return body.toString();
    }

    /**
     * Générer le template HTML
     */
    private String generateHtmlTemplate(String templateName, Map<String, Object> variables) {
        // Templates HTML simples - TODO: Utiliser Thymeleaf ou FreeMarker pour des templates plus complexes
        return switch (templateName) {
            case "BOOKING_CONFIRMATION" -> generateBookingConfirmationTemplate(variables);
            case "RENTAL_START_REMINDER" -> generateRentalStartReminderTemplate(variables);
            case "RENTAL_END_REMINDER" -> generateRentalEndReminderTemplate(variables);
            case "OVERDUE_ALERT" -> generateOverdueAlertTemplate(variables);
            case "ADMIN_OVERDUE_ALERT" -> generateAdminOverdueAlertTemplate(variables);
            case "ANALYTICS_RECOMMENDATIONS" -> generateAnalyticsRecommendationsTemplate(variables);
            default -> generateDefaultTemplate(variables);
        };
    }

    private String generateBookingConfirmationTemplate(Map<String, Object> vars) {
        return String.format("""
                <html>
                <body style="font-family: Arial, sans-serif;">
                    <h2 style="color: #2c3e50;">Confirmation de Réservation</h2>
                    <p>Votre réservation a été confirmée avec succès!</p>
                    <div style="background-color: #ecf0f1; padding: 15px; border-radius: 5px;">
                        <p><strong>Numéro de réservation:</strong> #%s</p>
                        <p><strong>Véhicule:</strong> #%s</p>
                        <p><strong>Début:</strong> %s</p>
                        <p><strong>Fin:</strong> %s</p>
                        <p><strong>Montant total:</strong> %,.0f FCFA</p>
                    </div>
                    <p>Merci de votre confiance!</p>
                    <p style="color: #7f8c8d; font-size: 12px;">GDLDV - Gestion des Locations de Véhicules</p>
                </body>
                </html>
                """,
                vars.get("rentalId"),
                vars.get("vehicleId"),
                vars.get("startDate"),
                vars.get("endDate"),
                vars.get("totalAmount")
        );
    }

    private String generateRentalStartReminderTemplate(Map<String, Object> vars) {
        return String.format("""
                <html>
                <body style="font-family: Arial, sans-serif;">
                    <h2 style="color: #27ae60;">Rappel: Votre location commence demain</h2>
                    <p>N'oubliez pas de récupérer votre véhicule!</p>
                    <div style="background-color: #d5f4e6; padding: 15px; border-radius: 5px;">
                        <p><strong>Réservation:</strong> #%s</p>
                        <p><strong>Date de début:</strong> %s</p>
                        <p><strong>Lieu de récupération:</strong> %s</p>
                    </div>
                    <p>Documents à apporter: Permis de conduire, Pièce d'identité, Carte de crédit</p>
                </body>
                </html>
                """,
                vars.get("rentalId"),
                vars.get("startDate"),
                vars.get("pickupLocation")
        );
    }

    private String generateRentalEndReminderTemplate(Map<String, Object> vars) {
        return String.format("""
                <html>
                <body style="font-family: Arial, sans-serif;">
                    <h2 style="color: #f39c12;">Rappel: Fin de location demain</h2>
                    <p>Merci de retourner le véhicule à temps pour éviter des frais supplémentaires.</p>
                    <div style="background-color: #fef5e7; padding: 15px; border-radius: 5px;">
                        <p><strong>Réservation:</strong> #%s</p>
                        <p><strong>Date de fin:</strong> %s</p>
                        <p><strong>Lieu de retour:</strong> %s</p>
                    </div>
                    <p>Assurez-vous que le véhicule est propre et avec le plein d'essence.</p>
                </body>
                </html>
                """,
                vars.get("rentalId"),
                vars.get("endDate"),
                vars.get("returnLocation")
        );
    }

    private String generateOverdueAlertTemplate(Map<String, Object> vars) {
        return String.format("""
                <html>
                <body style="font-family: Arial, sans-serif;">
                    <h2 style="color: #e74c3c;">URGENT: Votre location est en retard</h2>
                    <p>Le véhicule devait être retourné. Des frais de retard s'appliquent.</p>
                    <div style="background-color: #fadbd8; padding: 15px; border-radius: 5px;">
                        <p><strong>Réservation:</strong> #%s</p>
                        <p><strong>Date de fin prévue:</strong> %s</p>
                        <p><strong>Frais de retard:</strong> %,.0f FCFA/heure</p>
                    </div>
                    <p style="color: #c0392b;">Veuillez retourner le véhicule immédiatement ou nous contacter.</p>
                </body>
                </html>
                """,
                vars.get("rentalId"),
                vars.get("endDate"),
                vars.get("lateFee")
        );
    }

    private String generateAdminOverdueAlertTemplate(Map<String, Object> vars) {
        return String.format("""
                <html>
                <body style="font-family: Arial, sans-serif;">
                    <h2 style="color: #e74c3c;">Alerte Admin: Location en retard</h2>
                    <div style="background-color: #fadbd8; padding: 15px; border-radius: 5px;">
                        <p><strong>Réservation:</strong> #%s</p>
                        <p><strong>Client:</strong> #%s</p>
                        <p><strong>Véhicule:</strong> #%s</p>
                        <p><strong>Date de fin:</strong> %s</p>
                    </div>
                    <p>Action requise: Contacter le client immédiatement.</p>
                </body>
                </html>
                """,
                vars.get("rentalId"),
                vars.get("userId"),
                vars.get("vehicleId"),
                vars.get("endDate")
        );
    }

    private String generateAnalyticsRecommendationsTemplate(Map<String, Object> vars) {
        return String.format("""
                <html>
                <body style="font-family: Arial, sans-serif;">
                    <h2 style="color: #3498db;">Recommandations Analytics</h2>
                    <p>Généré le: %s</p>
                    <div style="background-color: #ebf5fb; padding: 15px; border-radius: 5px;">
                        <pre style="white-space: pre-wrap;">%s</pre>
                    </div>
                </body>
                </html>
                """,
                vars.get("generatedAt"),
                vars.get("recommendations")
        );
    }

    private String generateDefaultTemplate(Map<String, Object> vars) {
        StringBuilder html = new StringBuilder("<html><body style=\"font-family: Arial, sans-serif;\">");
        html.append("<h2>Notification</h2>");
        html.append("<div style=\"background-color: #ecf0f1; padding: 15px; border-radius: 5px;\">");

        vars.forEach((key, value) ->
                html.append("<p><strong>").append(key).append(":</strong> ").append(value).append("</p>")
        );

        html.append("</div></body></html>");
        return html.toString();
    }
}
