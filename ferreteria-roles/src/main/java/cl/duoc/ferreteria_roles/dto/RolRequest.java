package cl.duoc.ferreteria_roles.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RolRequest {
    @NotBlank(message = "El nombre del rol es obligatorio")
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    private Boolean activo;
}