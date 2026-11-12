package mx.utch.cafeteria.model;

/**
 * Estados del pedido (Diagrama de estados).
 * RN4: RECIBIDO -> EN_PREPARACION -> LISTO -> ENTREGADO, sin saltar ni retroceder.
 * El orden de declaracion define la secuencia valida.
 */
public enum EstadoPedido {
    RECIBIDO,
    EN_PREPARACION,
    LISTO,
    ENTREGADO;

    /** Devuelve el siguiente estado valido o null si el pedido ya fue entregado. */
    public EstadoPedido siguiente() {
        return this == ENTREGADO ? null : values()[ordinal() + 1];
    }

    /** true solo si {@code nuevo} es exactamente el estado siguiente. */
    public boolean puedeCambiarA(EstadoPedido nuevo) {
        return nuevo != null && nuevo == siguiente();
    }
}
