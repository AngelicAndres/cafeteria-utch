package mx.utch.cafeteria.dto;

import java.time.LocalDateTime;
import java.util.Map;

/** Formato uniforme de error para todas las respuestas 4xx. */
public record ErrorResponse(LocalDateTime timestamp, int status, String error, String mensaje,
                            String ruta, Map<String, String> campos) {

    public static ErrorResponse of(int status, String error, String mensaje, String ruta) {
        return new ErrorResponse(LocalDateTime.now(), status, error, mensaje, ruta, null);
    }
}
