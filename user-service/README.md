# user-service

**Puerto:** 8082 | **Base de datos:** PostgreSQL (`user-db:5432`)

## Responsabilidad

Gestiona los perfiles de usuario: nombres, apellidos, alias y fecha de
nacimiento. Cada perfil está vinculado al identificador de usuario del
servicio de autenticación.

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/users/{id}` | Obtiene el perfil por ID. Requiere JWT |

## Stack interno

- **Spring Boot 3** con Spring MVC
- **Spring Security 6** con OAuth2 Resource Server (valida JWT)
- **Spring Data JPA** con PostgreSQL
- **MapStruct** para mapeo DTO ↔ dominio
- **Testcontainers** para pruebas de integración

## Organización del código

- `domain/` — agregado `Perfil`, value object `Email`, puerto `PerfilRepository`
- `application/` — caso de uso `ObtenerPerfilUseCase`
- `infrastructure/` — implementación JPA, configuración de seguridad, seeder
- `interfaces/` — controlador REST `UserController` y DTOs

## Desarrollo

```bash
docker compose up          # Levanta el servicio + su Postgres (puerto 5433 en host)
mvn test                   # 6 tests unitarios
```
<!-- 2026-07-09 -->
