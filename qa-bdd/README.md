# qa-bdd

**Fase:** 8 (contra el sistema completo ya levantado)
**ADR relevantes:** 0005 (Serenity BDD + Gradle)

## Responsabilidad
Pruebas de aceptación de caja negra, con evidencias automáticas (Serenity).

## Checklist de esta fase
- [x] `build.gradle` con Serenity + Cucumber + REST Assured + Screenplay
- [x] Features API (`@login`, `@perfil`, `@feed`, `@likes-tiempo-real`)
- [x] Features UI (Screenplay, incluye el escenario de dos pestañas)
- [x] `./gradlew clean test aggregate` genera reporte en `target/site/serenity/`
