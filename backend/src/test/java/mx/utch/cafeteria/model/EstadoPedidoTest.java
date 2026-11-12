package mx.utch.cafeteria.model;

import mx.utch.cafeteria.exception.TransicionEstadoInvalidaException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Prueba unitaria del Diagrama de estados (RN4) y del calculo del total (RN5). */
class EstadoPedidoTest {

    private Pedido nuevoPedido() {
        return new Pedido(new Usuario("Cliente", "c@utch.mx", "x", Rol.CLIENTE));
    }

    @Test
    void recorreTodosLosEstadosEnOrden() {
        Pedido p = nuevoPedido();
        assertThat(p.getEstado()).isEqualTo(EstadoPedido.RECIBIDO);
        p.cambiarEstado(EstadoPedido.EN_PREPARACION);
        p.cambiarEstado(EstadoPedido.LISTO);
        p.cambiarEstado(EstadoPedido.ENTREGADO);
        assertThat(p.getEstado()).isEqualTo(EstadoPedido.ENTREGADO);
        assertThat(p.getEstado().siguiente()).isNull();
    }

    @Test
    void noPermiteSaltarEstados() {
        Pedido p = nuevoPedido();
        assertThatThrownBy(() -> p.cambiarEstado(EstadoPedido.LISTO))
                .isInstanceOf(TransicionEstadoInvalidaException.class)
                .hasMessageContaining("RN4");
    }

    @Test
    void noPermiteRetroceder() {
        Pedido p = nuevoPedido();
        p.cambiarEstado(EstadoPedido.EN_PREPARACION);
        assertThatThrownBy(() -> p.cambiarEstado(EstadoPedido.RECIBIDO))
                .isInstanceOf(TransicionEstadoInvalidaException.class);
    }

    @Test
    void calculaTotalComoSumaDePrecioPorCantidad() {
        Pedido p = nuevoPedido();
        p.agregarDetalle(new DetallePedido(new Producto("Capuchino", new BigDecimal("35.00"), true), 2));
        p.agregarDetalle(new DetallePedido(new Producto("Burrito", new BigDecimal("40.00"), true), 1));
        assertThat(p.calcularTotal()).isEqualByComparingTo("110.00");
    }
}
