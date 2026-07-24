import { Component, inject, OnInit } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { AuthStore } from './core/stores/auth.store';
import { RealtimeService } from './core/services/realtime.service';
import { NgIf } from '@angular/common';

/**
 * Componente raíz de PeriBook.
 * Actúa como shell principal de la aplicación: renderiza la barra de navegación
 * condicional cuando el usuario está autenticado y gestiona el enrutamiento
 * hijo mediante <router-outlet>.
 *
 * Decidí mantener el navbar aquí mismo en lugar de crear un componente
 * layout separado porque la aplicación sigue siendo pequeña y no quería
 * añadir otra capa de indirección sin necesidad. Si el proyecto crece
 * lo natural sería extraer un AppLayoutComponent.
 *
 * Uso inject() en lugar de constructor injection porque me parece más
 * legible y evita tener que declarar propiedades privadas manualmente.
 *
 * @author Alexander Rubio Cáceres
 */
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
  /** Instancia del store de autenticación — accesible desde el template para mostrar/ocultar el navbar según el estado de sesión. */
  authStore = inject(AuthStore);
  private router = inject(Router);
  private realtimeService = inject(RealtimeService);

  /**
   * Inicializa la conexión WebSocket si el usuario ya tenía sesión activa
   * (por ejemplo, tras recargar la página). Así evitamos reconectar
   * innecesariamente cuando el usuario está en la pantalla de login.
   */
  ngOnInit() {
    if (this.authStore.estaAutenticado()) {
      this.realtimeService.conectar();
    }
  }

  /** Navega al feed principal. */
  irAFeed() {
    this.router.navigate(['/feed']);
  }

  /** Navega a la pantalla de perfil del usuario autenticado. */
  irAPerfil() {
    this.router.navigate(['/profile']);
  }

  /**
   * Cierra la sesión: primero desconecta el WebSocket, luego limpia
   * el estado de autenticación y redirige al login. El orden importa:
   * desconectar primero evita que lleguen eventos después del logout.
   */
  logout() {
    this.realtimeService.desconectar();
    this.authStore.logout();
    this.router.navigate(['/login']);
  }
}
