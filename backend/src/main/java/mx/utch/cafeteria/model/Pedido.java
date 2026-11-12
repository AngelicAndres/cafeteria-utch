package mx.utch.cafeteria.model;

import jakarta.persistence.*;
import mx.utch.cafeteria.exception.TransicionEstadoInvalidaException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id")
    private Usuario cliente;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPedido estado;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    /** Composicion 1..* (Diagrama de clases): los detalles viven y mueren con el pedido. */
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedido> detalles = new ArrayList<>();

    protected Pedido() {
    }

    /** Todo pedido nace en estado RECIBIDO (Diagrama de estados). */
    public Pedido(Usuario cliente) {
        this.cliente = cliente;
        this.fecha = LocalDateTime.now();
        this.estado = EstadoPedido.RECIBIDO;
    }

    public void agregarDetalle(DetallePedido detalle) {
        detalle.setPedido(this);
        detalles.add(detalle);
    }

    /** RN5: total = suma de (precio x cantidad) de cada detalle. */
    public BigDecimal calcularTotal() {
        this.total = detalles.stream()
                .map(DetallePedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return this.total;
    }

    /** RN4: solo se permite avanzar al estado inmediato siguiente. */
    public void cambiarEstado(EstadoPedido nuevo) {
        if (!estado.puedeCambiarA(nuevo)) {
            EstadoPedido esperado = estado.siguiente();
            String detalle = (esperado == null)
                    ? "el pedido ya fue entregado y no admite mas cambios"
                    : "el siguiente estado valido es " + esperado;
            throw new TransicionEstadoInvalidaException(
                    "RN4: no se puede pasar de " + estado + " a " + nuevo + "; " + detalle);
        }
        this.estado = nuevo;
    }

    public Long getId() { return id; }
    public Usuario getCliente() { return cliente; }
    public LocalDateTime getFecha() { return fecha; }
    public EstadoPedido getEstado() { return estado; }
    public BigDecimal getTotal() { return total; }
    public List<DetallePedido> getDetalles() { return detalles; }
}
