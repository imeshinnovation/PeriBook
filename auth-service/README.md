# auth-service

**Fase:** 1 (primera a construir — ver roadmap)
**ADR relevantes:** 0001 (monorepo), 0002 (Clean Architecture + DDD), 0004 (secretos JWT)
**Puerto dev:** 8081

## Responsabilidad
Autenticación de usuarios (login), emisión y firma de JWT (RS256), seeder de
usuarios de prueba.

## Checklist de esta fase
- [x] `pom.xml` (Spring Boot 3.3.4 + Java 21, ver ADR 0006)
- [ ] Estructura de capas: domain / application / infrastructure / interfaces
- [ ] Agregado `Usuario`, VOs `Email`/`Password` (records)
- [ ] `LoginUseCase` con TDD (mocks de `UsuarioRepository`)
- [ ] Endpoint `POST /api/auth/login` → JWT
- [ ] Spring Security 6 (Resource Server, validación RS256)
- [ ] Seeder (`CommandLineRunner`, profile `dev`)
- [ ] `Dockerfile` + `docker-compose.yml` propio (Postgres `auth-db`)
- [ ] Contrato OpenAPI expuesto en `/v3/api-docs`

## No hace (por diseño)
No conoce `user-service`, `post-service` ni `like-service`. No expone Swagger UI
propia (se agrega en el Gateway).
