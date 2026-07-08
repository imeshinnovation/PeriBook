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
export class LoginComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private authStore = inject(AuthStore);
  private realtimeService = inject(RealtimeService);
  private router = inject(Router);

  form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
  });

  cargando = false;
  error: string | null = null;

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
