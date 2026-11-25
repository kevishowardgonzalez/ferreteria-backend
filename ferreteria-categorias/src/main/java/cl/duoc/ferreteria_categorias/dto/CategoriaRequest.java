package cl.duoc.ferreteria_categorias.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoriaRequest {
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(max = 80)
    private String nombre;

    @Size(max = 255)
    private String descripcion;

    private Boolean activo;
}
