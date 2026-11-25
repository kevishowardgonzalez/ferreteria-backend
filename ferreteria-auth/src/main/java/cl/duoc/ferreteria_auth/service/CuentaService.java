package cl.duoc.ferreteria_auth.service;

import cl.duoc.ferreteria_auth.dto.ActualizarCuentaRequest;
import cl.duoc.ferreteria_auth.dto.ActualizarPerfilRequest;
import cl.duoc.ferreteria_auth.dto.CrearCuentaRequest;
import cl.duoc.ferreteria_auth.dto.CuentaResponse;
import cl.duoc.ferreteria_auth.exception.RecursoNoEncontradoException;
import cl.duoc.ferreteria_auth.exception.ReglaNegocioException;
import cl.duoc.ferreteria_auth.model.Rol;
import cl.duoc.ferreteria_auth.model.Usuario;
import cl.duoc.ferreteria_auth.repository.UsuarioRepository;
import cl.duoc.ferreteria_auth.security.JwtService.Identidad;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class CuentaService {
    private final UsuarioRepository usuarios;
    private final FerreteriaService auth;

    public CuentaService(UsuarioRepository usuarios, FerreteriaService auth) {
        this.usuarios = usuarios;
        this.auth = auth;
    }

    public List<CuentaResponse> listar() {
        return usuarios.findAll().stream().map(this::respuesta).toList();
    }

    public CuentaResponse buscar(Long id) {
        return respuesta(usuarios.findById(id).orElseThrow(this::cuentaNoEncontrada));
    }

    public CuentaResponse crear(CrearCuentaRequest datos) {
        return respuesta(auth.crearCuenta(datos.run(), datos.nombre(), datos.apellido(),
                datos.correo(), datos.password(), validarRol(datos.rol())));
    }

    @Transactional
    public CuentaResponse actualizar(Long id, ActualizarCuentaRequest datos, Identidad actor) {
        List<Usuario> admins = bloquearAdmins(actor);
        Usuario cuenta = usuarios.findForUpdateById(id).orElseThrow(this::cuentaNoEncontrada);
        String rol = datos.rol() == null ? cuenta.getRol() : validarRol(datos.rol()).name();
        Boolean activo = datos.activo() == null ? cuenta.getActivo() : datos.activo();
        protegerUltimoAdmin(cuenta, rol, activo, admins.size());
        actualizarNombres(cuenta, datos.nombre(), datos.apellido());
        cuenta.setRol(rol);
        cuenta.setActivo(activo);
        return respuesta(cuenta);
    }

    @Transactional
    public void desactivar(Long id, Identidad actor) {
        List<Usuario> admins = bloquearAdmins(actor);
        Usuario cuenta = usuarios.findForUpdateById(id).orElseThrow(this::cuentaNoEncontrada);
        protegerUltimoAdmin(cuenta, cuenta.getRol(), false, admins.size());
        cuenta.setActivo(false);
    }

    public CuentaResponse perfil(Identidad identidad) {
        return respuesta(exigirTitularActivo(usuarios.findById(identidad.id()).orElse(null), identidad));
    }

    @Transactional
    public CuentaResponse actualizarPerfil(Identidad identidad, ActualizarPerfilRequest datos) {
        Usuario cuenta = exigirTitularActivo(usuarios.findForUpdateById(identidad.id()).orElse(null), identidad);
        actualizarNombres(cuenta, datos.nombre(), datos.apellido());
        return respuesta(cuenta);
    }

    private List<Usuario> bloquearAdmins(Identidad actor) {
        // Todas las bajas y cambios de rol toman primero los mismos bloqueos.
        List<Usuario> admins = usuarios.bloquearAdminsActivos();
        if (admins.stream().noneMatch(u -> actor.id().equals(u.getId()))) {
            exigirTitularActivo(usuarios.findForUpdateById(actor.id()).orElse(null), actor);
            throw new AccessDeniedException("Se requiere un administrador activo");
        }
        return admins;
    }

    private Usuario exigirTitularActivo(Usuario cuenta, Identidad identidad) {
        if (cuenta == null || !identidad.correo().equals(cuenta.getCorreo())
                || !Boolean.TRUE.equals(cuenta.getActivo())) {
            throw new BadCredentialsException("Cuenta inválida");
        }
        return cuenta;
    }

    private void protegerUltimoAdmin(Usuario cuenta, String rol, Boolean activo, int adminsActivos) {
        if ("ADMIN".equals(cuenta.getRol()) && Boolean.TRUE.equals(cuenta.getActivo())
                && (!"ADMIN".equals(rol) || !Boolean.TRUE.equals(activo)) && adminsActivos <= 1) {
            throw new ReglaNegocioException("Debe quedar al menos un administrador activo");
        }
    }

    private void actualizarNombres(Usuario cuenta, String nombre, String apellido) {
        if (nombre != null) {
            cuenta.setNombre(nombreValido(nombre));
        }
        if (apellido != null) {
            cuenta.setApellido(nombreValido(apellido));
        }
    }

    private String nombreValido(String valor) {
        if (valor.isBlank()) {
            throw new ReglaNegocioException("El nombre y el apellido no pueden estar en blanco");
        }
        return valor.trim();
    }

    private Rol validarRol(String valor) {
        try {
            return Rol.valueOf(valor.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new ReglaNegocioException("Rol no válido: use ADMIN, VENDEDOR o CLIENTE");
        }
    }

    private RecursoNoEncontradoException cuentaNoEncontrada() {
        return new RecursoNoEncontradoException("Cuenta no encontrada");
    }

    private CuentaResponse respuesta(Usuario cuenta) {
        return new CuentaResponse(cuenta.getId(), cuenta.getRun(), cuenta.getNombre(), cuenta.getApellido(),
                cuenta.getCorreo(), cuenta.getRol(), cuenta.getActivo());
    }
}
