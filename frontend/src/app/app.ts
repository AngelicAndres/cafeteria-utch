import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from './core/auth.service';
import { CarritoService } from './core/carrito.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.html',
})
export class App {
  readonly auth = inject(AuthService);
  readonly carrito = inject(CarritoService);

  salir(): void {
    this.carrito.vaciar();
    this.auth.logout();
  }
}
