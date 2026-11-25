package cl.duoc.ferreteria_auth.controller;

import cl.duoc.ferreteria_auth.dto.AuthResponse;
import cl.duoc.ferreteria_auth.dto.LoginRequest;
import cl.duoc.ferreteria_auth.dto.RefreshRequest;
import cl.duoc.ferreteria_auth.dto.RegistroRequest;
import cl.duoc.ferreteria_auth.service.FerreteriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación", description = "Login, registro y renovación de token JWT")
public class FerreteriaController {

    private final FerreteriaService ferreteriaService;

    public FerreteriaController(FerreteriaService ferreteriaService) {
        this.ferreteriaService = ferreteriaService;
    }

    @Operation(summary = "Iniciar sesión", description = "Devuelve un token JWT y los datos básicos del usuario. Público.")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(ferreteriaService.login(request));
    }

    @Operation(summary = "Registrar cliente", description = "Crea una cuenta con rol CLIENTE. Público.")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegistroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).cacheControl(CacheControl.noStore()).body(ferreteriaService.register(request));
    }

    @Operation(summary = "Renovar token", description = "Recibe refreshToken y devuelve un nuevo par de tokens.")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(ferreteriaService.refresh(request));
    }
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequest request) {
        ferreteriaService.logout(request);
        return ResponseEntity.noContent().build();
    }
}