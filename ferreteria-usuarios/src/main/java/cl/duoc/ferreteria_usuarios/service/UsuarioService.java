package cl.duoc.ferreteria_usuarios.service;

import cl.duoc.ferreteria_usuarios.dto.*;
import cl.duoc.ferreteria_usuarios.exception.RecursoNoEncontradoException;
import cl.duoc.ferreteria_usuarios.exception.ReglaNegocioException;
import cl.duoc.ferreteria_usuarios.model.Usuario;
import cl.duoc.ferreteria_usuarios.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UsuarioResponse> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public UsuarioResponse buscar(Long id) {
        Usuario u = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado: " + id));
        return toResponse(u);
    }

    public UsuarioResponse crear(UsuarioRequest req) {
        String correo = req.getCorreo().trim().toLowerCase();
        if (repository.existsByCorreoIgnoreCase(correo)) {
            throw new ReglaNegocioException("El correo ya está registrado");
        }
        if (repository.existsByRunIgnoreCase(req.getRun().trim())) {
            throw new ReglaNegocioException("El RUN ya está registrado");
        }

        Usuario u = new Usuario(
                null,
                req.getRun().trim(),
                req.getNombre().trim(),
                req.getApellido().trim(),
                correo,
                passwordEncoder.encode(req.getPassword()),
                validarRol(req.getRol()),
                true);
        return toResponse(repository.save(u));
    }

    public UsuarioResponse actualizar(Long id, ActualizarUsuarioRequest req) {
        Usuario u = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado: " + id));

        if (req.getNombre() != null && !req.getNombre().isBlank())
            u.setNombre(req.getNombre().trim());
        if (req.getApellido() != null && !req.getApellido().isBlank())
            u.setApellido(req.getApellido().trim());
        u.setRol(validarRol(req.getRol()));
        if (req.getActivo() != null)
            u.setActivo(req.getActivo());

        return toResponse(repository.save(u));
    }

    public void desactivar(Long id) {
        Usuario u = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado: " + id));
        u.setActivo(false);
        repository.save(u);
    }

    private String validarRol(String rol) {
        String valor = rol.trim().toUpperCase();
        if (!valor.equals("ADMIN") && !valor.equals("VENDEDOR") && !valor.equals("CLIENTE")) {
            throw new ReglaNegocioException("Rol no válido: use ADMIN, VENDEDOR o CLIENTE");
        }
        return valor;
    }

    private UsuarioResponse toResponse(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getRun(), u.getNombre(),
                u.getApellido(), u.getCorreo(), u.getRol(), u.getActivo());
    }
}