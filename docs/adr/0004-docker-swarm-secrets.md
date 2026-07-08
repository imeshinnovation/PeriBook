# ADR 0004 — Docker Swarm con secretos nativos y réplica única por servicio

## Estado
Aceptado

## Contexto
Docker Compose plano deja secretos (contraseñas, llaves JWT) visibles en texto
plano vía `docker inspect` o variables de entorno. Además, un `docker compose up`
suelto no garantiza que un contenedor caído se reinicie ni ofrece despliegues sin
downtime.

## Decisión
Compose sigue siendo el flujo de **desarrollo local** (build en caliente por
servicio). Para un despliegue más cercano a producción, se usa **Docker Swarm**:
`docker secret` para JWT/contraseñas de DB/RabbitMQ (cifrados en el Raft log,
montados en `/run/secrets/` como archivo, nunca como env var), y un
`infra/docker-stack.yml` con `deploy.replicas: 1` por servicio.

Réplica única es una decisión honesta para el alcance actual: `realtime-service`
mantiene sesiones WebSocket en memoria, y escalar a más de una réplica sin un
backplane compartido (RabbitMQ como relay o Redis pub/sub) rompería la entrega de
eventos a algunos clientes. Escalar `post-service`/`like-service` (sin estado en
memoria) sí sería trivial más adelante.

## Consecuencias
- (+) Secretos reales, no variables de entorno disfrazadas.
- (+) `update_config.order: start-first` permite despliegue sin downtime.
- (+) Red overlay cifrada entre nodos.
- (-) `docker stack deploy` no construye imágenes — requiere un paso de
  build+push en CI antes de desplegar (ya integrado con el job de Snyk/Trivy).
- (-) `realtime-service` no escala horizontalmente todavía — documentado como
  mejora futura, no como limitación oculta.

## Referencias
PeriBook-Plan-Arquitectura.md, sección 2.4; infra/docker-stack.yml;
infra/bootstrap-swarm.sh
