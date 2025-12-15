
package com.NovaBike.service;
import com.NovaBike.domain.Usuario;
import com.NovaBike.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("userDetailsService")
public class UsuarioDetailsService implements UserDetailsService{
    
    private final UsuarioRepository usuarioRepository;
    private final HttpSession session;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository, HttpSession session) {
        this.usuarioRepository = usuarioRepository;
        this.session = session;
    }
    
    @Override
    @Transactional(readOnly=true)
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException{
        Usuario usuario= usuarioRepository.findByUsernameAndActivoTrue(username)
                .orElseThrow(()-> new UsernameNotFoundException("usuario no encontrado: "+ username));
       
        //
        session.removeAttribute("imagenUsuario");
        session.setAttribute("imagenUsuario", usuario.getRutaImagen());
        
        
        
        // se cargan roles de usuario y se generan como roles de seguridad 
        
        var roles = usuario.getRoles().stream()
                .map(rol -> new SimpleGrantedAuthority("ROLE_"+rol.getRol()))
                .collect(Collectors.toSet());
        
        //se retorna usuario con su info
        return new User(usuario.getUsername(),usuario.getPassword(),roles);
    }
}
