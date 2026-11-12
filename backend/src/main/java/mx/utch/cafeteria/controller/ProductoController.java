package mx.utch.cafeteria.controller;

import jakarta.validation.Valid;
import mx.utch.cafeteria.dto.ProductoDtos.DisponibilidadRequest;
import mx.utch.cafeteria.dto.ProductoDtos.ProductoRequest;
import mx.utch.cafeteria.dto.ProductoDtos.ProductoResponse;
import mx.utch.cafeteria.service.ProductoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    /** GET /api/productos - Autenticados - Consultar menu (RF2) */
    @GetMapping
    public List<ProductoResponse> listar() {
        return productoService.listar();
    }

    /** GET /api/productos/{id} - Autenticados */
    @GetMapping("/{id}")
    public ProductoResponse obtener(@PathVariable Long id) {
        return productoService.obtener(id);
    }

    /** POST /api/productos - ADMIN - Crear producto (RF8) */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoResponse crear(@Valid @RequestBody ProductoRequest req) {
        return productoService.crear(req);
    }

    /** PUT /api/productos/{id} - ADMIN - Editar producto (RF8) */
    @PutMapping("/{id}")
    public ProductoResponse actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest req) {
        return productoService.actualizar(id, req);
    }

    /** PATCH /api/productos/{id}/disponibilidad - ADMIN - Cambiar disponibilidad (RF8) */
    @PatchMapping("/{id}/disponibilidad")
    public ProductoResponse cambiarDisponibilidad(@PathVariable Long id,
                                                  @Valid @RequestBody DisponibilidadRequest req) {
        return productoService.cambiarDisponibilidad(id, req.disponible());
    }
}
