package mx.utch.cafeteria.exception;

/** Recurso inexistente. Se responde con 404 Not Found. */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
