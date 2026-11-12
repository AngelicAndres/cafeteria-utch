package mx.utch.cafeteria.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import mx.utch.cafeteria.model.Producto;

import java.math.BigDecimal;

/** DTOs de producto (RN1, RF2, RF8). */
public final class ProductoDtos {

    private ProductoDtos() {
    }

    public record ProductoRequest(
            @NotBlank(message = "El nombre es obligatorio")
            @Size(max = 100, message = "El nombre admite maximo 100 caracteres") String nombre,
            @NotNull(message = "El precio es obligatorio")
            @DecimalMin(value = "0.01", message = "RN1: el precio debe ser mayor a 0") BigDecimal precio,
            @NotNull(message = "La disponibilidad es obligatoria") Boolean disponible) {
    }

    public record DisponibilidadRequest(
            @NotNull(message = "La disponibilidad es obligatoria") Boolean disponible) {
    }

    public record ProductoResponse(Long id, String nombre, BigDecimal precio, boolean disponible) {
        public static ProductoResponse from(Producto p) {
            return new ProductoResponse(p.getId(), p.getNombre(), p.getPrecio(), p.isDisponible());
        }
    }
}
