import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LoginRequest, LoginResponse, PerfilResponse } from '../models/user.model';

/**
 * Servicio de autenticación.
 *
 * Encapsula las llamadas HTTP relacionadas con la autenticación de usuarios.
 * Separar esto del store fue una decisión deliberada: el servicio se ocupa
 * de la comunicación con el backend (HTTP), mientras que el store gestiona
 * el estado local. Asi mantenemos separadas las responsabilidades y podemos
 * cambiar la implementación del transporte sin tocar el store.
 *
 * @author Alexander Rubio Cáceres
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  /** Base URL para los endpoints de autenticación. Se resuelve contra el proxy de Angular o el BFF según el entorno. */
  private apiUrl = '/api/auth';

  /**
   * Envía las credenciales al backend y devuelve el token JWT junto con
   * los datos básicos del usuario.
   */
  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, request);
  }
}
