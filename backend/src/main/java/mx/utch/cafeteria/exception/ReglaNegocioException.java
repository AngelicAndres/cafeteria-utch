package mx.utch.cafeteria.exception;

/** Violacion de una regla de negocio (RN2, RN3). Se responde con 400 Bad Request. */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
