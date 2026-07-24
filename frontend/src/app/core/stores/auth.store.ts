import { Injectable, signal, computed } from '@angular/core';

/**
 * Estado interno del store de autenticación: token JWT, ID del usuario y alias.
 * Esta interfaz no se exporta porque solo AuthStore necesita conocer su forma.
 * El estado se persiste en localStorage para que sobreviva a recargas de página.
 */
interface AuthState {
  /** Token JWT activo o null si no hay sesión. */
  token: string | null;
  /** Identificador del usuario autenticado o null. */
  userId: string | null;
  /** Alias público del usuario o null. */
  alias: string | null;
}

/**
 * Store de autenticación basado en signals de Angular.
 *
 * Decidí usar signals en lugar de un servicio con BehaviorSubject porque
 * la API de signals es más declarativa, se integra nativamente con el
 * sistema de detección de cambios de Angular y elimina la necesidad de
 * suscribirse manualmente con pipe(async) o suscripciones en el componente.
 *
 * El estado se hidrata desde localStorage al construir el store, lo que
 * permite que la sesión persista entre recargas de página sin necesidad
 * de un servicio separado de persistencia.
 *
 * @author Alexander Rubio Cáceres
 */
@Injectable({ providedIn: 'root' })
export class AuthStore {
  /** Signal privada que contiene el estado completo. Solo se modifica desde login() y logout(). */
  private state = signal<AuthState>(this.cargarDeStorage());

  /** Signal derivada: expone solo el token. */
  token = computed(() => this.state().token);
  /** Signal derivada: expone solo el userId. */
  userId = computed(() => this.state().userId);
  /** Signal derivada: expone solo el alias. */
  alias = computed(() => this.state().alias);
  /** Signal derivada: true si hay un token presente (sin validar si expiró). */
  estaAutenticado = computed(() => !!this.state().token);

  /**
   * Recupera el estado guardado en localStorage.
   * Envoltura en try/catch por si alguien manipuló manualmente el storage
   * y guardó JSON inválido — en ese caso devolvemos un estado limpio.
   */
  private cargarDeStorage(): AuthState {
    try {
      const saved = localStorage.getItem('peribook_auth');
      return saved ? JSON.parse(saved) : { token: null, userId: null, alias: null };
    } catch {
      return { token: null, userId: null, alias: null };
    }
  }

  /**
   * Establece la sesión activa: persiste en localStorage y actualiza la signal.
   * El orden es importante: primero persistir, luego actualizar la signal para
   * que los consumidores (componentes, servicios) reaccionen al cambio.
   */
  login(token: string, userId: string, alias: string) {
    const newState = { token, userId, alias };
    localStorage.setItem('peribook_auth', JSON.stringify(newState));
    this.state.set(newState);
  }

  /**
   * Cierra la sesión: elimina los datos del storage y resetea la signal.
   * No invalida el token del lado del servidor — eso sería ideal con una
   * lista de tokens revocados, pero para el MVP alcanza con limpiar el
   * lado del cliente. El token eventualmente expirará por sí solo.
   */
  logout() {
    localStorage.removeItem('peribook_auth');
    this.state.set({ token: null, userId: null, alias: null });
  }
}
