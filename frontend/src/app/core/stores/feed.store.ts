import { Injectable, signal } from '@angular/core';
import { FeedItem } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class FeedStore {
  items = signal<FeedItem[]>([]);
  cargando = signal(false);
  error = signal<string | null>(null);

  setItems(items: FeedItem[]) {
    this.items.set(items);
  }

  agregarAlInicio(item: FeedItem) {
    this.items.update(list => [item, ...list]);
  }

  actualizarLikes(publicacionId: string, totalLikes: number) {
    this.items.update(list =>
      list.map(item =>
        item.publicacionId === publicacionId ? { ...item, totalLikes } : item
      )
    );
  }
}
<!-- 2026-07-09 -->
