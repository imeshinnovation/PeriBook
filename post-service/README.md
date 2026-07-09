# post-service

**Puerto:** 8083 | **Base de datos:** PostgreSQL (`post-db:5432`) | **Mensajería:** RabbitMQ

## Responsabilidad

Crea y lista publicaciones. Cada vez que se crea una publicación, emite un
evento `PublicacionCreada` a RabbitMQ para que el servicio de tiempo real
lo transmita a los clientes conectados.

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/posts?limite=20` | Lista las publicaciones más recientes |
| `POST` | `/api/posts` | Crea una publicación. Recibe `{"contenido"}` |

## Stack interno

- **Spring Boot 3** con Spring MVC
- **Spring Security 6** con OAuth2 Resource Server
- **Spring Data JPA** con PostgreSQL
- **Spring AMQP** (RabbitMQ) para publicación de eventos
- **PL/pgSQL** — stored procedure `sp_crear_publicacion` con validación
- **Testcontainers** para pruebas de integración

## Evento de dominio

Al crear una publicación, se emite al exchange `peribook.events` con
routing key `publicacion.creada`:

```json
{
  "publicacionId": "uuid",
  "autorId": "uuid",
  "contenido": "texto",
  "creadaEn": "2026-07-09T00:00:00Z"
}
```

## Desarrollo

```bash
docker compose up          # Levanta el servicio + Postgres + RabbitMQ
mvn test                   # 6 tests unitarios + PL/pgSQL
```
<!-- 2026-07-09 -->
