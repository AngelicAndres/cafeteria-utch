import { CurrencyPipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { mensajeError } from '../../core/api';
import { Producto } from '../../core/models';
import { ProductoService } from '../../core/producto.service';

/** Gestión de productos por el administrador (RF8, RN1). */
@Component({
  selector: 'app-admin-productos',
  imports: [ReactiveFormsModule, CurrencyPipe],
  templateUrl: './admin-productos.component.html',
})
export class AdminProductosComponent implements OnInit {
  private readonly productoService = inject(ProductoService);

  readonly productos = signal<Producto[]>([]);
  readonly editandoId = signal<number | null>(null);
  readonly error = signal<string | null>(null);
  readonly aviso = signal<string | null>(null);

  readonly form = inject(FormBuilder).nonNullable.group({
    nombre: ['', [Validators.required, Validators.maxLength(100)]],
    precio: [0, [Validators.required, Validators.min(0.01)]],
    disponible: [true],
  });

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.productoService.listar().subscribe({
      next: (lista) => this.productos.set(lista),
      error: (e) => this.error.set(mensajeError(e)),
    });
  }

  editar(p: Producto): void {
    this.editandoId.set(p.id);
    this.form.setValue({ nombre: p.nombre, precio: p.precio, disponible: p.disponible });
  }

  cancelar(): void {
    this.editandoId.set(null);
    this.form.reset({ nombre: '', precio: 0, disponible: true });
  }

  guardar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const datos = this.form.getRawValue();
    const id = this.editandoId();
    const peticion = id === null ? this.productoService.crear(datos) : this.productoService.actualizar(id, datos);
    this.error.set(null);
    peticion.subscribe({
      next: (p) => {
        this.aviso.set(`Producto "${p.nombre}" ${id === null ? 'creado' : 'actualizado'}`);
        this.cancelar();
        this.cargar();
      },
      error: (e) => this.error.set(mensajeError(e)),
    });
  }

  alternarDisponibilidad(p: Producto): void {
    this.productoService.cambiarDisponibilidad(p.id, !p.disponible).subscribe({
      next: (act) => {
        this.aviso.set(`"${act.nombre}" ahora está ${act.disponible ? 'disponible' : 'no disponible'}`);
        this.cargar();
      },
      error: (e) => this.error.set(mensajeError(e)),
    });
  }
}
