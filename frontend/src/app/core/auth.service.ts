import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { API_URL } from './api';
import { LoginResponse, Rol } from './models';

const CLAVE_SESION = 'cafeteria.sesion';

/** RF1: inicio de sesión y manejo del JWT en el cliente. */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  readonly sesion = signal<LoginResponse | null>(this.leerSesion());
  readonly rol = computed<Rol | null>(() => this.sesion()?.rol ?? null);

  login(email: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${API_URL}/auth/login`, { email, password }).pipe(
      tap((s) => {
        localStorage.setItem(CLAVE_SESION, JSON.stringify(s));
        this.sesion.set(s);
      }),
    );
  }

  logout(): void {
    localStorage.removeItem(CLAVE_SESION);
    this.sesion.set(null);
    this.router.navigate(['/login']);
  }

  token(): string | null {
    return this.sesion()?.token ?? null;
  }

  /** Pantalla inicial de cada rol. */
  rutaInicio(rol?: Rol | null): string {
    switch (rol ?? this.rol()) {
      case 'CLIENTE':
        return '/menu';
      case 'PERSONAL':
        return '/personal';
      case 'ADMIN':
        return '/admin';
      default:
        return '/login';
    }
  }

  private leerSesion(): LoginResponse | null {
    try {
      const raw = localStorage.getItem(CLAVE_SESION);
      if (!raw) {
        return null;
      }

      const parsed = JSON.parse(raw) as Partial<LoginResponse>;
      const esSesionValida = parsed?.token && parsed?.rol && parsed?.email && parsed?.nombre;

      if (!esSesionValida) {
        localStorage.removeItem(CLAVE_SESION);
        return null;
      }

      return parsed as LoginResponse;
    } catch {
      localStorage.removeItem(CLAVE_SESION);
      return null;
    }
  }
}
