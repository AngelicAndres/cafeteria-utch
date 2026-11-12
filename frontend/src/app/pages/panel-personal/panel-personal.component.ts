import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { mensajeError } from '../../core/api';
import { ESTADOS, ETIQUETA_ESTADO, EstadoPedido, Pedido } from '../../core/models';
import { PedidoService } from '../../core/pedido.service';

/** Paso 5 del flujo: el personal recibe pedidos y actualiza su estado (RF7, RN4). */
@Component({
  selector: 'app-panel-personal',
  imports: [CurrencyPipe, DatePipe],
  templateUrl: './panel-personal.component.html',
})
export class PanelPersonalComponent implements OnInit {
  private readonly pedidoService = inject(PedidoService);

  readonly estados = ESTADOS;
  readonly etiqueta = ETIQUETA_ESTADO;
  readonly filtro = signal<EstadoPedido | null>(null);
  readonly pedidos = signal<Pedido[]>([]);
  readonly cargando = signal(false);
  readonly procesando = signal<number | null>(null);
  readonly error = signal<string | null>(null);
  readonly aviso = signal<string | null>(null);

  ngOnInit(): void {
    this.cargar();
  }

  filtrar(estado: EstadoPedido | null): void {
    this.filtro.set(estado);
    this.cargar();
  }

  cargar(): void {
    this.cargando.set(true);
    this.error.set(null);
    this.pedidoService.listar(this.filtro() ?? undefined).subscribe({
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

  /** Avanza al siguiente estado que indica el backend (siguienteEstado). */
  avanzar(p: Pedido): void {
    if (!p.siguienteEstado) {
      return;
    }
    this.procesando.set(p.id);
    this.aviso.set(null);
    this.error.set(null);
    this.pedidoService.cambiarEstado(p.id, p.siguienteEstado).subscribe({
      next: (actualizado) => {
        this.aviso.set(`Pedido #${actualizado.id} → ${this.etiqueta[actualizado.estado]}`);
        this.procesando.set(null);
        this.cargar();
      },
      error: (e) => {
        this.error.set(mensajeError(e));
        this.procesando.set(null);
      },
    });
  }
}
