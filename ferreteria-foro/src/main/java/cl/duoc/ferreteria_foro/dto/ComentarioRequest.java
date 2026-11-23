package cl.duoc.ferreteria_foro.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ComentarioRequest {
    @NotBlank(message = "El comentario es obligatorio")
    private String contenido;

    @NotBlank(message = "El nombre del autor es obligatorio")
    private String autorNombre;
}