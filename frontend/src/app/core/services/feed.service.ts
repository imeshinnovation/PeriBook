import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { FeedItem, PerfilResponse } from '../models/user.model';

/**
 * Servicio que agrupa las operaciones sobre el feed, publicaciones, perfil y likes.
 *
 * Notar que la obtención del feed pasa por /bff/feed mientras que las demás
 * operaciones van directo a /api/*. Esto refleja la arquitectura BFF
 * (Backend For Frontend): el feed requiere agregación de datos de varios
 * servicios (posts, usuarios, likes) y el BFF se encarga de orquestar
 * esas llamadas. En cambio, crear una publicación o dar like son
 * operaciones atómicas que pueden ir directo al servicio correspondiente.
 *
 * @author Alexander Rubio Cáceres
 */
@Injectable({ providedIn: 'root' })
export class FeedService {
  private http = inject(HttpClient);

  /**
   * Obtiene las publicaciones del feed desde el BFF.
   * El BFF se encarga de agregar los datos de autor y likes.
   * El parámetro limite controla cuántas publicaciones se traen (default 20).
   */
  obtenerFeed(limite = 20): Observable<FeedItem[]> {
    return this.http.get<FeedItem[]>(`/bff/feed?limite=${limite}`);
  }

  /**
   * Crea una nueva publicación. Va directo al post-service porque es una
   * operación simple que no necesita agregación.
   */
  crearPublicacion(contenido: string): Observable<any> {
    return this.http.post('/api/posts', { contenido });
  }

  /**
   * Obtiene los datos de perfil de un usuario específico.
   */
  obtenerPerfil(userId: string): Observable<PerfilResponse> {
    return this.http.get<PerfilResponse>(`/api/users/${userId}`);
  }

  /**
   * Envia un like a una publicación.
   * El query parameter publicacionId identifica la publicación objetivo.
   */
  darLike(publicacionId: string): Observable<any> {
    return this.http.post(`/api/likes?publicacionId=${publicacionId}`, {});
  }
}
