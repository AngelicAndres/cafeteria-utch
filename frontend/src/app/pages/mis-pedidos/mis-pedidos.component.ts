import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { mensajeError } from '../../core/api';
import { ESTADOS, ETIQUETA_ESTADO, EstadoPedido, Pedido } from '../../core/models';
import { PedidoService } from '../../core/pedido.service';

/** Paso 6 del flujo: el cliente consulta el estado de sus pedidos (RF6). */
@Component({
  selector: 'app-mis-pedidos',
  imports: [CurrencyPipe, DatePipe],
  templateUrl: './mis-pedidos.component.html',
})
export class MisPedidosComponent implements OnInit {
  private readonly pedidoService = inject(PedidoService);

  readonly estados = ESTADOS;
  readonly etiqueta = ETIQUETA_ESTADO;
  readonly pedidos = signal<Pedido[]>([]);
  readonly cargando = signal(false);
  readonly error = signal<string | null>(null);

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.cargando.set(true);
    this.error.set(null);
    this.pedidoService.misPedidos().subscribe({
      next: (lista) => {
        this.pedidos.set(lista);
        this.cargando.set(false);
      },
      error: (e) => {
        this.error.set(mensajeError(e));
        this.cargando.set(false);
      },
    });
  }

  /** true si el paso ya se alcanzó (para la barra de progreso). */
  alcanzado(actual: EstadoPedido, paso: EstadoPedido): boolean {
    return ESTADOS.indexOf(paso) <= ESTADOS.indexOf(actual);
  }
}
