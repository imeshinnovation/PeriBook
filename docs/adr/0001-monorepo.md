# ADR 0001 — Monorepo con microservicios independientes (no multi-repo)

## Estado
Aceptado

## Contexto
La prueba técnica pide arquitectura de microservicios. Multi-repo es la forma más
"pura" de garantizar independencia, pero complica la evaluación (clonar 8 repos).

## Decisión
Un único repositorio Git (`peribook/`). Cada microservicio vive en su propio
directorio de nivel raíz, con su propio `pom.xml`/`build.gradle`, `Dockerfile` y
`docker-compose.yml`. Ningún directorio importa código fuente de otro. Lo que se
comparte entre servicios (eventos de dominio) se resuelve por contrato JSON, no por
librería Java compartida.

## Consecuencias
- (+) Un solo `git clone` para ver todo el sistema.
- (+) Cada servicio sigue siendo desplegable/testeable de forma aislada
  (`cd auth-service && docker compose up`).
- (+) `docker-compose.yml` raíz usa `include:` para componer todo en un comando.
- (-) Requiere disciplina para no crear dependencias cruzadas de código —
  mitigado con revisión de PR y con que cada servicio tenga su propio pipeline de CI.

## Referencias
PeriBook-Plan-Arquitectura.md, sección 2.1
