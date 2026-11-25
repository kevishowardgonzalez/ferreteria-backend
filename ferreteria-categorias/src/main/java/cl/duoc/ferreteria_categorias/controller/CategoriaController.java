package cl.duoc.ferreteria_categorias.controller;

import cl.duoc.ferreteria_categorias.dto.CategoriaRequest;
import cl.duoc.ferreteria_categorias.model.Categoria;
import cl.duoc.ferreteria_categorias.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categorias")
@Tag(name = "Categorías", description = "Catálogo de categorías de productos")
public class CategoriaController {

    private final CategoriaService service;

    public CategoriaController(CategoriaService service) {
        this.service = service;
    }

    @Operation(summary = "Listar todas las categorías")
    @GetMapping
    public List<Categoria> listar() {
        return service.listar();
    }

    @Operation(summary = "Listar solo categorías activas (tienda)")
    @GetMapping("/activas")
    public List<Categoria> listarActivas() {
        return service.listarActivas();
    }

    @Operation(summary = "Obtener categoría por ID")
    @GetMapping("/{id}")
    public Categoria buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @Operation(summary = "Crear categoría (ADMIN o VENDEDOR)")
    @PostMapping
    public ResponseEntity<Categoria> crear(@Valid @RequestBody CategoriaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @Operation(summary = "Actualizar categoría (ADMIN o VENDEDOR)")
    @PutMapping("/{id}")
    public Categoria actualizar(@PathVariable Long id, @Valid @RequestBody CategoriaRequest request) {
        return service.actualizar(id, request);
    }

    @Operation(summary = "Desactivar categoría (borrado lógico)")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desactivar(@PathVariable Long id) {
        service.desactivar(id);
    }
}
