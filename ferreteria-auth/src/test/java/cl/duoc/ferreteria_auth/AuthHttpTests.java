package cl.duoc.ferreteria_auth;

import cl.duoc.ferreteria_auth.repository.UsuarioRepository;
import cl.duoc.ferreteria_auth.security.JwtService;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthHttpTests {
    @LocalServerPort
    int port;

    @Autowired UsuarioRepository usuarios;
    @Autowired PasswordEncoder encoder;
    @Autowired JwtService jwt;
    @Autowired ObjectMapper json;
    @Autowired JdbcTemplate jdbc;
    @Value("${app.jwt.expiration-ms}") long accessTtlMs;

    private final HttpClient client = HttpClient.newHttpClient();

    @Test
    void registroLoginRenovacionRevocacionYCuentaInactiva() throws Exception {
        String correo = "cliente-etapa2@example.test";
        String password = "prueba123";
        var registro = post("register", Map.of(
                "run", "11222333-4", "nombre", "Ana", "apellido", "Prueba",
                "correo", correo, "password", password, "rol", "ADMIN"), 201);
        assertEquals("CLIENTE", registro.get("rol").asText());
        String access = registro.get("token").asText();
        verificarAccessToken(access, correo);
        assertThrows(io.jsonwebtoken.JwtException.class, () -> jwt.validar("token-invalido"));
        var usuario = usuarios.findByCorreoIgnoreCase(correo).orElseThrow();
        assertNotEquals(password, usuario.getPassword());
        assertTrue(encoder.matches(password, usuario.getPassword()));

        post("login", Map.of("correo", correo, "password", "incorrecta"), 401);
        var login = post("login", Map.of("correo", correo, "password", password), 200);
        assertEquals("CLIENTE", login.get("rol").asText());
        post("refresh", Map.of("refreshToken", access), 401);

        String anterior = registro.get("refreshToken").asText();
        var renovada = post("refresh", Map.of("refreshToken", anterior), 200);
        verificarAccessToken(renovada.get("token").asText(), correo);
        String siguiente = renovada.get("refreshToken").asText();
        assertNotEquals(anterior, siguiente);
        post("refresh", Map.of("refreshToken", anterior), 409);
        post("logout", Map.of("refreshToken", siguiente), 204);
        post("refresh", Map.of("refreshToken", siguiente), 401);

        usuario.setActivo(false);
        usuarios.save(usuario);
        post("login", Map.of("correo", correo, "password", password), 401);
        post("refresh", Map.of("refreshToken", login.get("refreshToken").asText()), 401);
        usuario.setActivo(true);
        usuarios.save(usuario);
        post("refresh", Map.of("refreshToken", login.get("refreshToken").asText()), 401);
    }

    private void verificarAccessToken(String token, String correo) {
        var claims = jwt.validar(token);
        assertEquals(correo, claims.getSubject());
        // JWT NumericDate serializa las marcas de tiempo con precisión de segundos.
        long expiracion = claims.getExpiration().getTime();
        long emision = claims.getIssuedAt().getTime();
        assertTrue(Math.abs(expiracion - emision - accessTtlMs) < 1_000);
    }

    @Test
    void logoutConCredencialAnteriorRevocaElSucesor() throws Exception {
        String token = registrar("logout-anterior");
        String siguiente = post("refresh", Map.of("refreshToken", token), 200).get("refreshToken").asText();
        post("logout", Map.of("refreshToken", token), 204);
        post("refresh", Map.of("refreshToken", siguiente), 401);
    }

    @Test
    void logoutConcurrenteConRefreshNoDejaUnSucesorValido() throws Exception {
        String token = registrar("logout-concurrente");
        var data = Map.of("refreshToken", token);
        var refresh = client.sendAsync(request("refresh", data), HttpResponse.BodyHandlers.ofString());
        var logout = client.sendAsync(request("logout", data), HttpResponse.BodyHandlers.ofString());

        var renovada = refresh.get(10, TimeUnit.SECONDS);
        assertEquals(204, logout.get(10, TimeUnit.SECONDS).statusCode());
        assertTrue(List.of(200, 401).contains(renovada.statusCode()));
        if (renovada.statusCode() == 200) {
            String sucesor = json.readTree(renovada.body()).get("refreshToken").asText();
            post("refresh", Map.of("refreshToken", sucesor), 401);
        }
        post("refresh", data, 401);
    }

    @Test
    void renovacionesSimultaneasConservanUnaSesionValida() throws Exception {
        String token = registrar("duplicada");
        var solicitud = request("refresh", Map.of("refreshToken", token));
        var primera = client.sendAsync(solicitud, HttpResponse.BodyHandlers.ofString());
        var segunda = client.sendAsync(solicitud, HttpResponse.BodyHandlers.ofString());
        var resultados = List.of(primera.get(10, TimeUnit.SECONDS), segunda.get(10, TimeUnit.SECONDS));
        assertEquals(List.of(200, 409), resultados.stream().map(HttpResponse::statusCode).sorted().toList());
        var exitosa = resultados.stream().filter(r -> r.statusCode() == 200).findFirst().orElseThrow();
        String sucesor = json.readTree(exitosa.body()).get("refreshToken").asText();
        post("refresh", Map.of("refreshToken", sucesor), 200);
    }

    @Test
    void reutilizacionPosteriorRevocaLaSesionPeroNoOtraSesionDelUsuario() throws Exception {
        String token = registrar("reutilizada");
        String anterior = post("refresh", Map.of("refreshToken", token), 200).get("refreshToken").asText();
        String sucesor = post("refresh", Map.of("refreshToken", anterior), 200).get("refreshToken").asText();
        String otraSesion = post("login", Map.of("correo", "reutilizada@example.test", "password", "prueba123"), 200)
                .get("refreshToken").asText();
        // Envejece las credenciales consumidas sin introducir esperas en la prueba.
        assertEquals(2, jdbc.update("update auth_refresh_consumed set consumed_at = ? where session_id = ?",
                Timestamp.from(Instant.now().minusSeconds(30)), token.substring(0, 64)));
        post("refresh", Map.of("refreshToken", token), 401);
        post("refresh", Map.of("refreshToken", sucesor), 401);
        post("refresh", Map.of("refreshToken", otraSesion), 200);
    }

    private String registrar(String identificador) throws Exception {
        return post("register", Map.of(
                "run", identificador, "nombre", "Ana", "apellido", "Prueba",
                "correo", identificador + "@example.test", "password", "prueba123"), 201)
                .get("refreshToken").asText();
    }

    private HttpRequest request(String route, Map<String, String> data) {
        return HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/v1/auth/" + route))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json.writeValueAsString(data)))
                .build();
    }

    private JsonNode post(String route, Map<String, String> data, int expected) throws Exception {
        var response = client.send(request(route, data), HttpResponse.BodyHandlers.ofString());
        assertEquals(expected, response.statusCode(), route);
        return response.body().isEmpty() ? null : json.readTree(response.body());
    }
}
