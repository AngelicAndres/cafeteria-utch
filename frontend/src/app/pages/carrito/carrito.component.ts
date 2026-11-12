import { CurrencyPipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { mensajeError } from '../../core/api';
import { CarritoService } from '../../core/carrito.service';
import { ETIQUETA_ESTADO, Pedido } from '../../core/models';
import { PedidoService } from '../../core/pedido.service';

/** Pasos 3 y 4 del flujo: registrar pedido y mostrar el total calculado por el backend (RF3, RF4). */
@Component({
  selector: 'app-carrito',
  imports: [CurrencyPipe, RouterLink],
  templateUrl: './carrito.component.html',
})
export class CarritoComponent {
  private readonly pedidoService = inject(PedidoService);
  readonly carrito = inject(CarritoService);
  readonly etiqueta = ETIQUETA_ESTADO;

  readonly enviando = signal(false);
  readonly error = signal<string | null>(null);
  readonly confirmado = signal<Pedido | null>(null);

  cambiarCantidad(productoId: number, valor: string): void {
    this.carrito.cambiarCantidad(productoId, Number(valor));
  }

  confirmar(): void {
    const items = this.carrito.items().map((i) => ({ productoId: i.producto.id, cantidad: i.cantidad }));
    if (items.length === 0) {
      return;
    }
    this.enviando.set(true);
    this.error.set(null);
    this.pedidoService.crear(items).subscribe({
      next: (pedido) => {
        this.confirmado.set(pedido);
        this.carrito.vaciar();
        this.enviando.set(false);
      },
      error: (e) => {
        this.error.set(mensajeError(e));
        this.enviando.set(false);
      },
    });
  }

  nuevoPedido(): void {
    this.confirmado.set(null);
  }
}
