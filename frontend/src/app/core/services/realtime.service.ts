import { Injectable, inject, OnDestroy } from '@angular/core';
import { AuthStore } from '../stores/auth.store';
import { FeedStore } from '../stores/feed.store';
import { PublicacionCreadaEvent, LikeRegistradoEvent } from '../models/user.model';

declare var SockJS: any;
declare var Stomp: any;

@Injectable({ providedIn: 'root' })
export class RealtimeService implements OnDestroy {
  private authStore = inject(AuthStore);
  private feedStore = inject(FeedStore);
  private stompClient: any = null;
  private conectado = false;

  conectar() {
    if (this.conectado) return;

    const socket = new SockJS('/ws');
    this.stompClient = Stomp.over(socket);
    this.stompClient.debug = () => {}; // silenciar logs

    this.stompClient.connect({}, () => {
      this.conectado = true;
      console.log('WebSocket conectado');

      // Suscribirse al feed global
      this.stompClient.subscribe('/topic/feed', (mensaje: any) => {
        const event: PublicacionCreadaEvent = JSON.parse(mensaje.body);
        this.feedStore.agregarAlInicio({
          publicacionId: event.publicacionId,
          autorId: event.autorId,
          contenido: event.contenido,
          creadaEn: event.creadaEn,
          autorAlias: '...',
          totalLikes: 0,
        });
      });

      // Suscribirse a likes por publicación
      this.stompClient.subscribe('/topic/publicacion.*.likes', (mensaje: any) => {
        const event: LikeRegistradoEvent = JSON.parse(mensaje.body);
        // El contador real se obtiene del evento o se refresca
        console.log('Like recibido:', event);
      });
    }, () => {
      console.warn('WebSocket desconectado, reintentando en 5s...');
      setTimeout(() => this.conectar(), 5000);
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
