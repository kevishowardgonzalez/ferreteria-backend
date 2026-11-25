package cl.duoc.ferreteria_inventario;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class InventarioHttpTests {
    @Autowired MockMvc mvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    void persisteYValidaLasOperacionesDelServicio() throws Exception {
        String base = "/api/v1/inventario";
        String item = """
                {"productoId":78901,"productoSku":" QA-78901 ","productoNombre":"Producto QA","stockActual":5}
                """;
        mvc.perform(post(base).contentType("application/json").content(item))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.productoSku").value("QA-78901"));
        mvc.perform(post(base).contentType("application/json").content(item)).andExpect(status().isBadRequest());
        mvc.perform(get(base)).andExpect(status().isOk());
        String detalle = base + "/producto/78901";
        mvc.perform(get(detalle)).andExpect(status().isOk()).andExpect(jsonPath("$.stockActual").value(5));
        String movimiento = """
                {"productoId":78901,"tipo":"ENTRADA","cantidad":3,"motivo":"Prueba"}
                """;
        mvc.perform(post(base + "/movimientos").contentType("application/json").content(movimiento))
                .andExpect(status().isOk()).andExpect(jsonPath("$.stockActual").value(8));
        mvc.perform(post(base + "/movimientos").contentType("application/json").content(movimiento.replace("ENTRADA", "SALIDA")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.stockActual").value(5));
        mvc.perform(post(base + "/movimientos").contentType("application/json").content(movimiento.replace("ENTRADA", "AJUSTE")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.stockActual").value(3));
        mvc.perform(post(base + "/movimientos").contentType("application/json")
                .content(movimiento.replace("ENTRADA", "SALIDA").replace("\"cantidad\":3", "\"cantidad\":4")))
                .andExpect(status().isBadRequest());
        mvc.perform(post(base + "/movimientos").contentType("application/json")
                .content(movimiento.replace("\"cantidad\":3", "\"cantidad\":0"))).andExpect(status().isBadRequest());
        mvc.perform(post(base + "/movimientos").contentType("application/json")
                .content(movimiento.replace("78901", "78902"))).andExpect(status().isNotFound());
        mvc.perform(get(detalle + "/movimientos")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
        mvc.perform(get(detalle)).andExpect(status().isOk()).andExpect(jsonPath("$.stockActual").value(3));
        mvc.perform(get(base + "/producto/78902")).andExpect(status().isNotFound());
    }
}
