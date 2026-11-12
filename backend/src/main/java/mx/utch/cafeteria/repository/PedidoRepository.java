package mx.utch.cafeteria.repository;

import mx.utch.cafeteria.model.EstadoPedido;
import mx.utch.cafeteria.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByClienteEmailOrderByFechaDesc(String email);

    List<Pedido> findByEstadoOrderByFechaAsc(EstadoPedido estado);

    List<Pedido> findAllByOrderByFechaDesc();
}
