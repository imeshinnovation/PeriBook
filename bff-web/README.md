# bff-web

**Fase:** 2 (esqueleto) / se completa junto con user, post, like
**ADR relevantes:** 0003 (BFF separado del Gateway)
**Puerto dev:** 8086

## Responsabilidad
Agrega en una sola respuesta lo que el frontend necesita (feed enriquecido:
mensaje + alias del autor + contador de likes). Stateless, sin base de datos.

## Checklist de esta fase
- [ ] `ObtenerFeedEnriquecidoUseCase` (orquesta post/user/like en paralelo)
- [ ] `WebClient` + Resilience4j (timeout + fallback)
- [ ] Endpoint `GET /bff/feed`
- [ ] `Dockerfile` + `docker-compose.yml` propio (sin DB)
