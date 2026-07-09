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
export class FeedComponent implements OnInit {
  feedService = inject(FeedService);
  feedStore = inject(FeedStore);
  authStore = inject(AuthStore);

  nuevoContenido = '';
  publicando = false;

  ngOnInit() {
    this.cargarFeed();
  }

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

  darLike(publicacionId: string) {
    this.feedService.darLike(publicacionId).subscribe({
      next: (res: any) => {
        if (res?.esNuevo) {
          this.cargarFeed(); // Refrescar contadores
        }
      },
      error: (err) => console.error('Error al dar like', err),
    });
  }
}
<!-- 2026-07-09 -->
