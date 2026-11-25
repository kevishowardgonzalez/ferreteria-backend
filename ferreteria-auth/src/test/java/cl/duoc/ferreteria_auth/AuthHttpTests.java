package cl.duoc.ferreteria_auth;

import cl.duoc.ferreteria_auth.repository.UsuarioRepository;
import cl.duoc.ferreteria_auth.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthHttpTests {
    private static final String BASE = "/api/v1/auth";
    private static final String CORREO = "consolidacion-seguridad@example.test";
    private static final String PASSWORD = "prueba123";

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired UsuarioRepository usuarios;
    @Autowired PasswordEncoder encoder;
    @Autowired JwtService jwt;

    @Test
    void registraClienteValidaCredencialesYConservaContratoDeRefresh() throws Exception {
        mvc.perform(post(BASE + "/register").contentType("application/json").content(json.writeValueAsString(Map.of(
                        "run", "11222333-4", "nombre", "Ana", "apellido", "Prueba",
                        "correo", CORREO, "password", PASSWORD))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rol").value("CLIENTE"))
                .andExpect(jsonPath("$.password").doesNotExist());

        var usuario = usuarios.findByCorreoIgnoreCase(CORREO).orElseThrow();
        assertNotEquals(PASSWORD, usuario.getPassword());
        assertTrue(encoder.matches(PASSWORD, usuario.getPassword()));

        mvc.perform(post(BASE + "/login").contentType("application/json").content(json.writeValueAsString(
                        Map.of("correo", CORREO, "password", "incorrecta"))))
                .andExpect(status().isUnauthorized());

        String credenciales = json.writeValueAsString(Map.of("correo", CORREO, "password", PASSWORD));
        var login = mvc.perform(post(BASE + "/login").contentType("application/json").content(credenciales))
                .andExpect(status().isOk()).andReturn();
        String token = json.readTree(login.getResponse().getContentAsString()).get("token").asText();
        assertEquals(CORREO, jwt.validar(token).getSubject());
        assertEquals("CLIENTE", jwt.validar(token).get("rol", String.class));
        String refresh = json.writeValueAsString(Map.of("token", token));

        mvc.perform(post(BASE + "/refresh").contentType("application/json").content(refresh))
                .andExpect(status().isOk()).andExpect(jsonPath("$.token").isNotEmpty());
        mvc.perform(post(BASE + "/refresh").contentType("application/json").content("{\"token\":\"invalido\"}"))
                .andExpect(status().isUnauthorized());

        usuario.setActivo(false);
        usuarios.save(usuario);
        mvc.perform(post(BASE + "/login").contentType("application/json").content(credenciales))
                .andExpect(status().isUnauthorized());
        mvc.perform(post(BASE + "/refresh").contentType("application/json").content(refresh))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void permitePreflightDeLoginEnElOrigenConfigurado() throws Exception {
        mvc.perform(options(BASE + "/login")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }
}
