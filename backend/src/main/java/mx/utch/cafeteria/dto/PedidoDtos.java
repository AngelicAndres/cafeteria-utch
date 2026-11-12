package mx.utch.cafeteria.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import mx.utch.cafeteria.model.DetallePedido;
import mx.utch.cafeteria.model.EstadoPedido;
import mx.utch.cafeteria.model.Pedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** DTOs de pedido (RN2, RN4, RN5, RF3, RF6, RF7). */
public final class PedidoDtos {

    private PedidoDtos() {
    }

    public record ItemPedidoRequest(
            @NotNull(message = "El productoId es obligatorio") Long productoId,
            @NotNull(message = "La cantidad es obligatoria")
            @Min(value = 1, message = "RN2: la cantidad debe ser al menos 1") Integer cantidad) {
    }

    public record PedidoRequest(
            @NotEmpty(message = "RN2: el pedido debe tener al menos un producto")
            List<@Valid ItemPedidoRequest> items) {
    }

    public record EstadoRequest(
            @NotNull(message = "El estado es obligatorio") EstadoPedido estado) {
    }

    public record DetalleResponse(Long productoId, String producto, int cantidad,
                                  BigDecimal precioUnitario, BigDecimal subtotal) {
        public static DetalleResponse from(DetallePedido d) {
            return new DetalleResponse(d.getProducto().getId(), d.getProducto().getNombre(),
                    d.getCantidad(), d.getPrecioUnitario(), d.getSubtotal());
        }
    }

    public record PedidoResponse(Long id, String cliente, LocalDateTime fecha, EstadoPedido estado,
                                 EstadoPedido siguienteEstado, BigDecimal total,
                                 List<DetalleResponse> detalles) {
        public static PedidoResponse from(Pedido p) {
            return new PedidoResponse(p.getId(), p.getCliente().getNombre(), p.getFecha(),
                    p.getEstado(), p.getEstado().siguiente(), p.getTotal(),
                    p.getDetalles().stream().map(DetalleResponse::from).toList());
        }
    }
}
