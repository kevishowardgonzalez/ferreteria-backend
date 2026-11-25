package cl.duoc.ferreteria_auth.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;

public record ActualizarPerfilRequest(String nombre, String apellido) {
    @JsonAnySetter
    public void rechazarCampoDesconocido(String campo, Object valor) {
        throw new IllegalArgumentException("Campo no permitido en la edición de perfil");
    }
}
