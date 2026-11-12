package mx.utch.cafeteria.service;

import mx.utch.cafeteria.dto.ProductoDtos.ProductoRequest;
import mx.utch.cafeteria.dto.ProductoDtos.ProductoResponse;
import mx.utch.cafeteria.exception.RecursoNoEncontradoException;
import mx.utch.cafeteria.model.Producto;
import mx.utch.cafeteria.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** RF2 (consultar menu) y RF8 (gestion de productos por el administrador). */
@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listar() {
        return productoRepository.findAllByOrderByNombreAsc().stream()
                .map(ProductoResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductoResponse obtener(Long id) {
        return ProductoResponse.from(buscar(id));
    }

    @Transactional
    public ProductoResponse crear(ProductoRequest req) {
        Producto p = new Producto(req.nombre().trim(), req.precio(), req.disponible());
        return ProductoResponse.from(productoRepository.save(p));
    }

    @Transactional
    public ProductoResponse actualizar(Long id, ProductoRequest req) {
        Producto p = buscar(id);
        p.actualizar(req.nombre().trim(), req.precio(), req.disponible());
        return ProductoResponse.from(p);
    }

    @Transactional
    public ProductoResponse cambiarDisponibilidad(Long id, boolean disponible) {
        Producto p = buscar(id);
        p.setDisponible(disponible);
        return ProductoResponse.from(p);
    }

    private Producto buscar(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto " + id + " no encontrado"));
    }
}
