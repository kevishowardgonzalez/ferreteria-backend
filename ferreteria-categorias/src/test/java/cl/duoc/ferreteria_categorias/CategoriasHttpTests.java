package cl.duoc.ferreteria_categorias;

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
class CategoriasHttpTests {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    @Test
    void visitantePuedeConsultarCategoriasActivasPeroNoElListadoInterno() throws Exception {
        mvc.perform(get("/api/v1/categorias/activas")).andExpect(status().isOk());
        mvc.perform(get("/api/v1/categorias")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/categorias/1")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/v1/categorias").contentType("application/json").content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "VENDEDOR")
    void vendedorPuedeConsultarElListadoPeroNoEscribir() throws Exception {
        mvc.perform(get("/api/v1/categorias")).andExpect(status().isOk());
        mvc.perform(post("/api/v1/categorias").contentType("application/json").content("{}"))
                .andExpect(status().isForbidden());
        mvc.perform(put("/api/v1/categorias/1").contentType("application/json").content("{}"))
                .andExpect(status().isForbidden());
        mvc.perform(delete("/api/v1/categorias/1")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void clienteSoloPuedeConsultarElCatalogoPublico() throws Exception {
        mvc.perform(get("/api/v1/categorias/activas")).andExpect(status().isOk());
        mvc.perform(get("/api/v1/categorias")).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/categorias/1")).andExpect(status().isForbidden());
        mvc.perform(post("/api/v1/categorias").contentType("application/json").content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void persisteYValidaLasOperacionesDelServicio() throws Exception {
        String base = "/api/v1/categorias";
        String categoria = """
                {"nombre":"Categoria QA","descripcion":"Prueba","activo":true}
                """;
        var creada = mvc.perform(post(base).contentType("application/json").content(categoria))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.activo").value(true)).andReturn();
        long id = json.readTree(creada.getResponse().getContentAsString()).get("id").asLong();
        mvc.perform(get(base)).andExpect(status().isOk());
        mvc.perform(get(base + "/" + id)).andExpect(status().isOk()).andExpect(jsonPath("$.nombre").value("Categoria QA"));
        mvc.perform(get(base + "/activas")).andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + id + ")]").isNotEmpty());
        mvc.perform(post(base).contentType("application/json").content(categoria.replace("Categoria QA", " categoria qa ")))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.mensaje").exists());
        String segunda = categoria.replace("Categoria QA", "Otra QA");
        mvc.perform(post(base).contentType("application/json").content(segunda)).andExpect(status().isCreated());
        mvc.perform(put(base + "/" + id).contentType("application/json").content(segunda.replace("Otra QA", "otra qa")))
                .andExpect(status().isBadRequest());
        mvc.perform(put(base + "/" + id).contentType("application/json").content(categoria.replace("Prueba", "Actualizada")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.descripcion").value("Actualizada"));
        mvc.perform(delete(base + "/" + id)).andExpect(status().isNoContent());
        mvc.perform(get(base + "/" + id)).andExpect(status().isOk()).andExpect(jsonPath("$.activo").value(false));
        mvc.perform(get(base + "/activas")).andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + id + ")]").isEmpty());
        mvc.perform(get(base + "/999999")).andExpect(status().isNotFound());
        mvc.perform(delete(base + "/999999")).andExpect(status().isNotFound());
        mvc.perform(post(base).contentType("application/json").content("{\"nombre\":\" \"}")).andExpect(status().isBadRequest());
    }
}
