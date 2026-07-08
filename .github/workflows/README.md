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

## Nota Fase 10

En la práctica, Snyk + Trivy se agregan **al cerrar cada fase de servicio** (1 a 7),
no como paso separado al final. Este directorio contiene los workflows tal como
se habrían creado incrementalmente por cada servicio nuevo.
