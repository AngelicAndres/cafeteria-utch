package mx.utch.cafeteria.service;

import mx.utch.cafeteria.dto.AuthDtos.LoginRequest;
import mx.utch.cafeteria.dto.AuthDtos.LoginResponse;
import mx.utch.cafeteria.model.Usuario;
import mx.utch.cafeteria.repository.UsuarioRepository;
import mx.utch.cafeteria.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

/** RF1: iniciar sesion y distinguir roles. */
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager, UsuarioRepository usuarioRepository,
                       JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest req) {
        // Lanza BadCredentialsException (-> 401) si el email o la contrasena no coinciden
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(req.email(), req.password()));
        Usuario u = usuarioRepository.findByEmail(req.email())
                .orElseThrow(() -> new BadCredentialsException("Credenciales invalidas"));
        return new LoginResponse(jwtService.generarToken(u), "Bearer", u.getNombre(), u.getEmail(),
                u.getRol().name());
    }
}
