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
import java.util.function.Supplier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FerreteriaService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshSessionService sesiones;

    public FerreteriaService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                            JwtService jwtService, RefreshSessionService sesiones) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.sesiones = sesiones;
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

    @Transactional(noRollbackFor = BadCredentialsException.class)
    public AuthResponse refresh(RefreshRequest request) {
        String token = request.getRefreshToken();
        var sesion = sesiones.obtenerVigente(token);
        Usuario usuario = usuarioRepository.findById(sesion.getUsuarioId())
                .filter(u -> Boolean.TRUE.equals(u.getActivo()))
                .orElseThrow(() -> {
                    sesiones.revocar(sesion);
                    return new BadCredentialsException("Usuario inactivo");
                });
        return construirRespuesta(usuario, () -> sesiones.rotar(sesion, token));
    }

    public void logout(RefreshRequest request) {
        sesiones.revocar(request.getRefreshToken());
    }

    private AuthResponse construirRespuesta(Usuario u) {
        return construirRespuesta(u, () -> sesiones.emitir(u.getId()));
    }

    private AuthResponse construirRespuesta(Usuario u, Supplier<String> refreshToken) {
        Rol rol;
        try {
            rol = Rol.valueOf(u.getRol());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new BadCredentialsException("Rol invalido");
        }
        return new AuthResponse(jwtService.generarToken(u), refreshToken.get(), u.getId(),
                u.getNombre(), u.getApellido(), u.getCorreo(), rol.name());
    }
}
