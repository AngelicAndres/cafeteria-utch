package mx.utch.cafeteria.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** DTOs de autenticacion (RF1). */
public final class AuthDtos {

    private AuthDtos() {
    }

    public record LoginRequest(
            @NotBlank(message = "El email es obligatorio") @Email(message = "Email invalido") String email,
            @NotBlank(message = "La contrasena es obligatoria") String password) {
    }

    public record LoginResponse(String token, String tipo, String nombre, String email, String rol) {
    }
}
