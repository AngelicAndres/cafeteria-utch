import { HttpErrorResponse } from '@angular/common/http';

/** URL base del backend Spring Boot. */
export const API_URL = 'http://localhost:8080/api';

/** Convierte un error HTTP en un mensaje legible (usa el campo "mensaje" del ErrorResponse del backend). */
export function mensajeError(err: unknown): string {
  if (err instanceof HttpErrorResponse) {
    if (err.status === 0) {
      return 'No hay conexión con el servidor. ¿Está corriendo el backend en el puerto 8080?';
    }
    return err.error?.mensaje ?? `Error ${err.status}`;
  }
  return 'Ocurrió un error inesperado';
}
