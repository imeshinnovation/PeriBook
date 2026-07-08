# ADR 0005 — Serenity BDD + Gradle en proyecto QA independiente (qa-bdd)

## Estado
Aceptado

## Contexto
TDD (dentro de cada microservicio) prueba unidades de lógica aisladas, pero no
responde si el sistema integrado se comporta como el negocio espera. Cucumber por
sí solo ejecuta escenarios pero no genera evidencia (capturas, request/response)
sin instrumentación manual.

## Decisión
Las pruebas de aceptación (BDD) viven en un proyecto QA separado dentro del
monorepo, `qa-bdd/`, con Gradle + Serenity BDD, y prueban el sistema de **caja
negra, ya integrado** (nunca contra mocks), a dos niveles:
- API: REST Assured contra el BFF/Gateway.
- UI: patrón Screenplay + Selenium WebDriver contra el frontend real.

Serenity genera automáticamente un reporte HTML de "living documentation" con
capturas de pantalla, payloads de request/response, y trazabilidad por etiqueta de
requisito (`@login`, `@feed`, `@likes-tiempo-real`, `@perfil`).

## Consecuencias
- (+) Evidencia de pruebas sin trabajo manual adicional — el reporte es el
  entregable de calidad.
- (+) `qa-bdd` no depende del código interno de ningún microservicio (caja negra
  real, como lo haría un QA senior).
- (-) Herramienta y curva de aprendizaje adicional frente a Cucumber "pelado".

## Referencias
PeriBook-Plan-Arquitectura.md, sección 6
