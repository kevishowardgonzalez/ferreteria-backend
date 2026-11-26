package cl.duoc.ferreteria_inventario.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class InventarioRequest {
    @NotNull(message = "El ID de producto es obligatorio")
    @Positive
    private Long productoId;

    @NotBlank(message = "El SKU es obligatorio")
    @Size(max = 40)
    @Pattern(regexp = "\\s*[A-Za-z0-9][A-Za-z0-9_-]*\\s*", message = "SKU inválido")
    private String productoSku;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 120)
    private String productoNombre;

    @NotNull(message = "El stock inicial es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stockActual;

    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimo = 5;
}
