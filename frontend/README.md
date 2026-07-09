# frontend

**Puerto:** 4200 | **Tecnología:** Angular 20

## Responsabilidad

Aplicación de una sola página (SPA) que proporciona la interfaz de usuario
de PeriBook. Se comunica exclusivamente con el API Gateway (HTTP REST para
datos y WebSocket para tiempo real).

## Pantallas

| Ruta | Componente | Funcionalidad |
|---|---|---|
| `/login` | `LoginComponent` | Formulario de inicio de sesión. Obtiene JWT y conecta WebSocket |
| `/feed` | `FeedComponent` | Feed de publicaciones. Crear posts, dar likes, scroll infinito |
| `/profile` | `ProfileComponent` | Perfil del usuario autenticado |

## Stack interno

- **Angular 20** con standalone components y lazy loading
- **NgRx SignalStore** para estado reactivo (AuthStore, FeedStore)
- **SockJS + STOMP** para WebSocket
- **Interceptor HTTP** que agrega el token JWT a todas las peticiones
- **Paleta verde olivo** personalizada (#556B2F primario, #6B7A4F secundario)

## Tiempo real

Al iniciar sesión, el `RealtimeService` establece una conexión WebSocket
al Gateway (ruta `/ws`). Se suscribe al canal `/topic/feed` y ante cualquier
evento (nueva publicación o like) refresca el feed automáticamente sin
necesidad de recargar la página.

## Docker

La imagen de producción usa un build multi-stage:
1. `node:24-alpine` compila la aplicación Angular
2. `nginx:stable-alpine` sirve los archivos estáticos y proxypea `/api/`,
   `/bff/` y `/ws` al API Gateway

## Desarrollo

```bash
docker compose up          # Levanta el frontend (puerto 4200)
npm start                  # Desarrollo local con hot reload
```
<!-- 2026-07-09 -->
