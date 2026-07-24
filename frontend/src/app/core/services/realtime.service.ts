import { Injectable, inject, OnDestroy } from '@angular/core';
import { AuthStore } from '../stores/auth.store';
import { FeedStore } from '../stores/feed.store';
import { FeedService } from './feed.service';
import { PublicacionCreadaEvent } from '../models/user.model';

// Cargados como scripts globales en angular.json (sockjs-client + stompjs)
// No usé la version npm porque en el momento de configurar el proyecto
// los tipos de Stomp sobre SockJS daban conflicto con las versiones
// modernas. Declararlos como any fue la salida mas pragmática.
declare var SockJS: any;
declare var Stomp: any;

/**
 * Servicio de conexión WebSocket en tiempo real usando STOMP sobre SockJS.
 *
 * PeriBook necesita actualizar el feed sin que el usuario tenga que recargar
 * la página. En lugar de polling cada N segundos (que seria wasteful),
 * implementé WebSocket con STOMP que es el protocolo que usan los message
 * brokers como RabbitMQ del lado del backend.
 *
 * SockJS actúa como fallback: si WebSocket no está disponible (proxies,
 * firewalls corporativos), degrada a long-polling o eventos SSE.
 *
 * La reconexión automática con reintento a los 5 segundos la implementé
 * a mano porque quería control explícito sobre cuándo y cómo reconectar,
 * sin depender de una librería externa.
 *
 * @author Alexander Rubio Cáceres
 */
@Injectable({ providedIn: 'root' })
export class RealtimeService implements OnDestroy {
  private authStore = inject(AuthStore);
  private feedStore = inject(FeedStore);
  private feedService = inject(FeedService);
  private stompClient: any = null;
  private conectado = false;

  /**
   * Establece la conexión WebSocket.
   * Guarda contra doble conexión con el flag conectado y verifica que
   * exista un token antes de intentar la conexión. El token se pasa como
   * query parameter para que el backend autentique el socket antes de
   * suscribir al usuario a cualquier topic.
   */
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
      // Suscribirse a /topic/feed asegura que recibimos todos los eventos
      // publicados por el backend en ese topic (broadcast a todos los usuarios).
      this.stompClient.subscribe('/topic/feed', (mensaje: any) => {
        // Intento parsear el evento para logging, pero el feed se refresca
        // completo independientemente del contenido del mensaje.
        try {
          const data = JSON.parse(mensaje.body);
          console.log('[WebSocket] Evento recibido:', data);
        } catch (e) {
          console.log('[WebSocket] Evento recibido (raw)');
        }
        this.refrescarFeed();
      });
    }, () => {
      // Callback de error/desconexion: reintenta automáticamente tras 5 segundos
      console.warn('[WebSocket] Desconectado, reintentando en 5s...');
      this.conectado = false;
      setTimeout(() => this.conectar(), 5000);
    });
  }

  /**
   * Refresca el feed completo desde el BFF.
   * Podria optimizarse para hacer un merge inteligente de los items nuevos
   * con los existentes, pero para el MVP recargar todo es suficiente y
   * evita problemas de sincronización.
   */
  private refrescarFeed() {
    this.feedService.obtenerFeed().subscribe({
      next: (items) => this.feedStore.setItems(items),
      error: (err) => console.error('[WebSocket] Error al refrescar feed:', err),
    });
  }

  /**
   * Desconexión explícita del WebSocket.
   * Se llama desde el logout del AppComponent para asegurar que no queden
   * conexiones huérfanas cuando el usuario cierra sesión.
   */
  desconectar() {
    if (this.stompClient) {
      this.stompClient.disconnect();
      this.conectado = false;
    }
  }

  /** Lifecycle hook de Angular: desconecta al destruir el servicio (aunque con providedIn root rara vez se destruye). */
  ngOnDestroy() {
    this.desconectar();
  }
}
