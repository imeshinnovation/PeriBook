# PeriBook

Red social tipo Facebook — microservicios en monorepo, Clean Architecture + DDD,
Java 21, Angular 21. Ver decisiones de arquitectura en `docs/adr/`.

## Estado del proyecto

| Fase | Componente | Estado |
|---|---|---|
| 1 | `auth-service` | 🔲 Pendiente |
| 2 | `api-gateway` | 🔲 Pendiente |
| 2 | `bff-web` (esqueleto) | 🔲 Pendiente |
| 3 | `user-service` | 🔲 Pendiente |
| 4 | `post-service` | 🔲 Pendiente |
| 5 | `like-service` | 🔲 Pendiente |
| 6 | `realtime-service` | 🔲 Pendiente |
| 7 | `frontend` | 🔲 Pendiente |
| 8 | `qa-bdd` | 🔲 Pendiente |
| 9 | `infra/docker-stack.yml` (Swarm) | ✅ Definido, pendiente de probar contra servicios reales |

Actualiza esta tabla a `🟡 En progreso` / `✅ Listo` al cerrar cada fase — es la
forma más barata de que cualquiera (humano o el índice de memoria) sepa en qué
va el proyecto sin leer código.

## Decisiones de arquitectura (ADR)

- [0001 — Monorepo con proyectos independientes](docs/adr/0001-monorepo.md)
- [0002 — Clean Architecture + DDD](docs/adr/0002-clean-architecture-ddd.md)
- [0003 — BFF separado del Gateway](docs/adr/0003-bff.md)
- [0004 — Docker Swarm, secretos, réplica única](docs/adr/0004-docker-swarm-secrets.md)
- [0005 — Serenity BDD + Gradle](docs/adr/0005-serenity-bdd-gradle.md)
- [0006 — Plantilla de pom.xml (no parent local)](docs/adr/0006-pom-strategy.md)

## Cómo levantar lo que ya exista

```bash
docker compose up --build
```

(El `include:` de cada servicio se activa en `docker-compose.yml` a medida que
el servicio tenga su propio `docker-compose.yml` — ver el comentario en ese
archivo.)

## Despliegue (Swarm)

```bash
./infra/bootstrap-swarm.sh
docker stack deploy -c infra/docker-stack.yml peribook
```

## Estructura

Cada directorio de nivel raíz es un microservicio autocontenido — ver su propio
`README.md` para responsabilidad, puerto y checklist de esa fase.
