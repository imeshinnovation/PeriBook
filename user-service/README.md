# user-service

**Fase:** 3
**ADR relevantes:** 0002 (Clean Architecture + DDD)
**Puerto dev:** 8082

## Responsabilidad
Perfil de usuario: nombres, apellidos, fecha de nacimiento, alias.

## Checklist de esta fase
- [ ] Agregado `Perfil`
- [ ] `ObtenerPerfilUseCase` con TDD
- [ ] Endpoint `GET /api/users/{id}`
- [ ] Valida JWT emitido por auth-service (Resource Server, misma llave pública)
- [ ] `Dockerfile` + `docker-compose.yml` propio (Postgres `user-db`)
