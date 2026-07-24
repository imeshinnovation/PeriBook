import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { AuthStore } from '../../core/stores/auth.store';
import { RealtimeService } from '../../core/services/realtime.service';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, NgIf],
  template: `
    <div class="login-page">
      <div class="login-card">
        <h1>PeriBook</h1>
        <p>Conecta con tu comunidad</p>

        <form [formGroup]="form" (ngSubmit)="onSubmit()">
          <div class="form-group">
            <label>Email</label>
            <input class="input" type="email" formControlName="email" placeholder="ana@peribook.com" />
            <div class="text-danger" *ngIf="form.get('email')?.touched && form.get('email')?.invalid">
              Email inválido
            </div>
          </div>

          <div class="form-group">
            <label>Contraseña</label>
            <input class="input" type="password" formControlName="password" placeholder="••••••••" />
            <div class="text-danger" *ngIf="form.get('password')?.touched && form.get('password')?.invalid">
              Mínimo 8 caracteres
            </div>
          </div>

          <div class="text-danger" *ngIf="error" style="margin-bottom:1rem">{{ error }}</div>

          <button class="btn btn-primary" type="submit" [disabled]="form.invalid || cargando" style="width:100%">
            {{ cargando ? 'Entrando...' : 'Iniciar sesión' }}
          </button>
        </form>

        <p style="margin-top:1.5rem;font-size:0.8rem;color:#aaa;text-align:center">
          Usuarios de prueba: ana&#64;peribook.com, carlos&#64;peribook.com, admin&#64;peribook.com
        </p>
      </div>
    </div>
  `,
})
/**
 * Componente de inicio de sesión.
 *
 * Usa ReactiveFormsModule con validación en tiempo real: el boton de
 * submit se deshabilita mientras el formulario sea inválido. Decidí
 * usar formularios reactivos en lugar de template-driven porque:
 *
 * 1. La validación es más declarativa y fácil de extender.
 * 2. Es más sencillo escribir tests unitarios sobre el formulario.
 * 3. El estado del formulario está desacoplado del template.
 *
 * Al iniciar sesión exitosamente, además de guardar el token en el store,
 * conecta el WebSocket para recibir actualizaciones en tiempo real.
 * Esto lo hago aquí y no en el guard de ruta porque la conexión WebSocket
 * debe ocurrir explicitamente después de obtener el token.
 *
 * @author Alexander Rubio Cáceres
 */
export class LoginComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private authStore = inject(AuthStore);
  private realtimeService = inject(RealtimeService);
  private router = inject(Router);

  /** Formulario reactivo con validadores de email y contraseña. */
  form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
  });

  /** Bandera para mostrar el estado de carga mientras se procesa la petición. */
  cargando = false;
  /** Mensaje de error visible en el template cuando las credenciales son inválidas. */
  error: string | null = null;

  /**
   * Procesa el envío del formulario.
   * Si el formulario es inválido, no hace nada (el botón ya está deshabilitado,
   * pero esta guarda es por seguridad). Al recibir respuesta exitosa, persiste
   * la sesión en el store, conecta WebSocket y navega al feed.
   */
  onSubmit() {
    if (this.form.invalid) return;
    this.cargando = true;
    this.error = null;

    this.authService.login(this.form.value as any).subscribe({
      next: (res) => {
        this.authStore.login(res.token, res.userId, res.alias);
        this.realtimeService.conectar();
        this.router.navigate(['/feed']);
      },
      error: () => {
        this.error = 'Credenciales inválidas. Intenta de nuevo.';
        this.cargando = false;
      },
    });
  }
}
