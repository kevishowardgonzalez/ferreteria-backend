package cl.duoc.ferreteria_foro.controller;

import cl.duoc.ferreteria_foro.dto.ComentarioRequest;
import cl.duoc.ferreteria_foro.dto.PublicacionRequest;
import cl.duoc.ferreteria_foro.model.Publicacion;
import cl.duoc.ferreteria_foro.service.ForoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/foro")
@Tag(name = "Foro", description = "Comunidad, preguntas y consejos de ferretería")
public class ForoController {

    private final ForoService service;

    public ForoController(ForoService service) {
        this.service = service;
    }

    @Operation(summary = "Listar publicaciones (Público)")
    @GetMapping
    public List<Publicacion> listar() {
        return service.listar();
    }

    @Operation(summary = "Ver detalle de una publicación (Público)")
    @GetMapping("/{id}")
    public Publicacion buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @Operation(summary = "Crear publicación (Usuario autenticado)")
    @PostMapping
    public ResponseEntity<Publicacion> crear(@Valid @RequestBody PublicacionRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearPublicacion(auth.getName(), request));
    }

    @Operation(summary = "Comentar en una publicación (Usuario autenticado)")
    @PostMapping("/{id}/comentarios")
    public Publicacion comentar(@PathVariable Long id, @Valid @RequestBody ComentarioRequest request,
            Authentication auth) {
        return service.agregarComentario(id, auth.getName(), request);
    }

    @Operation(summary = "Eliminar publicación (Solo ADMIN)")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminarPublicacion(id);
    }
}