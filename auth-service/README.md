# auth-service

**Fase:** 1 (primera a construir — ver roadmap)
**ADR relevantes:** 0001 (monorepo), 0002 (Clean Architecture + DDD), 0004 (secretos JWT)
**Puerto dev:** 8081

## Responsabilidad
Autenticación de usuarios (login), emisión y firma de JWT (RS256), seeder de
usuarios de prueba.

## Checklist de esta fase
- [x] `pom.xml` (Spring Boot 3.3.4 + Java 21, ver ADR 0006)
- [x] Estructura de capas: domain / application / infrastructure / interfaces
- [x] Agregado `Usuario`, VOs `Email`/`Password` (records)
- [x] `LoginUseCase` con TDD (mocks de `UsuarioRepository`) — 22 tests ✅
- [x] Endpoint `POST /api/auth/login` → JWT
- [x] Spring Security 6 (Resource Server, validación RS256)
- [x] Seeder (`CommandLineRunner`, profile `dev`)
- [x] `Dockerfile` + `docker-compose.yml` propio (Postgres `auth-db`)
- [x] Contrato OpenAPI expuesto en `/v3/api-docs`

## No hace (por diseño)
No conoce `user-service`, `post-service` ni `like-service`. No expone Swagger UI
propia (se agrega en el Gateway).
