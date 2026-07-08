# user-service

**Fase:** 3
**ADR relevantes:** 0002 (Clean Architecture + DDD)
**Puerto dev:** 8082

## Responsabilidad
Perfil de usuario: nombres, apellidos, fecha de nacimiento, alias.

## Checklist de esta fase
- [x] Agregado `Perfil`
- [x] `ObtenerPerfilUseCase` con TDD — 6 tests ✅
- [x] Endpoint `GET /api/users/{id}`
- [x] Valida JWT emitido por auth-service (Resource Server, misma llave pública)
- [x] `Dockerfile` + `docker-compose.yml` propio (Postgres `user-db`)
