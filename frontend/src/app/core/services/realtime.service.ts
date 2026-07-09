import { Injectable, inject, OnDestroy } from '@angular/core';
import { AuthStore } from '../stores/auth.store';
import { FeedStore } from '../stores/feed.store';
import { FeedService } from './feed.service';
import { PublicacionCreadaEvent } from '../models/user.model';

// Cargados como scripts globales en angular.json (sockjs-client + stompjs)
declare var SockJS: any;
declare var Stomp: any;

@Injectable({ providedIn: 'root' })
export class RealtimeService implements OnDestroy {
  private authStore = inject(AuthStore);
  private feedStore = inject(FeedStore);
  private feedService = inject(FeedService);
  private stompClient: any = null;
  private conectado = false;

  conectar() {
    if (this.conectado) return;
    const token = this.authStore.token();
    if (!token) return;

    const socket = new SockJS(`/ws?token=${token}`);
    this.stompClient = Stomp.over(socket);
    this.stompClient.debug = () => {};

    this.stompClient.connect({}, () => {
      this.conectado = true;
      console.log('[WebSocket] Conectado');

      // ── Feed en tiempo real ───────────────────────────
      // Cualquier evento (nueva publicación o like) refresca el feed
      this.stompClient.subscribe('/topic/feed', (mensaje: any) => {
        try {
          const data = JSON.parse(mensaje.body);
          console.log('[WebSocket] Evento recibido:', data);
        } catch (e) {
          console.log('[WebSocket] Evento recibido (raw)');
        }
        this.refrescarFeed();
      });
    }, () => {
      console.warn('[WebSocket] Desconectado, reintentando en 5s...');
      this.conectado = false;
      setTimeout(() => this.conectar(), 5000);
    });
  }

  private refrescarFeed() {
    this.feedService.obtenerFeed().subscribe({
      next: (items) => this.feedStore.setItems(items),
      error: (err) => console.error('[WebSocket] Error al refrescar feed:', err),
    });
  }

  desconectar() {
    if (this.stompClient) {
      this.stompClient.disconnect();
      this.conectado = false;
    }
  }

  ngOnDestroy() {
    this.desconectar();
  }
}
<!-- 2026-07-09 -->
