package cl.duoc.ferreteria_inventario.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class MovimientoRequest {
    @NotNull(message = "El ID de producto es obligatorio")
    private Long productoId;

    @NotBlank(message = "El tipo de movimiento es obligatorio (ENTRADA, SALIDA, AJUSTE)")
    private String tipo;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;

    private String motivo;
}
