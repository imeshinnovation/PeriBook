# bff-web

**Puerto:** 8086 | **Base de datos:** No (sin estado)

## Responsabilidad

Backend For Frontend. Agrega en una sola respuesta lo que el frontend necesita
para mostrar el feed: junta las publicaciones (post-service), el alias del autor
(user-service) y el contador de likes (like-service). Sin esta capa, el
frontend tendría que hacer tres llamadas HTTP y ensamblar el resultado.

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/bff/feed?limite=20` | Feed enriquecido con mensaje, alias del autor y contador de likes |

## Stack interno

- **Spring WebFlux** para llamadas HTTP reactivas y no bloqueantes
- **WebClient** para comunicación con los servicios de dominio
- **Resilience4j** con fallback: si like-service no responde, el contador
  de likes se muestra como 0 sin romper el feed
- **Spring Security** con OAuth2 Resource Server reactivo

## Cómo funciona

1. Recibe una petición `GET /bff/feed` del API Gateway (que ya validó el JWT)
2. Propaga el token JWT a post-service, user-service y like-service
3. Por cada publicación, obtiene en paralelo el alias del autor y el contador
   de likes usando `flatMapSequential` (procesamiento concurrente con orden
   garantizado)
4. Si like-service falla, devuelve `0` como contador sin interrumpir el feed

## Desarrollo

```bash
docker compose up          # Levanta el BFF (puerto 8086)
mvn test                   # 2 tests con WebTestClient reactivo
```
<!-- 2026-07-09 -->
