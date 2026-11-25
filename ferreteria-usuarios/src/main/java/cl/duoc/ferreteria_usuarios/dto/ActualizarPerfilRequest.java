package cl.duoc.ferreteria_usuarios.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Data;

@Data
public class ActualizarPerfilRequest {

    private String nombre;
    private String apellido;

    @JsonAnySetter
    public void rechazarCampoDesconocido(
            String campo, Object valor) {

        throw new IllegalArgumentException(
                "Campo no permitido en la edición de perfil");
    }
}