package cl.duoc.ferreteria_auth.security;

import cl.duoc.ferreteria_auth.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRET = "ZmVycmV0ZXJpYUxvc01hZXN0cm9zMjAyNkR1b2NTZWNyZXRLZXk=";
    private JwtService jwtService;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", SECRET);
        ReflectionTestUtils.setField(jwtService, "issuer", "ferreteria-auth");
        ReflectionTestUtils.setField(jwtService, "expirationMs", 3600000L);
        usuario = new Usuario(1L, "12345678-5", "Kevis", "Admin", "admin@duoc.cl", "hash", "ADMIN", true);
    }

    @Test
    void tokenGeneradoContieneCorreoYRol() {
        Claims claims = jwtService.validar(jwtService.generarToken(usuario));
        assertEquals("admin@duoc.cl", claims.getSubject());
        assertEquals("ADMIN", claims.get("rol", String.class));
    }

    @Test
    void tokenAlteradoNoEsValido() {
        String token = jwtService.generarToken(usuario);
        assertFalse(jwtService.esValido(token + "x"));
        assertThrows(JwtException.class, () -> jwtService.validar(token + "x"));
    }

    @Test
    void tokenExpiradoNoEsValido() {
        ReflectionTestUtils.setField(jwtService, "expirationMs", -1000L);
        assertFalse(jwtService.esValido(jwtService.generarToken(usuario)));
    }

    @Test
    void tokenConOtroEmisorNoEsValido() {
        String token = jwtService.generarToken(usuario);
        ReflectionTestUtils.setField(jwtService, "issuer", "otro-emisor");
        assertFalse(jwtService.esValido(token));
    }
}
