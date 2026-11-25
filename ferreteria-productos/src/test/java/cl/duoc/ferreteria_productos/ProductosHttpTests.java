package cl.duoc.ferreteria_productos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductosHttpTests {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    @Test
    void visitantePuedeLeerPeroNecesitaAutenticacionParaEscribir() throws Exception {
        mvc.perform(get("/api/productos")).andExpect(status().isOk());
        mvc.perform(post("/api/productos").contentType("application/json").content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "VENDEDOR")
    void vendedorPuedeConsultarPeroNoEscribir() throws Exception {
        mvc.perform(get("/api/productos")).andExpect(status().isOk());
        mvc.perform(post("/api/productos").contentType("application/json").content("{}"))
                .andExpect(status().isForbidden());
        mvc.perform(put("/api/productos/1").contentType("application/json").content("{}"))
                .andExpect(status().isForbidden());
        mvc.perform(delete("/api/productos/1")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void clientePuedeConsultarPeroNoEscribir() throws Exception {
        mvc.perform(get("/api/productos")).andExpect(status().isOk());
        mvc.perform(post("/api/productos").contentType("application/json").content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void persisteYValidaLasOperacionesDelServicio() throws Exception {
        String base = "/api/productos";
        String producto = """
                {"nombre":"Martillo QA","categoria":"Manual QA","precio":8990.50,"stock":7,"descripcion":"Prueba"}
                """;
        var creado = mvc.perform(post(base).contentType("application/json").content(producto))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.stock").value(0)).andReturn();
        long id = json.readTree(creado.getResponse().getContentAsString()).get("id").asLong();
        mvc.perform(get(base).param("filtro", "manual qa")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id));
        mvc.perform(get(base).param("filtro", "MARTILLO QA")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id));
        mvc.perform(get(base + "/" + id)).andExpect(status().isOk()).andExpect(jsonPath("$.precio").value(8990.50));
        mvc.perform(put(base + "/" + id).contentType("application/json").content(producto.replace("8990.50", "10000.25")))
                .andExpect(status().isOk());
        mvc.perform(get(base + "/" + id)).andExpect(status().isOk()).andExpect(jsonPath("$.precio").value(10000.25))
                .andExpect(jsonPath("$.stock").value(0));
        mvc.perform(post(base).contentType("application/json").content(producto.replace("8990.50", "1.001")))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errores.precio").exists());
        mvc.perform(post(base).contentType("application/json").content(producto.replace("\"stock\":7", "\"stock\":-1")))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errores.stock").exists());
        mvc.perform(delete(base + "/" + id)).andExpect(status().isNoContent());
        mvc.perform(get(base + "/" + id)).andExpect(status().isOk()).andExpect(jsonPath("$.retirado").value(true));
        mvc.perform(get(base).param("filtro", "MARTILLO QA")).andExpect(status().isOk()).andExpect(jsonPath("$").isEmpty());
        mvc.perform(put(base + "/" + id).contentType("application/json").content(producto)).andExpect(status().isConflict());
        mvc.perform(delete(base + "/" + id)).andExpect(status().isNoContent());
    }
}
