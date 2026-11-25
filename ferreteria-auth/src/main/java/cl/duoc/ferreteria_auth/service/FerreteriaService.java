package cl.duoc.ferreteria_auth.service;

import cl.duoc.ferreteria_auth.dto.AuthResponse;
import cl.duoc.ferreteria_auth.dto.LoginRequest;
import cl.duoc.ferreteria_auth.dto.RefreshRequest;
import cl.duoc.ferreteria_auth.dto.RegistroRequest;
import cl.duoc.ferreteria_auth.exception.ReglaNegocioException;
import cl.duoc.ferreteria_auth.model.Rol;
import cl.duoc.ferreteria_auth.model.Usuario;
import cl.duoc.ferreteria_auth.repository.UsuarioRepository;
import cl.duoc.ferreteria_auth.security.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class FerreteriaService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public FerreteriaService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(request.getCorreo().trim())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }
        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new BadCredentialsException("Usuario desactivado");
        }
        return construirRespuesta(usuario);
    }

    /** Registro público: siempre crea un usuario con rol CLIENTE. */
    public AuthResponse register(RegistroRequest request) {
        String correo = request.getCorreo().trim().toLowerCase();
        if (usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            throw new ReglaNegocioException("El correo ya está registrado");
        }
        if (usuarioRepository.existsByRunIgnoreCase(request.getRun().trim())) {
            throw new ReglaNegocioException("El RUN ya está registrado");
        }

        Usuario nuevo = new Usuario(
                null,
                request.getRun().trim(),
                request.getNombre().trim(),
                request.getApellido().trim(),
                correo,
                passwordEncoder.encode(request.getPassword()),
                Rol.CLIENTE.name(),
                true
        );
        return construirRespuesta(usuarioRepository.save(nuevo));
    }

    /**
     * Renovación de token: valida el token recibido, extrae el correo y emite uno nuevo
     * releyendo el usuario desde la BD.
     */
    public AuthResponse refresh(RefreshRequest request) {
        try {
            Claims claims = jwtService.validar(request.getToken());
            String correo = claims.getSubject();

            Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(correo)
                    .orElseThrow(() -> new BadCredentialsException("Usuario no existe"));
            if (!Boolean.TRUE.equals(usuario.getActivo())) {
                throw new BadCredentialsException("Usuario desactivado");
            }
            return construirRespuesta(usuario);
        } catch (JwtException | IllegalArgumentException e) {
            throw new BadCredentialsException("Token inválido o expirado");
        }
    }

    private AuthResponse construirRespuesta(Usuario u) {
        String rol = u.getRol();
        if (!"ADMIN".equals(rol) && !"VENDEDOR".equals(rol) && !"CLIENTE".equals(rol)) {
            throw new BadCredentialsException("Rol invalido");
        }
        return new AuthResponse(jwtService.generarToken(u), u.getId(), u.getNombre(),
                u.getApellido(), u.getCorreo(), u.getRol());
    }
}