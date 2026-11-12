import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { mensajeError } from '../../core/api';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
  templateUrl: './login.component.html',
})
export class LoginComponent {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  readonly enviando = signal(false);
  readonly error = signal<string | null>(null);

  readonly form = inject(FormBuilder).nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
  });

  /** Usuarios semilla para la demostración. */
  readonly demos = [
    { etiqueta: 'Cliente', email: 'cliente@utch.mx', password: 'cliente123' },
    { etiqueta: 'Personal', email: 'personal@utch.mx', password: 'personal123' },
    { etiqueta: 'Admin', email: 'admin@utch.mx', password: 'admin123' },
  ];

  usarDemo(d: { email: string; password: string }): void {
    this.form.setValue({ email: d.email, password: d.password });
  }

  entrar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.enviando.set(true);
    this.error.set(null);
    const { email, password } = this.form.getRawValue();
    this.auth.login(email, password).subscribe({
      next: (s) => this.router.navigateByUrl(this.auth.rutaInicio(s.rol)),
      error: (e) => {
        this.error.set(mensajeError(e));
        this.enviando.set(false);
      },
    });
  }
}
