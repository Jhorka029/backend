package com.iespp.app.services;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Método para el registro de usuarios
    public void enviarCorreoVerificacion(String correoDestino, String nombreUsuario, String codigoVerificacion) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(correoDestino);
            helper.setSubject("Confirma tu cuenta - IESPP Monseñor Elías Olázar");

            // Esta es la URL a la que el usuario hará clic (apunta a nuestro backend)
            String urlVerificacion = "https://cevicherias.spring.informaticapp.com/seguridad%20EESSP/backend/api/auth/verify?code=" + codigoVerificacion;

            String contenidoHtml = "<div style='font-family: Arial, sans-serif; padding: 20px; color: #333;'>"
                    + "<h2 style='color: #004085;'>¡Bienvenido al sistema, " + nombreUsuario + "!</h2>"
                    + "<p>Para completar tu registro y asegurar tu cuenta, por favor haz clic en el siguiente botón:</p>"
                    + "<a href='" + urlVerificacion + "' style='display: inline-block; padding: 10px 20px; color: white; background-color: #004085; text-decoration: none; border-radius: 5px;'>Verificar mi cuenta</a>"
                    + "<p style='margin-top: 20px; font-size: 12px; color: #666;'>Si no solicitaste este registro, ignora este correo.</p>"
                    + "</div>";

            helper.setText(contenidoHtml, true); // El 'true' indica que es HTML
            mailSender.send(message);

        } catch (Exception e) {
            System.err.println("Error al enviar el correo de verificación: " + e.getMessage());
        }
    }

    // Método para recuperar contraseña con Código de Seguridad
    public void enviarCorreoRecuperacion(String correoDestino, String codigoSeguridad) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(correoDestino);
            helper.setSubject("Código de Recuperación - IESPP");

            String contenidoHtml = "<div style='font-family: Arial, sans-serif; padding: 20px; color: #333; text-align: center;'>"
                    + "<h2 style='color: #db4437;'>Recuperación de Contraseña</h2>"
                    + "<p>Has solicitado restablecer tu contraseña. Copia el siguiente código de seguridad y pégalo en el sistema:</p>"
                    + "<div style='margin: 20px auto; padding: 15px; background-color: #f4f4f4; border-radius: 8px; display: inline-block; letter-spacing: 5px; font-size: 24px; font-weight: bold; color: #004085;'>"
                    + codigoSeguridad 
                    + "</div>"
                    + "<p style='margin-top: 20px; font-size: 12px; color: #666;'>Si no solicitaste este cambio, ignora este correo.</p>"
                    + "</div>";

            helper.setText(contenidoHtml, true);
            mailSender.send(message);

        } catch (Exception e) {
            System.err.println("Error al enviar el correo de recuperación: " + e.getMessage());
        }
    }
}