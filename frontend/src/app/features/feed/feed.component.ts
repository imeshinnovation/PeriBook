import { Component, inject, OnInit } from '@angular/core';
import { FeedService } from '../../core/services/feed.service';
import { FeedStore } from '../../core/stores/feed.store';
import { AuthStore } from '../../core/stores/auth.store';
import { DatePipe, NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-feed',
  standalone: true,
  imports: [NgFor, NgIf, DatePipe, FormsModule],
  template: `
    <div class="container">
      <!-- Crear publicación -->
      <div class="card create-post">
        <textarea [(ngModel)]="nuevoContenido" placeholder="¿Qué estás pensando?" rows="2" maxlength="500"></textarea>
        <button class="btn btn-primary" (click)="crearPublicacion()"
                [disabled]="!nuevoContenido.trim() || publicando">
          {{ publicando ? '...' : 'Publicar' }}
        </button>
      </div>

      <!-- Feed -->
      <div *ngIf="feedStore.cargando()" class="spinner"><div class="loader"></div></div>

      <div *ngIf="!feedStore.cargando() && feedStore.items().length === 0" style="text-align:center;color:#888;padding:2rem">
        No hay publicaciones aún. ¡Sé el primero en publicar!
      </div>

      <div *ngFor="let item of feedStore.items()" class="card feed-item">
        <div class="feed-header">
          <div class="feed-avatar">{{ item.autorAlias.charAt(0).toUpperCase() }}</div>
          <div>
            <div class="feed-author">{{ item.autorAlias }}</div>
            <div class="feed-time">{{ item.creadaEn | date:'short' }}</div>
          </div>
        </div>
        <div class="feed-content">{{ item.contenido }}</div>
        <div class="feed-actions">
          <button class="like-btn" [class.liked]="false" (click)="darLike(item.publicacionId)">
            ❤️ {{ item.totalLikes }}
          </button>
        </div>
      </div>
    </div>
  `,
})
/**
 * Componente principal del feed de publicaciones.
 *
 * Aquí se muestra el listado de publicaciones, se pueden crear nuevas
 * y se pueden dar likes. El estado del feed (items, cargando, error)
 * lo gestiona FeedStore via signals, lo que permite que el componente
 * solo se preocupe por la interacción del usuario y deje la gestión
 * de estado al store.
 *
 * El textarea usa ngModel (FormsModule) porque es un formulario simple
 * de un solo campo. Para el login usé reactive forms por la validación
 * más compleja; aquí con template-driven alcanza y sobra.
 *
 * @author Alexander Rubio Cáceres
 */
export class FeedComponent implements OnInit {
  /** Servicio para llamadas HTTP relacionadas con el feed. Es público porque no necesita encapsulación adicional. */
  feedService = inject(FeedService);
  /** Store reactivo del feed. Las signals se leen directamente en el template. */
  feedStore = inject(FeedStore);
  /** Store de autenticación — se mantiene inyectado por si en el futuro necesitamos el userId o alias. */
  authStore = inject(AuthStore);

  /** Contenido del textarea para nueva publicación, enlazado con ngModel. */
  nuevoContenido = '';
  /** Bandera para deshabilitar el boton de publicar mientras se procesa la peticion. */
  publicando = false;

  ngOnInit() {
    this.cargarFeed();
  }

  /**
   * Obtiene las publicaciones del feed desde el BFF.
   * Actualiza la signal cargando para que el template muestre el spinner.
   */
  cargarFeed() {
    this.feedStore.cargando.set(true);
    this.feedService.obtenerFeed().subscribe({
      next: (items) => {
        this.feedStore.setItems(items);
        this.feedStore.cargando.set(false);
      },
      error: (err) => {
        console.error('Error al cargar feed', err);
        this.feedStore.cargando.set(false);
      },
    });
  }

  /**
   * Crea una nueva publicación.
   * Tras crearla, limpia el textarea y recarga el feed completo para
   * reflejar la nueva publicación. Podria insertarla al inicio del
   * array con agregarAlInicio para evitar recargar todo, pero recargar
   * asegura que el orden sea consistente con el servidor.
   */
  crearPublicacion() {
    const contenido = this.nuevoContenido.trim();
    if (!contenido) return;
    this.publicando = true;

    this.feedService.crearPublicacion(contenido).subscribe({
      next: () => {
        this.nuevoContenido = '';
        this.publicando = false;
        this.cargarFeed();
      },
      error: (err) => {
        console.error('Error al publicar', err);
        this.publicando = false;
      },
    });
  }

  /**
   * Envia un like a una publicación.
   * Solo refresca el feed si el backend indica que el like es nuevo
   * (res.esNuevo). Esto evita recargas innecesarias cuando el usuario
   * ya había dado like previamente.
   */
  darLike(publicacionId: string) {
    this.feedService.darLike(publicacionId).subscribe({
      next: (res: any) => {
        if (res?.esNuevo) {
          this.cargarFeed(); // Refrescar contadores tras un like nuevo
        }
      },
      error: (err) => console.error('Error al dar like', err),
    });
  }
}
