package mx.utch.cafeteria.controller;

import jakarta.validation.Valid;
import mx.utch.cafeteria.dto.PedidoDtos.EstadoRequest;
import mx.utch.cafeteria.dto.PedidoDtos.PedidoRequest;
import mx.utch.cafeteria.dto.PedidoDtos.PedidoResponse;
import mx.utch.cafeteria.model.EstadoPedido;
import mx.utch.cafeteria.service.PedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    /** POST /api/pedidos - CLIENTE - Registrar pedido y calcular total (RF3, RF4, RF5) */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponse crear(@Valid @RequestBody PedidoRequest req, Authentication auth) {
        return pedidoService.crearPedido(req, auth.getName());
    }

    /** GET /api/pedidos/mis-pedidos - CLIENTE - Ver mis pedidos y su estado (RF6) */
    @GetMapping("/mis-pedidos")
    public List<PedidoResponse> misPedidos(Authentication auth) {
        return pedidoService.misPedidos(auth.getName());
    }

    /** GET /api/pedidos/{id} - CLIENTE (solo propios) / PERSONAL - Consultar un pedido */
    @GetMapping("/{id}")
    public PedidoResponse obtener(@PathVariable Long id, Authentication auth) {
        boolean esPersonal = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_PERSONAL".equals(a.getAuthority()));
        return pedidoService.obtener(id, auth.getName(), esPersonal);
    }

    /** GET /api/pedidos?estado= - PERSONAL - Ver pedidos (todos o por estado) (RF7) */
    @GetMapping
    public List<PedidoResponse> listar(@RequestParam(required = false) EstadoPedido estado) {
        return pedidoService.listar(estado);
    }

    /** PATCH /api/pedidos/{id}/estado - PERSONAL - Avanzar estado (RF7, RN4) */
    @PatchMapping("/{id}/estado")
    public PedidoResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoRequest req) {
        return pedidoService.cambiarEstado(id, req.estado());
    }
}
