import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { Rol } from './models';

/**
 * Guard por rol: solo deja entrar a la ruta si el usuario tiene alguno de los roles indicados.
 * Es una protección de interfaz; la autorización real la hace Spring Security en el backend.
 */
export function roleGuard(...roles: Rol[]): CanActivateFn {
  return () => {
    const auth = inject(AuthService);
    const router = inject(Router);
    const rol = auth.rol();
    if (rol && roles.includes(rol)) {
      return true;
    }
    return router.createUrlTree([rol ? auth.rutaInicio(rol) : '/login']);
  };
}
