package cl.duoc.ferreteria_auth.controller;

import cl.duoc.ferreteria_auth.dto.ActualizarCuentaRequest;
import cl.duoc.ferreteria_auth.dto.ActualizarPerfilRequest;
import cl.duoc.ferreteria_auth.dto.CrearCuentaRequest;
import cl.duoc.ferreteria_auth.dto.CuentaResponse;
import cl.duoc.ferreteria_auth.security.JwtService.Identidad;
import cl.duoc.ferreteria_auth.service.CuentaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/v1/cuentas")
public class CuentaInternaController {
    private final CuentaService cuentas;

    public CuentaInternaController(CuentaService cuentas) {
        this.cuentas = cuentas;
    }

    @GetMapping
    public List<CuentaResponse> listar() {
        return cuentas.listar();
    }

    @GetMapping("/{id}")
    public CuentaResponse buscar(@PathVariable Long id) {
        return cuentas.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CuentaResponse crear(@Valid @RequestBody CrearCuentaRequest datos) {
        return cuentas.crear(datos);
    }

    @PutMapping("/{id}")
    public CuentaResponse actualizar(@PathVariable Long id, @RequestBody ActualizarCuentaRequest datos,
                                    @AuthenticationPrincipal Identidad actor) {
        return cuentas.actualizar(id, datos, actor);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desactivar(@PathVariable Long id, @AuthenticationPrincipal Identidad actor) {
        cuentas.desactivar(id, actor);
    }

    @GetMapping("/me")
    public CuentaResponse perfil(@AuthenticationPrincipal Identidad identidad) {
        return cuentas.perfil(identidad);
    }

    @PutMapping("/me")
    public CuentaResponse actualizarPerfil(@AuthenticationPrincipal Identidad identidad,
                                          @RequestBody ActualizarPerfilRequest datos) {
        return cuentas.actualizarPerfil(identidad, datos);
    }
}
