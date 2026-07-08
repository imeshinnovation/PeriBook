# realtime-service

**Fase:** 6 (depende de post-service y like-service ya publicando eventos)
**ADR relevantes:** 0004 (Swarm — sin escalar réplicas por estado en memoria)
**Puerto dev:** 8085

## Responsabilidad
Consume `peribook.events` de RabbitMQ y reenvía por WebSocket (STOMP/SockJS) a
los clientes conectados. Sin base de datos propia.

## Checklist de esta fase
- [ ] Consumer de `PublicacionCreada` y `LikeRegistrado`
- [ ] Canal `/topic/feed` y `/topic/publicacion.{id}.likes`
- [ ] `Dockerfile` + `docker-compose.yml` propio (sin DB)

## Nota
No escala horizontalmente sin backplane compartido — ver ADR 0004.
