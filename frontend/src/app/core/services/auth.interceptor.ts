import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthStore } from '../stores/auth.store';
import { Router } from '@angular/router';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';

/**
 * Interceptor HTTP funcional que inyecta el token JWT en cada petición
 * saliente y maneja errores 401 de forma global.
 *
 * Decidí usar HttpInterceptorFn (functional) en lugar de la clase
 * HttpInterceptor tradicional porque Angular 15+ la introdujo como
 * alternativa más liviana y tree-shakeable. El interceptor funcional
 * no necesita una clase, se registra directamente en provideHttpClient
 * y tiene menos boilerplate.
 *
 * El manejo global del 401 evita tener que poner catchError en cada
 * llamada HTTP individual. Si el token expiró, limpiamos la sesión y
 * redirigimos al login. La condición `token` adicional evita redirigir
 * cuando el 401 ocurre en una petición que nunca llevaba token (por
 * ejemplo, el propio login).
 *
 * @author Alexander Rubio Cáceres
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authStore = inject(AuthStore);
  const router = inject(Router);
  const token = authStore.token();

  if (token) {
    req = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` },
    });
  }

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 && token) {
        // Token inválido o expirado: cerramos sesión y redirigimos
        authStore.logout();
        router.navigate(['/login']);
      }
      return throwError(() => error);
    })
  );
};
