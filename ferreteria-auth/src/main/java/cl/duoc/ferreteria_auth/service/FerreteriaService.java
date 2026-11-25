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
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Locale;

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
        return construirRespuesta(crearCuenta(request.getRun(), request.getNombre(), request.getApellido(),
                request.getCorreo(), request.getPassword(), Rol.CLIENTE));
    }

    Usuario crearCuenta(String runOriginal, String nombre, String apellido,
                        String correoOriginal, String password, Rol rol) {
        String correo = correoOriginal.trim().toLowerCase(Locale.ROOT);
        String run = runOriginal.trim().toUpperCase(Locale.ROOT);
        if (usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            throw new ReglaNegocioException("El correo ya está registrado");
        }
        if (usuarioRepository.existsByRunIgnoreCase(run)) {
            throw new ReglaNegocioException("El RUN ya está registrado");
        }

        Usuario nuevo = new Usuario(
                null,
                run,
                nombre.trim(),
                apellido.trim(),
                correo,
                passwordEncoder.encode(password),
                rol.name(),
                true
        );
        try {
            return usuarioRepository.saveAndFlush(nuevo);
        } catch (DataIntegrityViolationException e) {
            for (Throwable causa = e; causa != null; causa = causa.getCause()) {
                if (causa instanceof ConstraintViolationException restriccion
                        && restriccion.getKind() == ConstraintViolationException.ConstraintKind.UNIQUE) {
                    throw new ReglaNegocioException("El correo o el RUN ya está registrado");
                }
            }
            throw e;
        }
    }

    /**
     * Renovación de token: valida el token recibido, extrae el correo y emite uno nuevo
     * releyendo el usuario desde la BD.
     */
    public AuthResponse refresh(RefreshRequest request) {
        try {
            Claims claims = jwtService.validar(request.getToken());
            var identidad = jwtService.identidad(claims);
            Usuario usuario = usuarioRepository.findById(identidad.id())
                    .filter(u -> identidad.correo().equals(u.getCorreo()))
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
