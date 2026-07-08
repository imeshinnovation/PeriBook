# bff-web

**Fase:** 2 (esqueleto) / se completa junto con user, post, like
**ADR relevantes:** 0003 (BFF separado del Gateway)
**Puerto dev:** 8086

## Responsabilidad
Agrega en una sola respuesta lo que el frontend necesita (feed enriquecido:
mensaje + alias del autor + contador de likes). Stateless, sin base de datos.

## Checklist de esta fase
- [x] `ObtenerFeedEnriquecidoUseCase` (orquesta post/user/like en paralelo) → **Fase 6**
- [x] `WebClient` + Resilience4j (timeout + fallback) → **configurado en pom.xml, listo para Fase 6**
- [x] Endpoint `GET /bff/feed` → **Fase 6**
- [x] `Dockerfile` + `docker-compose.yml` propio (sin DB)
- [x] Esqueleto compila y arranca (Fase 2)
