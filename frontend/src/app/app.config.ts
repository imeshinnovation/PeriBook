import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { routes } from './app.routes';
import { authInterceptor } from './core/services/auth.interceptor';

/**
 * Configuración global de la aplicación Angular.
 *
 * proveedores clave:
 * - provideZoneChangeDetection con eventCoalescing activado para reducir
 *   la cantidad de detecciones de cambios en eventos rápidos (como typing).
 * - provideRouter con las rutas definidas en app.routes.ts.
 * - provideHttpClient con el interceptor funcional authInterceptor para
 *   inyectar el token JWT en cada petición saliente.
 *
 * Decidí usar el interceptor como función (HttpInterceptorFn) en lugar de
 * la clase HttpInterceptor tradicional porque el API funcional es más
 * concisa, tree-shakeable y se alinea con el estilo moderno de Angular.
 *
 * @author Alexander Rubio Cáceres
 */
export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor])),
  ],
};
