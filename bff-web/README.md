# bff-web

**Fase:** 2 (esqueleto) / se completa junto con user, post, like
**ADR relevantes:** 0003 (BFF separado del Gateway)
**Puerto dev:** 8086

## Responsabilidad
Agrega en una sola respuesta lo que el frontend necesita (feed enriquecido:
mensaje + alias del autor + contador de likes). Stateless, sin base de datos.

## Checklist de esta fase
- [x] `ObtenerFeedEnriquecidoUseCase` (orquesta post/user/like en paralelo) ✅
- [x] `WebClient` + Resilience4j (timeout + fallback) ✅ — Circuit Breaker en like-service
- [x] Endpoint `GET /bff/feed` ✅
- [x] `Dockerfile` + `docker-compose.yml` propio (sin DB)
- [x] Esqueleto compila y arranca (Fase 2)
