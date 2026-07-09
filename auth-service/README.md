# auth-service

**Puerto:** 8081 | **Base de datos:** PostgreSQL (`auth-db:5432`)

## Responsabilidad

Gestiona la autenticación de usuarios: recibe credenciales, valida contra
la base de datos y emite tokens JWT firmados con RSA 256 bits.

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/auth/login` | Inicia sesión. Recibe `{"email", "password"}`, devuelve `{"token", "userId", "alias"}` |

## Stack interno

- **Spring Boot 3** con Spring MVC
- **Spring Security 6** con OAuth2 Resource Server (validación JWT)
- **Spring Data JPA** con PostgreSQL
- **JJWT 0.12** para firma y validación de tokens
- **MapStruct** para mapeo DTO ↔ dominio
- **Testcontainers** para pruebas de integración con Postgres real

## Organización del código

El servicio sigue una arquitectura de capas con dominio aislado:

- `domain/` — lógica de negocio pura, sin dependencias de Spring. Contiene el
  agregado `Usuario`, los value objects `Email` y `Password`, y el puerto
  `UsuarioRepository`
- `application/` — caso de uso `LoginUseCase`, puerto `JwtService`
- `infrastructure/` — implementaciones JPA, seguridad (filtros JWT, llaves RSA),
  y el seeder de datos de prueba
- `interfaces/` — controlador REST `AuthController` y DTOs

## Desarrollo

```bash
# Dentro de auth-service/
docker compose up          # Levanta el servicio + su Postgres
mvn test                   # 22 tests unitarios (mocks sin Spring)
```
<!-- 2026-07-09 -->
