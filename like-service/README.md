# like-service

**Puerto:** 8084 | **Base de datos:** PostgreSQL (`like-db:5432`) | **Mensajería:** RabbitMQ

## Responsabilidad

Registra y cuenta likes en publicaciones. Un mismo usuario no puede dar like
dos veces a la misma publicación: el sistema lo detecta y responde de forma
idempotente sin lanzar errores. Cada like nuevo emite un evento
`LikeRegistrado` a RabbitMQ.

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/likes?publicacionId={id}` | Da like a una publicación |
| `GET` | `/api/likes/{id}/count` | Devuelve el contador de likes de una publicación |

## Stack interno

- **Spring Boot 3** con Spring MVC
- **Spring Security 6** con OAuth2 Resource Server
- **Spring Data JPA** con PostgreSQL (constraint único en `publicacionId + usuarioId`)
- **Spring AMQP** (RabbitMQ) para publicación de eventos
- **PL/pgSQL** — stored procedure `sp_registrar_like` con `ON CONFLICT DO NOTHING`
- **Testcontainers** para pruebas de integración

## Idempotencia

Si un usuario ya dio like a una publicación, una segunda petición:
- Devuelve HTTP 200 (en lugar de 201)
- No duplica el registro en la base de datos
- No vuelve a publicar el evento en RabbitMQ

## Desarrollo

```bash
docker compose up          # Levanta el servicio + Postgres + RabbitMQ
mvn test                   # 4 tests unitarios (incluye caso de like duplicado)
```
