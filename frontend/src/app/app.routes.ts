import { Routes } from '@angular/router';

/**
 * Definición de rutas de la aplicación.
 *
 * Usé lazy loading con loadComponent en lugar de cargar los componentes
 * de forma eager. Esto reduce el bundle inicial porque cada pantalla se
 * descarga bajo demanda cuando el usuario navega a ella.
 *
 * La ruta vacía redirige a /login y el wildcard ** atrapa cualquier ruta
 * no definida redirigiendo también al login. Es una decisión consciente:
 * como PeriBook requiere autenticación para casi todo, tiene sentido que
 * caer al login sea el comportamiento por defecto.
 *
 * @author Alexander Rubio Cáceres
 */
export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  {
    path: 'login',
    loadComponent: () => import('./features/login/login.component').then(m => m.LoginComponent),
  },
  {
    path: 'feed',
    loadComponent: () => import('./features/feed/feed.component').then(m => m.FeedComponent),
  },
  {
    path: 'profile',
    loadComponent: () => import('./features/profile/profile.component').then(m => m.ProfileComponent),
  },
  { path: '**', redirectTo: '/login' },
];
