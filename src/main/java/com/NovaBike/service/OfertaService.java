package com.NovaBike.service;

import com.NovaBike.domain.Usuario;
import com.NovaBike.repository.UsuarioRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OfertaService {

    private final UsuarioRepository usuarioRepository;
    private final CorreoService correoService;

    public OfertaService(UsuarioRepository usuarioRepository,
            CorreoService correoService) {
        this.usuarioRepository = usuarioRepository;
        this.correoService = correoService;
    }

    public void enviarOfertasATodos(String asunto, String mensajeHtml) {

        List<Usuario> usuarios
                = usuarioRepository.findByActivoTrueAndCorreoIsNotNull();

        usuarios.forEach(usuario -> {
            try {
                correoService.enviarCorreoHtml(
                        usuario.getCorreo(),
                        asunto,
                        mensajeHtml
                );
            } catch (Exception e) {
                System.err.println("Error enviando correo a: " + usuario.getCorreo());
            }
        });
    }
}
