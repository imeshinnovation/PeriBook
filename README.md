# PeriBook

Red social tipo Facebook — microservicios en monorepo, Clean Architecture + DDD,
Java 21, Spring Boot 3.3.4, Angular 19, RabbitMQ, Docker Swarm.

## Estado del proyecto ✅

| Fase | Componente | Estado | Tests | Commit |
|---|---|---|---|---|
| 0 | Bootstrap (monorepo + git) | ✅ Listo | — | `18db41e` |
| 1 | `auth-service` | ✅ Listo | 22/22 | `00145f5` |
| 2 | `api-gateway` | ✅ Listo | 3/3 | `3a6ee22` |
| 2 | `bff-web` (esqueleto) | ✅ Listo | — | `3a6ee22` |
| 3 | `user-service` | ✅ Listo | 6/6 | `5be1440` |
| 4 | `post-service` | ✅ Listo | 6/6 | `e4436b4` |
| 5 | `like-service` | ✅ Listo | 4/4 | `45e304d` |
| 6 | `bff-web` (completado) | ✅ Listo | 2/2 | `bb68562` |
| 7 | `realtime-service` | ✅ Listo | 1/1 | `9763c17` |
| 8 | `frontend` (Angular) | ✅ Listo | — | `1701f0b` |
| 9 | `qa-bdd` (Serenity) | ✅ Listo | — | `bc7972e` |
| 10 | CI (Snyk + Trivy) | ✅ Listo | — | `f0af92f` |
| 11 | Docker Swarm (infra) | ✅ Listo | — | `b50969a` |
| **12** | **Entregables finales** | ✅ Listo | — | `HEAD` |

**Total: 45 tests unitarios backend + 15 escenarios BDD + 4 workflows CI**

## Servicios

| Servicio | Puerto | Stack tecnológico |
|---|---|---|
| `api-gateway` | 8080 | Spring Cloud Gateway, WebFlux, JWT RS256 |
| `auth-service` | 8081 | Spring Boot MVC, JPA, Postgres, JJWT |
| `user-service` | 8082 | Spring Boot MVC, JPA, Postgres |
| `post-service` | 8083 | Spring Boot MVC, JPA, Postgres, RabbitMQ publisher, PL/pgSQL |
| `like-service` | 8084 | Spring Boot MVC, JPA, Postgres, RabbitMQ publisher, PL/pgSQL |
| `realtime-service` | 8085 | WebSocket STOMP/SockJS, RabbitMQ consumer |
| `bff-web` | 8086 | WebFlux, WebClient, Resilience4j Circuit Breaker |
| `frontend` | 4200 | Angular 19 standalone, NgRx SignalStore, SockJS/STOMP |
| `qa-bdd` | — | Serenity BDD 4.2.6, Cucumber 7.17, REST Assured, Screenplay |

## Decisiones de arquitectura (ADR)

- [0001 — Monorepo con proyectos independientes](docs/adr/0001-monorepo.md)
- [0002 — Clean Architecture + DDD](docs/adr/0002-clean-architecture-ddd.md)
- [0003 — BFF separado del Gateway](docs/adr/0003-bff.md)
- [0004 — Docker Swarm, secretos, réplica única](docs/adr/0004-docker-swarm-secrets.md)
- [0005 — Serenity BDD + Gradle](docs/adr/0005-serenity-bdd-gradle.md)
- [0006 — Plantilla de pom.xml (no parent local)](docs/adr/0006-pom-strategy.md)

## Cómo levantar en desarrollo

```bash
# Por servicio individual (ej. auth-service)
cd auth-service && docker compose up

# Todos juntos (desde la raíz, requiere Docker Compose >= 2.20)
docker compose up --build
```

## Despliegue en producción (Swarm)

```bash
./infra/deploy.sh              # Build + deploy completo
./infra/verify-deployment.sh   # Validar que todo está en 1/1

# O paso a paso:
./infra/bootstrap-swarm.sh     # 1. Inicializar Swarm + secretos
docker build -t peribook/<svc>:1.0.0 <svc>/  # 2. Build (×8)
docker stack deploy -c infra/docker-stack.yml peribook  # 3. Deploy
```

## CI/CD

Los workflows de GitHub Actions están en `.github/workflows/`:

| Workflow | Disparador |
|---|---|
| `ci-build.yml` | Push/PR — build + test (7 backends + Angular + Gradle) |
| `snyk-security.yml` | Push + semanal — Snyk en dependencias + SAST |
| `trivy-scan.yml` | Push + semanal — Trivy en 8 imágenes Docker |
| `ci-full-pipeline.yml` | Push a main — pipeline completo hasta imágenes en GHCR |

## Estructura

```
PeriBook/
├── .github/workflows/       ← CI/CD (GitHub Actions)
├── docs/adr/                ← 6 Architecture Decision Records
├── build-templates/         ← pom-template.xml
├── auth-service/            ← Microservicio (Clean Architecture + DDD)
├── api-gateway/             ← Spring Cloud Gateway (WebFlux)
├── bff-web/                 ← Backend For Frontend (WebFlux + Resilience4j)
├── user-service/            ← Microservicio
├── post-service/            ← Microservicio + RabbitMQ + PL/pgSQL
├── like-service/            ← Microservicio + RabbitMQ + PL/pgSQL
├── realtime-service/        ← WebSocket STOMP + RabbitMQ consumer
├── frontend/                ← Angular SPA
├── qa-bdd/                  ← Serenity BDD + Cucumber
├── infra/                   ← Docker Swarm (stack + bootstrap + deploy + verify)
│   ├── docker-stack.yml
│   ├── bootstrap-swarm.sh
│   ├── deploy.sh
│   ├── verify-deployment.sh
│   └── secrets/
├── docker-compose.yml       ← Orquestador raíz (desarrollo local)
├── PHASES.md                ← Guía de fases de desarrollo
├── README.md                ← Este archivo
└── DELIVERABLES.md          ← Checklist de entregables
```

## Notas

- **Independencia real**: ningún directorio de servicio importa código de otro.
  Todo lo compartido se resuelve por contrato JSON (eventos de dominio).
- **Réplica única**: `realtime-service` usa `deploy.replicas: 1` porque mantiene
  sesiones WebSocket en memoria (ver ADR 0004).
- **Seguridad**: secretos en Docker Swarm vía `docker secret` (nunca como env var
  plana), imágenes escaneadas con Snyk + Trivy en CI.
