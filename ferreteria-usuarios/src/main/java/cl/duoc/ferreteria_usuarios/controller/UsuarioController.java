package cl.duoc.ferreteria_usuarios.controller;

import cl.duoc.ferreteria_usuarios.dto.*;
import cl.duoc.ferreteria_usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
@Tag(name = "Usuarios",
        description = "Gestion administrativa y perfil propio")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @Operation(summary = "Listar usuarios")
    @GetMapping
    public List<UsuarioResponse> listar(
            @RequestHeader(HttpHeaders.AUTHORIZATION)
            String authorization) {

        return service.listar(authorization);
    }

    @Operation(summary = "Obtener usuario por ID")
    @GetMapping("/{id}")
    public UsuarioResponse buscar(
            @PathVariable Long id,
            @RequestHeader(HttpHeaders.AUTHORIZATION)
            String authorization) {

        return service.buscar(id, authorization);
    }

    @Operation(summary = "Crear usuario con rol especifico")
    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(
            @Valid @RequestBody UsuarioRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION)
            String authorization) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.crear(request, authorization));
    }

    @Operation(summary = "Actualizar usuario, rol o estado")
    @PutMapping("/{id}")
    public UsuarioResponse actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarUsuarioRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION)
            String authorization) {

        return service.actualizar(id, request, authorization);
    }

    @Operation(summary = "Desactivar usuario")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desactivar(
            @PathVariable Long id,
            @RequestHeader(HttpHeaders.AUTHORIZATION)
            String authorization) {

        service.desactivar(id, authorization);
    }

    @Operation(summary = "Consultar perfil propio")
    @GetMapping("/me")
    public UsuarioResponse obtenerPerfil(
            @RequestHeader(HttpHeaders.AUTHORIZATION)
            String authorization) {

        return service.obtenerPerfil(authorization);
    }

    @Operation(summary = "Actualizar perfil propio")
    @PutMapping("/me")
    public UsuarioResponse actualizarPerfil(
            @Valid @RequestBody ActualizarPerfilRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION)
            String authorization) {

        return service.actualizarPerfil(request, authorization);
    }
}