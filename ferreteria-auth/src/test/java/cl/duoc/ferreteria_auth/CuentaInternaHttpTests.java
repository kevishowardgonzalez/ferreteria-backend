package cl.duoc.ferreteria_auth;

import cl.duoc.ferreteria_auth.model.Usuario;
import cl.duoc.ferreteria_auth.repository.UsuarioRepository;
import cl.duoc.ferreteria_auth.security.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:cuentas-internas;MODE=MySQL;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
class CuentaInternaHttpTests {
    private static final String CUENTAS = "/internal/v1/cuentas";
    private static final String AUTH = "/api/v1/auth";
    private static final String PASSWORD = "Prueba123";

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired UsuarioRepository usuarios;
    @Autowired PasswordEncoder encoder;
    @Autowired JwtService jwt;
    @Value("${app.jwt.secret}") String secretoPruebas;
    @Value("${app.jwt.issuer}") String emisor;

    Usuario admin;
    String bearerAdmin;

    @BeforeEach
    void preparar() {
        usuarios.deleteAll();
        admin = guardar("101-1", "admin@example.test", "ADMIN");
        bearerAdmin = bearer(jwt.generarToken(admin));
    }

    @Test
    void registroPublicoImponeClienteActivoYPermiteLogin() throws Exception {
        mvc.perform(post(AUTH + "/register").contentType("application/json")
                        .content(alta("202-2", "registro@example.test", "ADMIN", false)))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.rol").value("CLIENTE"))
                .andExpect(jsonPath("$.password").doesNotExist());
        login("registro@example.test").andExpect(status().isOk())
                .andExpect(jsonPath("$.rol").value("CLIENTE"));
        assertTrue(usuarios.findByCorreoIgnoreCase("registro@example.test").orElseThrow().getActivo());
    }

    @Test
    void altasAdministrativasPermitenLoginYNoExponenCredenciales() throws Exception {
        altaAdmin("303-K", "Vendedor@Example.Test", "VENDEDOR")
                .andExpect(status().isCreated()).andExpect(jsonPath("$.correo").value("vendedor@example.test"))
                .andExpect(jsonPath("$.password").doesNotExist()).andExpect(jsonPath("$.hash").doesNotExist());
        altaAdmin("404-4", "cliente@example.test", "CLIENTE")
                .andExpect(status().isCreated()).andExpect(jsonPath("$.rol").value("CLIENTE"));
        login("vendedor@example.test").andExpect(status().isOk()).andExpect(jsonPath("$.rol").value("VENDEDOR"));
        login("cliente@example.test").andExpect(status().isOk()).andExpect(jsonPath("$.rol").value("CLIENTE"));
        Usuario vendedor = usuarios.findByCorreoIgnoreCase("vendedor@example.test").orElseThrow();
        assertTrue(vendedor.getPassword().startsWith("$2"));
        assertTrue(encoder.matches(PASSWORD, vendedor.getPassword()));
        mvc.perform(get(CUENTAS).header("Authorization", bearerAdmin))
                .andExpect(status().isOk()).andExpect(jsonPath("$[*].password").doesNotExist());
        mvc.perform(get(CUENTAS + "/{id}", vendedor.getId()).header("Authorization", bearerAdmin))
                .andExpect(status().isOk()).andExpect(jsonPath("$.password").doesNotExist());
        mvc.perform(get(CUENTAS + "/{id}", Long.MAX_VALUE).header("Authorization", bearerAdmin))
                .andExpect(status().isNotFound());
    }

    @Test
    void unicidadCompartidaYRespaldadaPorLaBase() throws Exception {
        altaAdmin("303-k", "vendedor@example.test", "VENDEDOR").andExpect(status().isCreated());
        altaAdmin("404-4", "VENDEDOR@example.test", "CLIENTE").andExpect(status().isBadRequest());
        altaAdmin("303-K", "otra@example.test", "CLIENTE").andExpect(status().isBadRequest());
        altaAdmin("505-5", "otro@example.test", "OTRO").andExpect(status().isBadRequest());
        mvc.perform(post(AUTH + "/register").contentType("application/json")
                        .content(alta("303-K", "publico@example.test", "CLIENTE", true)))
                .andExpect(status().isBadRequest());
        assertThrows(DataIntegrityViolationException.class,
                () -> guardar("303-K", "nuevo@example.test", "CLIENTE"));
        assertThrows(DataIntegrityViolationException.class,
                () -> guardar("404-4", "vendedor@example.test", "CLIENTE"));
    }

    @Test
    void cambioDeRolApareceEnLoginYRefreshYLaBajaBloqueaAmbos() throws Exception {
        Usuario cliente = guardar("202-2", "cliente@example.test", "CLIENTE");
        String anterior = jwt.generarToken(cliente);
        editar(cliente.getId(), "{\"rol\":\"VENDEDOR\",\"nombre\":null}").andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ana"));
        var loginNuevo = login(cliente.getCorreo()).andExpect(status().isOk())
                .andExpect(jsonPath("$.rol").value("VENDEDOR")).andReturn();
        var refreshNuevo = refresh(anterior).andExpect(status().isOk())
                .andExpect(jsonPath("$.rol").value("VENDEDOR")).andReturn();
        for (var resultado : List.of(loginNuevo, refreshNuevo)) {
            String token = json.readTree(resultado.getResponse().getContentAsString()).get("token").asText();
            assertEquals("VENDEDOR", jwt.validar(token).get("rol", String.class));
        }
        baja(cliente.getId()).andExpect(status().isNoContent());
        baja(cliente.getId()).andExpect(status().isNoContent());
        assertFalse(usuarios.findById(cliente.getId()).orElseThrow().getActivo());
        login(cliente.getCorreo()).andExpect(status().isUnauthorized());
        refresh(anterior).andExpect(status().isUnauthorized());
        mvc.perform(get(CUENTAS + "/me").header("Authorization", bearer(anterior)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminDegradadoODesactivadoPierdeAccesoConTokenAnterior() throws Exception {
        Usuario segundo = guardar("202-2", "segundo@example.test", "ADMIN");
        Usuario tercero = guardar("303-3", "tercero@example.test", "ADMIN");
        String tokenSegundo = bearer(jwt.generarToken(segundo));
        String tokenTercero = bearer(jwt.generarToken(tercero));
        editar(segundo.getId(), "{\"rol\":\"VENDEDOR\"}").andExpect(status().isOk());
        baja(tercero.getId()).andExpect(status().isNoContent());
        mvc.perform(get(CUENTAS).header("Authorization", tokenSegundo)).andExpect(status().isForbidden());
        mvc.perform(get(CUENTAS).header("Authorization", tokenTercero)).andExpect(status().isUnauthorized());
        mvc.perform(get(CUENTAS + "/me").header("Authorization", tokenSegundo))
                .andExpect(status().isOk()).andExpect(jsonPath("$.rol").value("VENDEDOR"));
    }

    @Test
    void visitanteYRolesNoAdministrativosSeRechazan() throws Exception {
        mvc.perform(get(CUENTAS)).andExpect(status().isUnauthorized());
        mvc.perform(get(CUENTAS + "/me")).andExpect(status().isUnauthorized());
        for (String rol : List.of("VENDEDOR", "CLIENTE")) {
            var cuenta = guardar("RUN-" + rol, rol.toLowerCase() + "@example.test", rol);
            String token = bearer(jwt.generarToken(cuenta));
            mvc.perform(get(CUENTAS).header("Authorization", token)).andExpect(status().isForbidden());
            mvc.perform(post(CUENTAS).header("Authorization", token).contentType("application/json").content("{}"))
                    .andExpect(status().isForbidden());
        }
    }

    @Test
    void rechazaJwtAlteradoVencidoConOtraClaveOtroEmisorOIdentidadInconsistente() throws Exception {
        SecretKey clave = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretoPruebas));
        Instant futuro = Instant.now().plusSeconds(600);
        String original = jwt.generarToken(admin);
        int firma = original.lastIndexOf('.') + 1;
        String alterado = original.substring(0, firma) + (original.charAt(firma) == 'A' ? 'B' : 'A')
                + original.substring(firma + 1);
        String inconsistente = token(clave, emisor, futuro, admin.getId(), "otra@example.test");
        for (String invalido : List.of(alterado,
                token(clave, emisor, Instant.now().minusSeconds(60), admin.getId(), admin.getCorreo()),
                token(Jwts.SIG.HS256.key().build(), emisor, futuro, admin.getId(), admin.getCorreo()),
                token(clave, "otro-emisor-pruebas", futuro, admin.getId(), admin.getCorreo()),
                inconsistente)) {
            mvc.perform(get(CUENTAS).header("Authorization", bearer(invalido))).andExpect(status().isUnauthorized());
        }
        refresh(inconsistente).andExpect(status().isUnauthorized());
    }

    @Test
    void perfilesAisladosYEdicionPropiaEstricta() throws Exception {
        Usuario ana = guardar("202-2", "ana@example.test", "CLIENTE");
        Usuario otra = guardar("303-3", "otra@example.test", "CLIENTE");
        String token = bearer(jwt.generarToken(ana));
        mvc.perform(get(CUENTAS + "/me").header("Authorization", token).param("id", otra.getId().toString()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(ana.getId()));
        mvc.perform(get(CUENTAS + "/me").header("Authorization", bearer(jwt.generarToken(otra))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(otra.getId()));
        mvc.perform(get(CUENTAS + "/{id}", otra.getId()).header("Authorization", token))
                .andExpect(status().isForbidden());
        mvc.perform(put(CUENTAS + "/me").header("Authorization", token).contentType("application/json")
                        .content("{\"nombre\":\" Ana María \",\"apellido\":null}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nombre").value("Ana María"))
                .andExpect(jsonPath("$.apellido").value("Pérez")).andExpect(jsonPath("$.rol").value("CLIENTE"));
        for (String prohibido : List.of("id", "run", "correo", "password", "rol", "activo")) {
            mvc.perform(put(CUENTAS + "/me").header("Authorization", token).contentType("application/json")
                            .content("{\"nombre\":\"No debe guardarse\",\"" + prohibido + "\":null}"))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").exists());
        }
        assertEquals("Ana María", usuarios.findById(ana.getId()).orElseThrow().getNombre());
        assertEquals("Ana", usuarios.findById(otra.getId()).orElseThrow().getNombre());
    }

    @Test
    void impideBajaYDegradacionDelUltimoAdminInclusoConDosBajasConcurrentes() throws Exception {
        baja(admin.getId()).andExpect(status().isBadRequest());
        editar(admin.getId(), "{\"rol\":\"VENDEDOR\"}").andExpect(status().isBadRequest());
        editar(admin.getId(), "{\"activo\":false}").andExpect(status().isBadRequest());
        Usuario segundo = guardar("202-2", "segundo@example.test", "ADMIN");
        var inicio = new CountDownLatch(1);
        try (var executor = Executors.newFixedThreadPool(2)) {
            var primera = executor.submit(() -> {
                inicio.await();
                return baja(admin.getId()).andReturn().getResponse().getStatus();
            });
            var segunda = executor.submit(() -> {
                inicio.await();
                return mvc.perform(delete(CUENTAS + "/{id}", segundo.getId())
                        .header("Authorization", bearer(jwt.generarToken(segundo))))
                        .andReturn().getResponse().getStatus();
            });
            inicio.countDown();
            assertEquals(List.of(204, 400), List.of(primera.get(10, TimeUnit.SECONDS),
                    segunda.get(10, TimeUnit.SECONDS)).stream().sorted().toList());
        }
        assertEquals(1, usuarios.countByRolAndActivoTrue("ADMIN"));
    }

    private Usuario guardar(String run, String correo, String rol) {
        return usuarios.saveAndFlush(new Usuario(null, run, "Ana", "Pérez", correo,
                encoder.encode(PASSWORD), rol, true));
    }

    private ResultActions altaAdmin(String run, String correo, String rol) throws Exception {
        return mvc.perform(post(CUENTAS).header("Authorization", bearerAdmin)
                .contentType("application/json").content(alta(run, correo, rol, false)));
    }

    private String alta(String run, String correo, String rol, boolean activo) throws Exception {
        return json.writeValueAsString(Map.of("run", run, "nombre", "Ana", "apellido", "Pérez",
                "correo", correo, "password", PASSWORD, "rol", rol, "activo", activo));
    }

    private ResultActions editar(Long id, String cuerpo) throws Exception {
        return mvc.perform(put(CUENTAS + "/{id}", id).header("Authorization", bearerAdmin)
                .contentType("application/json").content(cuerpo));
    }

    private ResultActions baja(Long id) throws Exception {
        return mvc.perform(delete(CUENTAS + "/{id}", id).header("Authorization", bearerAdmin));
    }

    private ResultActions login(String correo) throws Exception {
        return mvc.perform(post(AUTH + "/login").contentType("application/json")
                .content(json.writeValueAsString(Map.of("correo", correo, "password", PASSWORD))));
    }

    private ResultActions refresh(String token) throws Exception {
        return mvc.perform(post(AUTH + "/refresh").contentType("application/json")
                .content(json.writeValueAsString(Map.of("token", token))));
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }

    private String token(SecretKey clave, String iss, Instant exp, Long id, String correo) {
        return Jwts.builder().issuer(iss).subject(correo).claim("id", id).claim("rol", "ADMIN")
                .expiration(Date.from(exp)).signWith(clave).compact();
    }
}
