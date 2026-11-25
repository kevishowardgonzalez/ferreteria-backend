package cl.duoc.ferreteria_roles.service;

import cl.duoc.ferreteria_roles.dto.RolRequest;
import cl.duoc.ferreteria_roles.exception.RecursoNoEncontradoException;
import cl.duoc.ferreteria_roles.exception.ReglaNegocioException;
import cl.duoc.ferreteria_roles.model.Rol;
import cl.duoc.ferreteria_roles.repository.RolRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolService {

    private final RolRepository repository;

    public RolService(RolRepository repository) {
        this.repository = repository;
    }

    public List<Rol> listar() {
        return repository.findAll();
    }

    public Rol buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Rol no encontrado: " + id));
    }

    public Rol crear(RolRequest req) {
        String nombre = req.getNombre().trim().toUpperCase();
        if (repository.existsByNombreIgnoreCase(nombre)) {
            throw new ReglaNegocioException("El rol ya existe: " + nombre);
        }
        Rol rol = new Rol(
                null,
                nombre,
                req.getDescripcion().trim(),
                req.getActivo() != null ? req.getActivo() : true);
        return repository.save(rol);
    }

    public Rol actualizar(Long id, RolRequest req) {
        Rol rol = buscar(id);
        rol.setNombre(req.getNombre().trim().toUpperCase());
        rol.setDescripcion(req.getDescripcion().trim());
        if (req.getActivo() != null) {
            rol.setActivo(req.getActivo());
        }
        return repository.save(rol);
    }

    public void desactivar(Long id) {
        Rol rol = buscar(id);
        rol.setActivo(false);
        repository.save(rol);
    }
}
