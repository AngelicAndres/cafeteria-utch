import { CurrencyPipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { mensajeError } from '../../core/api';
import { CarritoService } from '../../core/carrito.service';
import { Producto } from '../../core/models';
import { ProductoService } from '../../core/producto.service';
import { CarritoComponent } from '../carrito/carrito.component';

/** Paso 1 y 2 del flujo: consultar menú y seleccionar productos (RF2). */
@Component({
  selector: 'app-menu',
  imports: [CurrencyPipe, CarritoComponent],
  templateUrl: './menu.component.html',
})
export class MenuComponent implements OnInit {
  private readonly productoService = inject(ProductoService);
  readonly carrito = inject(CarritoService);

  readonly productos = signal<Producto[]>([]);
  readonly cargando = signal(true);
  readonly error = signal<string | null>(null);

  ngOnInit(): void {
    this.productoService.listar().subscribe({
      next: (lista) => {
        this.productos.set(lista);
        this.cargando.set(false);
      },
      error: (e) => {
        this.error.set(mensajeError(e));
        this.cargando.set(false);
      },
    });
  }

  agregar(p: Producto): void {
    this.carrito.agregar(p);
  }
}
