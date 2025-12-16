
package com.NovaBike.service;

import com.NovaBike.security.TokenGenerator;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {

    private final JavaMailSender mailSender;

    public PasswordService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarCorreoRecuperacion(String email) {

        //  Generar token único
        String token = TokenGenerator.generarToken();

        //  URL de recuperación
        String url = "http://localhost:80/reset-password?token=" + token;

        //  Crear el mensaje
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Recuperación de contraseña - NovaBike");
        message.setText("""
                Has solicitado restablecer tu contraseña.

                Haz clic en el siguiente enlace para continuar:
                """ + url + """

                Si no solicitaste este cambio, ignora este mensaje.
                """);

        //  Enviar correo
        mailSender.send(message);

        System.out.println("Correo de recuperación enviado a: " + email);
        System.out.println("Token generado: " + token);
    }

    public void restablecerPassword(String token, String password) {
        System.out.println("Token recibido" + token);
        System.out.println("Nueva contraseña" + password);

        // 1. Buscar el token en la base de datos (falta)
        // 2. Buscar el usuario asociado          (falta)
        // 3. Encriptar la nueva contraseña       (falta)
        // 4. Guardarla en la BD                  (falta)
        // 5. Invalidar el token                  (falta)
        
        System.out.println("Metodo restablacerPassword ejecutado correctamente(aun no funciona)");
    }
}
