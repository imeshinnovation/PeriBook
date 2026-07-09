import { Component, inject, OnInit } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { AuthStore } from './core/stores/auth.store';
import { RealtimeService } from './core/services/realtime.service';
import { NgIf } from '@angular/common';

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
export class AppComponent implements OnInit {
  authStore = inject(AuthStore);
  private router = inject(Router);
  private realtimeService = inject(RealtimeService);

  ngOnInit() {
    if (this.authStore.estaAutenticado()) {
      this.realtimeService.conectar();
    }
  }

  irAFeed() {
    this.router.navigate(['/feed']);
  }

  irAPerfil() {
    this.router.navigate(['/profile']);
  }

  logout() {
    this.realtimeService.desconectar();
    this.authStore.logout();
    this.router.navigate(['/login']);
  }
}
<!-- 2026-07-09 -->
