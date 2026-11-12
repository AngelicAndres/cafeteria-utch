import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from './api';
import { EstadoPedido, ItemPedido, Pedido } from './models';

/** Consume PedidoController (/api/pedidos). */
@Injectable({ providedIn: 'root' })
export class PedidoService {
  private readonly http = inject(HttpClient);
  private readonly url = `${API_URL}/pedidos`;

  crear(items: ItemPedido[]): Observable<Pedido> {
    return this.http.post<Pedido>(this.url, { items });
  }

  misPedidos(): Observable<Pedido[]> {
    return this.http.get<Pedido[]>(`${this.url}/mis-pedidos`);
  }

  obtener(id: number): Observable<Pedido> {
    return this.http.get<Pedido>(`${this.url}/${id}`);
  }

  listar(estado?: EstadoPedido): Observable<Pedido[]> {
    const params = estado ? new HttpParams().set('estado', estado) : undefined;
    return this.http.get<Pedido[]>(this.url, { params });
  }

  cambiarEstado(id: number, estado: EstadoPedido): Observable<Pedido> {
    return this.http.patch<Pedido>(`${this.url}/${id}/estado`, { estado });
  }
}
