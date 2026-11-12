package mx.utch.cafeteria.exception;

/** Cambio de estado que salta o retrocede (RN4). Se responde con 409 Conflict. */
public class TransicionEstadoInvalidaException extends RuntimeException {

    public TransicionEstadoInvalidaException(String mensaje) {
        super(mensaje);
    }
}
