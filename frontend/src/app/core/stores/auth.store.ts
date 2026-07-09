import { Injectable, signal, computed } from '@angular/core';

interface AuthState {
  token: string | null;
  userId: string | null;
  alias: string | null;
}

@Injectable({ providedIn: 'root' })
export class AuthStore {
  private state = signal<AuthState>(this.cargarDeStorage());

  token = computed(() => this.state().token);
  userId = computed(() => this.state().userId);
  alias = computed(() => this.state().alias);
  estaAutenticado = computed(() => !!this.state().token);

  private cargarDeStorage(): AuthState {
    try {
      const saved = localStorage.getItem('peribook_auth');
      return saved ? JSON.parse(saved) : { token: null, userId: null, alias: null };
    } catch {
      return { token: null, userId: null, alias: null };
    }
  }

  login(token: string, userId: string, alias: string) {
    const newState = { token, userId, alias };
    localStorage.setItem('peribook_auth', JSON.stringify(newState));
    this.state.set(newState);
  }

  logout() {
    localStorage.removeItem('peribook_auth');
    this.state.set({ token: null, userId: null, alias: null });
  }
}
