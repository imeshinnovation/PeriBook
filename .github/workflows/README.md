# CI/CD Workflows — PeriBook

## Workflows

| Archivo | Disparador | Qué hace |
|---|---|---|
| `ci-build.yml` | Push/PR a main | Build + test (7 Maven + Angular + Gradle) |
| `snyk-security.yml` | Push + semanal | Snyk en dependencias (Maven/NPM/Gradle) + SAST |
| `trivy-scan.yml` | Push + semanal | Trivy en imágenes Docker (CRITICAL/HIGH) |
| `ci-full-pipeline.yml` | Push a main | Pipeline completo: Build → Test → Snyk → Docker → Trivy |

## Secretos requeridos

| Secreto | Descripción |
|---|---|
| `SNYK_TOKEN` | Token de API de Snyk (gratuito en snyk.io) |
| `GITHUB_TOKEN` | Automático — para login en GHCR y subir SARIF |

## Flujo completo

```
Push a main
  │
  ├─▶ ci-build.yml
  │     ├─ Backend (7 Maven jobs) → test
  │     ├─ Frontend (npm ci + build)
  │     └─ QA-BDD (Gradle build)
  │
  └─▶ ci-full-pipeline.yml
        ├─ Build & Test
        ├─ Snyk (deps + code)
        ├─ Docker build & push (GHCR)
        ├─ Trivy scan (images)
        └─ Notify
```

Los escaneos de Snyk y Trivy se ejecutan sobre cada servicio usando una matriz
de GitHub Actions, permitiendo que cada microservicio sea analizado
de forma independiente en paralelo.
<!-- 2026-07-09 -->
