# PeriBook — Checklist de entregables (Fase 12)

## 1. Código fuente ✅

- [x] Monorepo con 9 servicios autocontenidos
- [x] Clean Architecture + DDD en cada microservicio de dominio
- [x] SOLID: un UseCase por acción, Dependency Inversion en todos los puertos
- [x] TDD: 45 tests unitarios con mocks puros (sin contexto Spring)
- [x] PL/pgSQL: 2 procedures mínimos (`sp_crear_publicacion`, `sp_registrar_like`)
- [x] RabbitMQ: 2 publishers (post, like) + 1 consumer (realtime)
- [x] WebSocket: STOMP/SockJS en realtime-service, proxy vía Gateway
- [x] Circuit Breaker: Resilience4j en bff-web con fallback a like-service
- [x] Frontend Angular 19: standalone components + lazy loading + SignalStore + SockJS
- [x] 6 ADRs documentando cada decisión arquitectónica
- [x] Docker Compose para desarrollo local (un compose por servicio + raíz con include:)

## 2. Infraestructura ✅

- [x] `docker-stack.yml` con los 9 servicios (8 app + RabbitMQ)
- [x] Red overlay cifrada (`encrypted: "true"`)
- [x] Secretos en Docker Swarm (JWT, Postgres, RabbitMQ — nunca en env vars)
- [x] `bootstrap-swarm.sh` idempotente (init swarm + labels + llaves JWT + secretos)
- [x] `deploy.sh` (build 8 imágenes + bootstrap + deploy)
- [x] `verify-deployment.sh` (réplicas 1/1 + health checks + flujo completo)
- [x] `update_config.order: start-first` + `failure_action: rollback` en todos

## 3. CI/CD ✅

- [x] `ci-build.yml`: Build + test en matrix de 7 backends + Angular + Gradle
- [x] `snyk-security.yml`: Snyk en dependencias (Maven/NPM/Gradle) + SAST (Snyk Code)
- [x] `trivy-scan.yml`: Trivy en 8 imágenes Docker (CRITICAL/HIGH) con SARIF
- [x] `ci-full-pipeline.yml`: Pipeline completo Build→Test→Snyk→Docker→Trivy→Notify

## 4. QA ✅

- [x] `qa-bdd/` con Serenity BDD 4.2.6 + Cucumber 7.17.0
- [x] 4 feature files, 15 escenarios Gherkin con tags (@login, @feed, @perfil, @likes-tiempo-real)
- [x] Step definitions Java para API (REST Assured) y UI (Screenplay)
- [x] Escenario de dos pestañas para likes en tiempo real (`@two-tabs`)
- [x] `serenity.conf` con entornos `default` (localhost) y `docker` (Swarm)
- [x] Reporte HTML de evidencias en `target/site/serenity/`

## 5. Documentación ✅

- [x] `PeriBook-Plan-Arquitectura.md` — Plan técnico completo (45K)
- [x] `PeriBook-Memoria-Tecnica.docx` — Documento narrativo para evaluador
- [x] `PHASES.md` — Guía de fases de desarrollo (0→12)
- [x] `README.md` — Tabla de estado con todo en ✅
- [x] `DELIVERABLES.md` — Este archivo
- [x] 6 ADRs en `docs/adr/`
- [x] Cada microservicio tiene su propio README con checklist, responsabilidad y puerto

## 6. Demo y evidencias 📋

- [ ] **Video demo** grabado contra el stack ya desplegado
      - Flujo: login → feed → crear publicación → like → perfil
      - Mostrar que el contador de likes se actualiza en vivo (dos pestañas)
      - URL: `http://localhost:4200` (frontend) + `http://localhost:8080/docs` (Swagger)
- [ ] **Evidencias de qa-bdd**: Ejecutar `./gradlew clean test aggregate` en `qa-bdd/`
      - Adjuntar screenshots de `qa-bdd/target/site/serenity/index.html`
- [ ] **Evidencias de Snyk/Trivy**: Screenshots de los resultados en GitHub Actions
      - O ejecutar localmente: `snyk test --all-projects` y `trivy image peribook/auth-service:1.0.0`
- [ ] **Evidencias de Swagger**: Screenshot de `http://localhost:8080/docs` con los 5 grupos de API
- [ ] **Evidencias de RabbitMQ**: Screenshot de `http://localhost:15672` mostrando las colas `realtime.feed` y `realtime.likes`

## 7. Archivos adjuntos al evaluador

| Archivo | Formato | Descripción |
|---|---|---|
| `PeriBook-Memoria-Tecnica.docx` | DOCX | Documento narrativo (70% humano / 30% técnico) |
| `PeriBook-Plan-Arquitectura.md` | Markdown | Plan técnico detallado |
| `DELIVERABLES.md` | Markdown | Este checklist |
| `README.md` | Markdown | Estado del proyecto |
| Evidencias QA | PNG/HTML | Screenshots del reporte Serenity |
| Evidencias seguridad | PNG | Snyk + Trivy scans |
| Video demo | MP4 | Grabación del flujo completo |

## Nota sobre la memoria técnica

`PeriBook-Memoria-Tecnica.docx` se redactó antes de la implementación. Si algo cambió
significativamente durante el desarrollo (por ejemplo, el número final de fases, la
versión exacta de Angular, o detalles concretos de la configuración de Resilience4j),
actualiza ese documento para reflejar lo realmente construido.

<!-- Last preview delivery: 2026-07-09 -->
<!-- 2026-07-09 -->
