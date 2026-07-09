# realtime-service

**Puerto:** 8085 | **Base de datos:** No (sin estado) | **Mensajería:** RabbitMQ

## Responsabilidad

Escucha eventos de dominio desde RabbitMQ (`PublicacionCreada` y `LikeRegistrado`)
y los retransmite en vivo a los navegadores conectados vía WebSocket usando
el protocolo STOMP sobre SockJS.

## Canales WebSocket

| Canal | Evento | Qué recibe el cliente |
|---|---|---|
| `/topic/feed` | `PublicacionCreada` y `LikeRegistrado` | Cualquier cambio en el feed |

## Stack interno

- **Spring Boot 3** con Spring MVC
- **Spring WebSocket** con STOMP sobre SockJS
- **Spring AMQP** (RabbitMQ) — consumidor de eventos
- Broker de mensajes en memoria (no se usa broker externo para STOMP)

## Flujo de tiempo real

```
post-service ──▶ RabbitMQ ──▶ realtime-service ──▶ WebSocket ──▶ navegador
                  exchange          consumidor         STOMP         cliente
like-service ──▶ peribook.events    @RabbitListener    /topic/feed   SockJS
```

Las colas `realtime.feed` y `realtime.likes` son durables (sobreviven
reinicios de RabbitMQ). El servicio es consciente de que con réplica única
las sesiones WebSocket viven en memoria (no escala horizontalmente sin un
backplane compartido).

## Desarrollo

```bash
docker compose up          # Levanta el servicio + RabbitMQ
mvn test                   # 1 test de health check
```
<!-- 2026-07-09 -->
<!-- 2026-07-09 -->
