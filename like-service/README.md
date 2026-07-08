# like-service

**Fase:** 5
**ADR relevantes:** 0002 (Clean Architecture + DDD)
**Puerto dev:** 8084

## Responsabilidad
Registrar likes (sin duplicados) y publicar `LikeRegistrado` en RabbitMQ.

## Checklist de esta fase
- [x] Agregado `Like`
- [x] `sp_registrar_like` (PL/pgSQL, anti-duplicados) — 2º procedure mínimo
- [x] `DarLikeUseCase` con TDD (incluye caso "like duplicado") — 4 tests ✅
- [x] Publisher de `LikeRegistrado`
- [x] `Dockerfile` + `docker-compose.yml` propio (Postgres `like-db`)
