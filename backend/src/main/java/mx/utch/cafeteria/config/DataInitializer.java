package mx.utch.cafeteria.config;

import mx.utch.cafeteria.model.Producto;
import mx.utch.cafeteria.model.Rol;
import mx.utch.cafeteria.model.Usuario;
import mx.utch.cafeteria.repository.ProductoRepository;
import mx.utch.cafeteria.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/** Datos semilla: se insertan solo si las tablas estan vacias. */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository, ProductoRepository productoRepository,
                           PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            usuarioRepository.saveAll(List.of(
                    new Usuario("Cliente Demo", "cliente@utch.mx", passwordEncoder.encode("cliente123"), Rol.CLIENTE),
                    new Usuario("Cliente Dos", "cliente2@utch.mx", passwordEncoder.encode("cliente123"), Rol.CLIENTE),
                    new Usuario("Personal Cafeteria", "personal@utch.mx", passwordEncoder.encode("personal123"), Rol.PERSONAL),
                    new Usuario("Administrador", "admin@utch.mx", passwordEncoder.encode("admin123"), Rol.ADMIN)));
            log.info("Usuarios semilla creados (cliente@utch.mx, cliente2@utch.mx, personal@utch.mx, admin@utch.mx)");
        }
        if (productoRepository.count() == 0) {
            productoRepository.saveAll(List.of(
                    new Producto("Cafe americano", new BigDecimal("25.00"), true),
                    new Producto("Capuchino", new BigDecimal("35.00"), true),
                    new Producto("Latte", new BigDecimal("38.00"), true),
                    new Producto("Burrito de deshebrada", new BigDecimal("40.00"), true),
                    new Producto("Sandwich de jamon", new BigDecimal("45.00"), true),
                    new Producto("Chilaquiles verdes", new BigDecimal("55.00"), true),
                    new Producto("Galleta de avena", new BigDecimal("15.00"), true),
                    new Producto("Jugo de naranja", new BigDecimal("28.00"), false)));
            log.info("Productos semilla creados (Jugo de naranja como NO disponible para probar RN3)");
        }
    }
}
