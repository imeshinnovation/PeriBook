import { Component, inject, OnInit, signal } from '@angular/core';
import { FeedService } from '../../core/services/feed.service';
import { AuthStore } from '../../core/stores/auth.store';
import { PerfilResponse } from '../../core/models/user.model';
import { DatePipe, NgIf } from '@angular/common';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [NgIf, DatePipe],
  template: `
    <div class="container">
      <div class="card profile-header" *ngIf="perfil(); else loading">
        <div class="profile-avatar">{{ perfil()?.alias?.charAt(0)?.toUpperCase() }}</div>
        <h2 style="color:#3a4720">{{ perfil()?.nombres }} {{ perfil()?.apellidos }}</h2>
        <p style="color:#888">&#64;{{ perfil()?.alias }}</p>
        <p style="color:#888;font-size:0.9rem" *ngIf="perfil()?.fechaNacimiento">
          Nacimiento: {{ perfil()?.fechaNacimiento | date:'longDate' }}
        </p>
      </div>
      <ng-template #loading>
        <div class="spinner"><div class="loader"></div></div>
      </ng-template>
    </div>
  `,
})
/**
 * Componente de perfil de usuario.
 *
 * Muestra los datos del perfil del usuario autenticado: nombre completo,
 * alias y fecha de nacimiento. Usa una signal local para el estado del
 * perfil en lugar de un store global porque los datos del perfil son
 * específicos de esta vista y no los necesita ningún otro componente.
 *
 * Decidí usar FeedService.obtenerPerfil en lugar de crear un servicio
 * separado de perfiles porque por ahora solo hay una llamada relacionada.
 * Si el perfil creciera en funcionalidades (editar, avatar, estadisticas),
 * lo moveria a un ProfileService dedicado.
 *
 * @author Alexander Rubio Cáceres
 */
export class ProfileComponent implements OnInit {
  private feedService = inject(FeedService);
  private authStore = inject(AuthStore);
  /** Signal local con los datos del perfil. Inicia como null y se llena al cargar. */
  perfil = signal<PerfilResponse | null>(null);

  ngOnInit() {
    const userId = this.authStore.userId();
    if (userId) {
      this.feedService.obtenerPerfil(userId).subscribe({
        next: (p) => this.perfil.set(p),
        error: (err) => console.error('Error al cargar perfil', err),
      });
    }
  }
}
