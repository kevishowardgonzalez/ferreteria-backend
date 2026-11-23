package cl.duoc.ferreteria_foro.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PublicacionRequest {
    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    @NotBlank(message = "El contenido es obligatorio")
    private String contenido;

    @NotBlank(message = "El nombre del autor es obligatorio")
    private String autorNombre;
}