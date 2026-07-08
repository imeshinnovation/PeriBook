# post-service

**Fase:** 4
**ADR relevantes:** 0002 (Clean Architecture + DDD)
**Puerto dev:** 8083

## Responsabilidad
Crear y listar publicaciones. Publica el evento `PublicacionCreada` en RabbitMQ.

## Checklist de esta fase
- [x] Agregado `Publicacion`
- [x] `CrearPublicacionUseCase` / `ListarPublicacionesUseCase` con TDD — 6 tests ✅
- [x] `sp_crear_publicacion` (PL/pgSQL) — 1 de los 2 procedures mínimos
- [x] Publisher de `PublicacionCreada` al exchange `peribook.events`
- [x] `Dockerfile` + `docker-compose.yml` propio (Postgres `post-db`)
