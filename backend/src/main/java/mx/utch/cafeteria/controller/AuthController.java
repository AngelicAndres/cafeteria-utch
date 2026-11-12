package mx.utch.cafeteria.controller;

import jakarta.validation.Valid;
import mx.utch.cafeteria.dto.AuthDtos.LoginRequest;
import mx.utch.cafeteria.dto.AuthDtos.LoginResponse;
import mx.utch.cafeteria.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** POST /api/auth/login - Publico - Obtener JWT */
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }
}
