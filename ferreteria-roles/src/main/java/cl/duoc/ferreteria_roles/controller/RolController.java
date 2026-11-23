package cl.duoc.ferreteria_roles.controller;

import cl.duoc.ferreteria_roles.dto.RolRequest;
import cl.duoc.ferreteria_roles.model.RolEntity;
import cl.duoc.ferreteria_roles.service.RolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@Tag(name = "Roles", description = "Gestión del catálogo de roles (Solo ADMIN)")
public class RolController {

    private final RolService service;

    public RolController(RolService service) {
        this.service = service;
    }

    @Operation(summary = "Listar roles")
    @GetMapping
    public List<RolEntity> listar() {
        return service.listar();
    }

    @Operation(summary = "Obtener rol por ID")
    @GetMapping("/{id}")
    public RolEntity buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @Operation(summary = "Crear rol")
    @PostMapping
    public ResponseEntity<RolEntity> crear(@Valid @RequestBody RolRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @Operation(summary = "Actualizar rol")
    @PutMapping("/{id}")
    public RolEntity actualizar(@PathVariable Long id, @Valid @RequestBody RolRequest request) {
        return service.actualizar(id, request);
    }

    @Operation(summary = "Desactivar rol (borrado lógico)")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desactivar(@PathVariable Long id) {
        service.desactivar(id);
    }
}