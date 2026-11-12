import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from './api';
import { Producto, ProductoRequest } from './models';

/** Consume ProductoController (/api/productos). */
@Injectable({ providedIn: 'root' })
export class ProductoService {
  private readonly http = inject(HttpClient);
  private readonly url = `${API_URL}/productos`;

  listar(): Observable<Producto[]> {
    return this.http.get<Producto[]>(this.url);
  }

  crear(req: ProductoRequest): Observable<Producto> {
    return this.http.post<Producto>(this.url, req);
  }

  actualizar(id: number, req: ProductoRequest): Observable<Producto> {
    return this.http.put<Producto>(`${this.url}/${id}`, req);
  }

  cambiarDisponibilidad(id: number, disponible: boolean): Observable<Producto> {
    return this.http.patch<Producto>(`${this.url}/${id}/disponibilidad`, { disponible });
  }
}
