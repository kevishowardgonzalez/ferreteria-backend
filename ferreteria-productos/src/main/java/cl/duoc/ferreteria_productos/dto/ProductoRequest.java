package cl.duoc.ferreteria_productos.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProductoRequest {
    @NotBlank(message = "El SKU es obligatorio")
    private String sku;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;

    private String marca;

    @NotNull(message = "El precio es obligatorio")
    @Min(value = 1, message = "El precio debe ser mayor a 0")
    private Integer precio;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @NotNull(message = "La categoría es obligatoria")
    private Long categoriaId;

    private String imagenUrl;

    private Boolean activo;
}