# ADR 0002 — Clean Architecture + DDD en cada microservicio

## Estado
Aceptado

## Contexto
Spring Boot tiende a filtrar detalles de framework (JPA, HTTP) hasta el corazón de
la lógica de negocio, lo que hace los casos de uso difíciles de testear sin levantar
todo el contexto de Spring.

## Decisión
Cada microservicio de dominio (auth, user, post, like) sigue la misma plantilla de
capas: `domain/` (sin dependencias de Spring) → `application/` (casos de uso, un
`UseCase` por acción, SRP) → `infrastructure/` (adaptadores JPA/RabbitMQ/Security) →
`interfaces/` (controllers REST). Los casos de uso dependen de puertos (interfaces),
nunca de implementaciones concretas (Dependency Inversion).

DDD aporta el vocabulario de límites: cada microservicio es un bounded context, con
sus propios agregados (`Usuario`, `Perfil`, `Publicacion`, `Like`), value objects
inmutables (Java Records) y domain events (`PublicacionCreada`, `LikeRegistrado`).

## Consecuencias
- (+) Los casos de uso se prueban con TDD real: mocks puros, sin Spring context,
  milisegundos por test.
- (+) Los 5 principios SOLID tienen un lugar concreto donde aplicarse (ver plan,
  sección 4.3).
- (-) Más carpetas y boilerplate por servicio que un CRUD directo con Spring MVC —
  aceptado como costo de mantenibilidad y testabilidad.

## Referencias
PeriBook-Plan-Arquitectura.md, secciones 4.1–4.4
