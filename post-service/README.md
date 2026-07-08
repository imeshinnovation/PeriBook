# post-service

**Fase:** 4
**ADR relevantes:** 0002 (Clean Architecture + DDD)
**Puerto dev:** 8083

## Responsabilidad
Crear y listar publicaciones. Publica el evento `PublicacionCreada` en RabbitMQ.

## Checklist de esta fase
- [ ] Agregado `Publicacion`
- [ ] `CrearPublicacionUseCase` / `ListarPublicacionesUseCase` con TDD
- [ ] `sp_crear_publicacion` (PL/pgSQL) — 1 de los 2 procedures mínimos
- [ ] Publisher de `PublicacionCreada` al exchange `peribook.events`
- [ ] `Dockerfile` + `docker-compose.yml` propio (Postgres `post-db`)
