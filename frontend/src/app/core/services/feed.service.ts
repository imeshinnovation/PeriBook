import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { FeedItem, PerfilResponse } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class FeedService {
  private http = inject(HttpClient);

  obtenerFeed(limite = 20): Observable<FeedItem[]> {
    return this.http.get<FeedItem[]>(`/bff/feed?limite=${limite}`);
  }

  crearPublicacion(contenido: string): Observable<any> {
    return this.http.post('/api/posts', { contenido });
  }

  obtenerPerfil(userId: string): Observable<PerfilResponse> {
    return this.http.get<PerfilResponse>(`/api/users/${userId}`);
  }

  darLike(publicacionId: string): Observable<any> {
    return this.http.post(`/api/likes?publicacionId=${publicacionId}`, {});
  }
}
