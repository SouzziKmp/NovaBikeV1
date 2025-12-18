package com.NovaBike.service;

import com.NovaBike.domain.PasswordResetToken;
import com.NovaBike.domain.Usuario;
import com.NovaBike.repository.PasswordResetTokenRepository;
import com.NovaBike.repository.UsuarioRepository;
import com.NovaBike.security.TokenGenerator;
import java.time.LocalDateTime;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {

    private final JavaMailSender mailSender;
    private final PasswordResetTokenRepository tokenRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordService(JavaMailSender mailSender,
                           PasswordResetTokenRepository tokenRepository,
                           UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder) {

        this.mailSender = mailSender;
        this.tokenRepository = tokenRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void enviarCorreoRecuperacion(String email) {

        // Buscar Usuario
        Usuario usuario = usuarioRepository.findByUsernameOrCorreo(email, email)
        .orElseThrow(() -> new RuntimeException("Usuario no existe"));

        // generar Token
        String token = TokenGenerator.generarToken();

        // Crear y guardar Token
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUsuario(usuario);
        resetToken.setExpiration(LocalDateTime.now().plusMinutes(30));

        tokenRepository.save(resetToken);

        // URL
        String url = "http://localhost:80/reset-password?token=" + token;

        // Enviar Correo 
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Recuperación de contraseña - NovaBike");
        message.setText("""
                Has solicitado restablecer tu contraseña.

                Haz clic en el siguiente enlace:
                """ + url + """

                El enlace expira en 30 minutos.
                """);

        mailSender.send(message);
    }

    public void restablecerPassword(String token, String password) {

        // BuscarToken
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        // Validar Expiracion
        if (resetToken.getExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expirado");
        }

        // Obtener Usuario
        Usuario usuario = resetToken.getUsuario();

        // Encriptar Password
        usuario.setPassword(passwordEncoder.encode(password));
        usuarioRepository.save(usuario);

        //Invalidar Token
        tokenRepository.delete(resetToken);
    }
}