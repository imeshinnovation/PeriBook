import { Injectable, signal } from '@angular/core';
import { FeedItem } from '../models/user.model';

/**
 * Store del feed basado en signals.
 *
 * Centraliza el estado del feed de publicaciones para que tanto el
 * componente FeedComponent como el servicio RealtimeService puedan
 * modificarlo sin depender uno del otro. Esto sigue el principio de
 * que el estado debe vivir fuera del componente para facilitar las
 * actualizaciones desde múltiples fuentes (HTTP + WebSocket).
 *
 * Las signals son públicas porque el template las necesita. En un
 * escenario más grande pondría una capa de selectores computados,
 * pero para el alcance actual es suficiente.
 *
 * @author Alexander Rubio Cáceres
 */
@Injectable({ providedIn: 'root' })
export class FeedStore {
  /** Lista completa de publicaciones del feed. */
  items = signal<FeedItem[]>([]);
  /** Indicador de carga — el template lo usa para mostrar un spinner. */
  cargando = signal(false);
  /** Mensaje de error si algo salió mal al obtener el feed. */
  error = signal<string | null>(null);

  /** Reemplaza completamente la lista del feed (por ejemplo, tras una recarga completa). */
  setItems(items: FeedItem[]) {
    this.items.set(items);
  }

  /**
   * Agrega una publicación al inicio del feed.
   * Ideal para cuando el WebSocket notifica una nueva publicación del
   * propio usuario y queremos mostrarla de inmediato sin recargar todo.
   */
  agregarAlInicio(item: FeedItem) {
    this.items.update(list => [item, ...list]);
  }

  /**
   * Actualiza el contador de likes de una publicación específica.
   * Usa una función pura dentro de update para mantener la inmutabilidad:
   * solo se reemplaza el item cuyo ID coincide, el resto permanece igual.
   */
  actualizarLikes(publicacionId: string, totalLikes: number) {
    this.items.update(list =>
      list.map(item =>
        item.publicacionId === publicacionId ? { ...item, totalLikes } : item
      )
    );
  }
}
