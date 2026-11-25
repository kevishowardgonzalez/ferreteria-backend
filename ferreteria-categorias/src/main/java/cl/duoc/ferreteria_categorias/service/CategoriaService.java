package cl.duoc.ferreteria_categorias.service;

import cl.duoc.ferreteria_categorias.dto.CategoriaRequest;
import cl.duoc.ferreteria_categorias.exception.RecursoNoEncontradoException;
import cl.duoc.ferreteria_categorias.exception.ReglaNegocioException;
import cl.duoc.ferreteria_categorias.model.Categoria;
import cl.duoc.ferreteria_categorias.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository repository;

    public CategoriaService(CategoriaRepository repository) {
        this.repository = repository;
    }

    public List<Categoria> listar() {
        return repository.findAll();
    }

    public List<Categoria> listarActivas() {
        return repository.findByActivoTrue();
    }

    public Categoria buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada: " + id));
    }

    public Categoria crear(CategoriaRequest req) {
        String nombre = req.getNombre().trim();
        if (repository.existsByNombreIgnoreCase(nombre)) {
            throw new ReglaNegocioException("Ya existe una categoría con ese nombre");
        }
        Categoria cat = new Categoria(
                null,
                nombre,
                req.getDescripcion() != null ? req.getDescripcion().trim() : "",
                req.getActivo() != null ? req.getActivo() : true);
        return repository.save(cat);
    }

    public Categoria actualizar(Long id, CategoriaRequest req) {
        Categoria cat = buscar(id);
        String nombre = req.getNombre().trim();
        if (repository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new ReglaNegocioException("Ya existe una categoría con ese nombre");
        }
        cat.setNombre(nombre);
        if (req.getDescripcion() != null) {
            cat.setDescripcion(req.getDescripcion().trim());
        }
        if (req.getActivo() != null) {
            cat.setActivo(req.getActivo());
        }
        return repository.save(cat);
    }

    public void desactivar(Long id) {
        Categoria cat = buscar(id);
        cat.setActivo(false);
        repository.save(cat);
    }
}
