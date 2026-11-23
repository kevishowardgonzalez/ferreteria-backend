package cl.duoc.ferreteria_carrito.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CarritoItemRequest {
    @NotNull(message = "El ID de producto es obligatorio")
    private Long productoId;

    @NotBlank(message = "El nombre del producto es obligatorio")
    private String productoNombre;

    @NotNull(message = "El precio unitario es obligatorio")
    @Min(value = 1, message = "El precio debe ser mayor a 0")
    private Integer precioUnitario;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad mínima es 1")
    private Integer cantidad;

    private String imagenUrl;
}