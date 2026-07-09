# qa-bdd

**Framework:** Serenity BDD 4.2.6 + Cucumber 7.17 | **Build:** Gradle 8.10

## Responsabilidad

Pruebas de aceptación de caja negra contra el sistema completo. No conoce
los detalles internos de los microservicios — solo se comunica con el API
Gateway como lo haría un cliente real.

## Estructura de pruebas

```
src/test/
├── resources/
│   ├── features/
│   │   ├── login.feature              ← Autenticación (4 escenarios)
│   │   ├── feed.feature               ← Publicaciones (5 escenarios)
│   │   ├── profile.feature            ← Perfiles (2 escenarios)
│   │   └── realtime_likes.feature     ← Likes en tiempo real (4 escenarios)
│   └── serenity.conf                  ← Configuración de entornos
└── java/com/peribook/qa/
    ├── RunCucumberTest.java           ← Test runner JUnit 5 + Cucumber
    ├── steps/TestContext.java         ← Estado compartido entre steps
    └── steps/*Steps.java              ← Step definitions en español
```

## Escenarios cubiertos

| Feature | Escenarios | Tipos de prueba |
|---|---|---|
| Login | 4 | Happy path, credenciales inválidas, email inválido |
| Feed | 5 | Obtener feed, crear publicación, validaciones, seguridad |
| Perfil | 2 | Obtener perfil, perfil inexistente |
| Likes | 4 | Dar like, like duplicado (idempotencia), WebSocket, dos pestañas |

## Cómo ejecutar

```bash
cd qa-bdd

# Contra backend local
./gradlew clean test aggregate -Dapi.base.url=http://localhost:8080

# Reporte de evidencias
open target/site/serenity/index.html
```

## Stack interno

- **Serenity BDD** genera reportes HTML con capturas y payloads HTTP
- **Cucumber** con step definitions en español (`@Dado`, `@Cuando`, `@Entonces`)
- **REST Assured** para pruebas de API (caja negra, sin mocks)
- **Screenplay** para pruebas de UI con Selenium WebDriver
- **JUnit 5** con Cucumber Platform Engine
