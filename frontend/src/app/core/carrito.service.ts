import { Injectable, computed, signal } from '@angular/core';
import { Producto } from './models';

export interface ItemCarrito {
  producto: Producto;
  cantidad: number;
}

/**
 * Estado compartido del carrito entre MenuComponent y CarritoComponent (paso "seleccionar").
 * El total aquí es solo una vista previa: el total oficial lo calcula el backend (RN5).
 */
@Injectable({ providedIn: 'root' })
export class CarritoService {
  readonly items = signal<ItemCarrito[]>([]);
  readonly total = computed(() =>
    this.items().reduce((suma, i) => suma + i.producto.precio * i.cantidad, 0),
  );
  readonly cantidadTotal = computed(() => this.items().reduce((s, i) => s + i.cantidad, 0));

  agregar(producto: Producto): void {
    if (!producto.disponible) {
      return; // RN3 también se valida en la interfaz
    }
    this.items.update((lista) => {
      const existe = lista.find((i) => i.producto.id === producto.id);
      return existe
        ? lista.map((i) => (i.producto.id === producto.id ? { ...i, cantidad: i.cantidad + 1 } : i))
        : [...lista, { producto, cantidad: 1 }];
    });
  }

  cambiarCantidad(productoId: number, cantidad: number): void {
    const valor = Math.max(1, Math.floor(cantidad || 1)); // RN2: cantidad >= 1
    this.items.update((lista) =>
      lista.map((i) => (i.producto.id === productoId ? { ...i, cantidad: valor } : i)),
    );
  }

  quitar(productoId: number): void {
    this.items.update((lista) => lista.filter((i) => i.producto.id !== productoId));
  }

  vaciar(): void {
    this.items.set([]);
  }
}
