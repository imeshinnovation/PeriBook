# like-service

**Fase:** 5
**ADR relevantes:** 0002 (Clean Architecture + DDD)
**Puerto dev:** 8084

## Responsabilidad
Registrar likes (sin duplicados) y publicar `LikeRegistrado` en RabbitMQ.

## Checklist de esta fase
- [ ] Agregado `Like`
- [ ] `sp_registrar_like` (PL/pgSQL, anti-duplicados) — 2º procedure mínimo
- [ ] `DarLikeUseCase` con TDD (incluye caso "like duplicado")
- [ ] Publisher de `LikeRegistrado`
- [ ] `Dockerfile` + `docker-compose.yml` propio (Postgres `like-db`)
