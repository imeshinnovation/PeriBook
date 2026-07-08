import { Component, inject } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { AuthStore } from './core/stores/auth.store';
import { NgIf, AsyncPipe } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NgIf],
  template: `
    <nav class="navbar" *ngIf="authStore.estaAutenticado()">
      <span class="navbar-brand">PeriBook</span>
      <span class="navbar-user">{{ authStore.alias() }}</span>
      <div class="navbar-actions">
        <button class="btn-logout" (click)="irAFeed()">Feed</button>
        <button class="btn-logout" (click)="irAPerfil()">Perfil</button>
        <button class="btn-logout" (click)="logout()">Salir</button>
      </div>
    </nav>
    <router-outlet />
  `,
})
export class AppComponent {
  authStore = inject(AuthStore);
  private router = inject(Router);

  irAFeed() {
    this.router.navigate(['/feed']);
  }

  irAPerfil() {
    this.router.navigate(['/profile']);
  }

  logout() {
    this.authStore.logout();
    this.router.navigate(['/login']);
  }
}
