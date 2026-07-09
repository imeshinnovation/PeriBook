import { Injectable, inject, OnDestroy } from '@angular/core';
import { AuthStore } from '../stores/auth.store';
import { FeedStore } from '../stores/feed.store';
import { PublicacionCreadaEvent, LikeRegistradoEvent } from '../models/user.model';

// Cargados como scripts globales en angular.json (sockjs-client + stompjs)
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
    // Pasar el token JWT como query param para que el Gateway valide el handshake SockJS
    const token = this.authStore.token();
    const socket = new SockJS(`/ws?token=${token}`);
    this.stompClient = Stomp.over(socket);
    this.stompClient.debug = () => {};

    this.stompClient.connect({}, () => {
      this.conectado = true;
      console.log('WebSocket conectado');

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

      this.stompClient.subscribe('/topic/publicacion.*.likes', (mensaje: any) => {
        const event: LikeRegistradoEvent = JSON.parse(mensaje.body);
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
