package mx.utch.cafeteria.service;

import mx.utch.cafeteria.dto.PedidoDtos.ItemPedidoRequest;
import mx.utch.cafeteria.dto.PedidoDtos.PedidoRequest;
import mx.utch.cafeteria.dto.PedidoDtos.PedidoResponse;
import mx.utch.cafeteria.exception.RecursoNoEncontradoException;
import mx.utch.cafeteria.exception.ReglaNegocioException;
import mx.utch.cafeteria.model.*;
import mx.utch.cafeteria.repository.PedidoRepository;
import mx.utch.cafeteria.repository.ProductoRepository;
import mx.utch.cafeteria.repository.UsuarioRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Logica de negocio de pedidos. Cada metodo corresponde a un mensaje del
 * Diagrama de secuencia "Registrar pedido" o a una transicion del Diagrama de estados.
 */
@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    public PedidoService(PedidoRepository pedidoRepository, ProductoRepository productoRepository,
                         UsuarioRepository usuarioRepository) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /** RF3, RF4, RF5 - aplica RN2, RN3 y RN5. Transaccional: o se guarda todo o nada (RNF2). */
    @Transactional
    public PedidoResponse crearPedido(PedidoRequest req, String emailCliente) {
        if (req.items() == null || req.items().isEmpty()) {
            throw new ReglaNegocioException("RN2: el pedido debe tener al menos un producto");
        }
        Usuario cliente = usuarioRepository.findByEmail(emailCliente)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado"));

        Pedido pedido = new Pedido(cliente);                        // estado inicial RECIBIDO
        for (ItemPedidoRequest item : req.items()) {
            if (item.cantidad() == null || item.cantidad() < 1) {   // RN2
                throw new ReglaNegocioException("RN2: la cantidad debe ser al menos 1");
            }
            Producto producto = productoRepository.findById(item.productoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Producto " + item.productoId() + " no encontrado"));
            if (!producto.isDisponible()) {                          // RN3
                throw new ReglaNegocioException(
                        "RN3: el producto '" + producto.getNombre() + "' no esta disponible");
            }
            pedido.agregarDetalle(new DetallePedido(producto, item.cantidad()));
        }
        pedido.calcularTotal();                                      // RN5
        return PedidoResponse.from(pedidoRepository.save(pedido));
    }

    /** RF6: el cliente consulta sus pedidos y su estado. */
    @Transactional(readOnly = true)
    public List<PedidoResponse> misPedidos(String emailCliente) {
        return pedidoRepository.findByClienteEmailOrderByFechaDesc(emailCliente).stream()
                .map(PedidoResponse::from)
                .toList();
    }

    /** Consulta de un pedido. Un CLIENTE solo puede ver los suyos; PERSONAL ve cualquiera. */
    @Transactional(readOnly = true)
    public PedidoResponse obtener(Long id, String email, boolean esPersonal) {
        Pedido pedido = buscar(id);
        if (!esPersonal && !pedido.getCliente().getEmail().equals(email)) {
            throw new AccessDeniedException("No puedes consultar pedidos de otro cliente");
        }
        return PedidoResponse.from(pedido);
    }

    /** RF7: el personal lista pedidos, opcionalmente filtrados por estado. */
    @Transactional(readOnly = true)
    public List<PedidoResponse> listar(EstadoPedido estado) {
        List<Pedido> pedidos = (estado == null)
                ? pedidoRepository.findAllByOrderByFechaDesc()
                : pedidoRepository.findByEstadoOrderByFechaAsc(estado);
        return pedidos.stream().map(PedidoResponse::from).toList();
    }

    /** RF7 + RN4: el personal avanza el estado; saltos o retrocesos -> 409. */
    @Transactional
    public PedidoResponse cambiarEstado(Long id, EstadoPedido nuevo) {
        Pedido pedido = buscar(id);
        pedido.cambiarEstado(nuevo);
        return PedidoResponse.from(pedido);
    }

    private Pedido buscar(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido " + id + " no encontrado"));
    }
}
