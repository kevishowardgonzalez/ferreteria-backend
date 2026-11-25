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
    @WithMockUser(roles = "ADMIN")
    void persisteYValidaLasOperacionesDelServicio() throws Exception {
        String base = "/api/productos";
        String producto = """
                {"nombre":"Martillo QA","categoria":"Manual QA","precio":8990.50,"stock":7,"descripcion":"Prueba"}
                """;
        var creado = mvc.perform(post(base).contentType("application/json").content(producto))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.stock").value(7)).andReturn();
        long id = json.readTree(creado.getResponse().getContentAsString()).get("id").asLong();
        mvc.perform(get(base).param("filtro", "manual qa")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id));
        mvc.perform(get(base).param("filtro", "MARTILLO QA")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id));
        mvc.perform(get(base + "/" + id)).andExpect(status().isOk()).andExpect(jsonPath("$.precio").value(8990.50));
        mvc.perform(put(base + "/" + id).contentType("application/json").content(producto.replace("8990.50", "10000.25")))
                .andExpect(status().isOk());
        mvc.perform(get(base + "/" + id)).andExpect(status().isOk()).andExpect(jsonPath("$.precio").value(10000.25));
        mvc.perform(post(base).contentType("application/json").content(producto.replace("8990.50", "1.001")))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errores.precio").exists());
        mvc.perform(post(base).contentType("application/json").content(producto.replace("\"stock\":7", "\"stock\":-1")))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errores.stock").exists());
        mvc.perform(delete(base + "/" + id)).andExpect(status().isNoContent());
        mvc.perform(get(base + "/" + id)).andExpect(status().isNotFound());
        mvc.perform(put(base + "/" + id).contentType("application/json").content(producto)).andExpect(status().isNotFound());
        mvc.perform(delete(base + "/" + id)).andExpect(status().isNotFound());
    }
}
