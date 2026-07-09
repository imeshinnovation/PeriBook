# PeriBook

Red social tipo Facebook — microservicios en monorepo, Clean Architecture + DDD,
Java 21, Spring Boot 3.3.4, Angular 19, RabbitMQ, Docker Compose / Swarm.

## Estado del proyecto ✅

| Fase | Componente | Estado | Tests | Commit |
|---|---|---|---|---|
| 0 | Bootstrap (monorepo + git) | ✅ | — | `18db41e` |
| 1 | `auth-service` | ✅ | 22/22 | `00145f5` |
| 2 | `api-gateway` + `bff-web` (esqueleto) | ✅ | 4/4 | `3a6ee22` |
| 3 | `user-service` | ✅ | 6/6 | `5be1440` |
| 4 | `post-service` | ✅ | 6/6 | `e4436b4` |
| 5 | `like-service` | ✅ | 4/4 | `45e304d` |
| 6 | `bff-web` (completado) | ✅ | 2/2 | `bb68562` |
| 7 | `realtime-service` | ✅ | 1/1 | `9763c17` |
| 8 | `frontend` (Angular) | ✅ | — | `1701f0b` |
| 9 | `qa-bdd` (Serenity BDD) | ✅ | — | `bc7972e` |
| 10 | CI (Snyk + Trivy) | ✅ | — | `f0af92f` |
| 11 | Docker Swarm (infra) | ✅ | — | `b50969a` |
| 12 | Entregables finales | ✅ | — | `525b3d1` |

**Total: 44 tests unitarios backend + 15 escenarios BDD + 4 workflows CI**

## Servicios

| Servicio | Puerto | Stack | DB propia |
|---|---|---|---|
| `api-gateway` | 8080 | Spring Cloud Gateway, WebFlux, JWT RS256 | ❌ |
| `auth-service` | 8081 | Spring Boot MVC, JPA, Postgres, JJWT | `auth-db:5432` |
| `user-service` | 8082 | Spring Boot MVC, JPA, Postgres | `user-db:5432` |
| `post-service` | 8083 | Spring Boot MVC, JPA, Postgres, RabbitMQ publisher, PL/pgSQL | `post-db:5432` |
| `like-service` | 8084 | Spring Boot MVC, JPA, Postgres, RabbitMQ publisher, PL/pgSQL | `like-db:5432` |
| `realtime-service` | 8085 | WebSocket STOMP/SockJS, RabbitMQ consumer | ❌ |
| `bff-web` | 8086 | WebFlux, WebClient, Resilience4j Circuit Breaker | ❌ |
| `frontend` | 4200 | Angular 19 standalone, NgRx SignalStore, SockJS/STOMP | ❌ |
| `qa-bdd` | — | Serenity BDD 4.2.6, Cucumber 7.17, REST Assured | ❌ |

## Arquitectura

Cada microservicio de dominio sigue Clean Architecture + DDD (ADR 0002):
```
domain/ → application/ → infrastructure/ → interfaces/
```
- `domain/`: sin dependencias de Spring. Agregados, VOs (Java Records), puertos (interfaces)
- `application/`: casos de uso (SRP: uno por acción), dependen solo de puertos
- `infrastructure/`: adaptadores JPA, RabbitMQ, Security
- `interfaces/`: controladores REST, DTOs

## Decisiones de arquitectura (ADR)

- [0001 — Monorepo con proyectos independientes](docs/adr/0001-monorepo.md)
- [0002 — Clean Architecture + DDD](docs/adr/0002-clean-architecture-ddd.md)
- [0003 — BFF separado del Gateway](docs/adr/0003-bff.md)
- [0004 — Docker Swarm, secretos, réplica única](docs/adr/0004-docker-swarm-secrets.md)
- [0005 — Serenity BDD + Gradle](docs/adr/0005-serenity-bdd-gradle.md)
- [0006 — Plantilla de pom.xml (no parent local)](docs/adr/0006-pom-strategy.md)

## Desarrollo local

### Requisitos
- Docker Compose ≥ 2.20 (para `include:`)
- Java 21 + Maven 3.9 (para desarrollo fuera de Docker)
- Node.js 24 + npm (para frontend fuera de Docker)

### Levantar todo el sistema
```bash
docker compose up --build
```

Esto levanta 13 contenedores: 8 apps + 4 Postgres + RabbitMQ.

### Levantar un solo servicio
```bash
cd auth-service && docker compose up
```

### Ejecutar tests
```bash
for svc in auth-service user-service post-service like-service \
           api-gateway bff-web realtime-service; do
  mvn -f $svc/pom.xml test -B
done
```

### Usuarios de prueba
| Email | Contraseña | Alias |
|---|---|---|
| `ana@peribook.com` | `secreto123` | `ana_writer` |
| `carlos@peribook.com` | `secreto123` | `carlos_reader` |
| `admin@peribook.com` | `admin1234` | `admin_root` |

## Despliegue en producción (Swarm)

```bash
./infra/deploy.sh              # Build 8 imágenes + bootstrap + deploy
./infra/verify-deployment.sh   # Validar 9 réplicas en 1/1
```

## CI/CD

Workflows en `.github/workflows/`:

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
├── auth-service/            ← Autenticación JWT RS256
├── api-gateway/             ← Spring Cloud Gateway (WebFlux)
├── bff-web/                 ← Backend For Frontend (WebFlux + Resilience4j)
├── user-service/            ← Perfiles de usuario
├── post-service/            ← Publicaciones + RabbitMQ + PL/pgSQL
├── like-service/            ← Likes + RabbitMQ + PL/pgSQL (idempotente)
├── realtime-service/        ← WebSocket STOMP + RabbitMQ consumer
├── frontend/                ← Angular SPA (standalone + SignalStore)
├── qa-bdd/                  ← Serenity BDD + Cucumber (15 escenarios)
├── infra/                   ← Docker Swarm (stack + bootstrap + deploy + verify)
│   ├── docker-stack.yml
│   ├── bootstrap-swarm.sh
│   ├── deploy.sh
│   ├── verify-deployment.sh
│   ├── postman/             ← Postman collection (11 endpoints)
│   ├── secrets/             ← Script generador de secretos locales
│   └── seed/                ← SQL seeds para las 4 bases de datos
├── docker-compose.yml       ← Orquestador raíz (include: 8 servicios)
├── .env.example             ← Plantilla de variables de entorno
├── PHASES.md                ← Guía de fases de desarrollo (0 → 12)
├── README.md                ← Este archivo
└── DELIVERABLES.md          ← Checklist de entregables
```

## Notas

- **Database-per-Service**: cada microservicio con DB propia es dueño de su schema (ADR 0001).
  Dentro de la red Docker, el puerto es siempre **5432** (el puerto interno del contenedor
  Postgres, no el mapeado al host).
- **Independencia real**: ningún servicio importa código de otro. Eventos de dominio
  compartidos por contrato JSON, nunca por librería Java.
- **Tiempo real**: RabbitMQ exchange `peribook.events` + WebSocket STOMP. Los mensajes
  viajan como String JSON (sin `__TypeId__`) para que publicadores y consumidores
  puedan tener DTOs en paquetes distintos.
- **Réplica única**: `realtime-service` usa `deploy.replicas: 1` porque mantiene
  sesiones WebSocket en memoria (ADR 0004).
- **Idempotencia**: `DarLikeUseCase` no lanza excepción en likes duplicados —
  devuelve el like existente sin publicar evento.
