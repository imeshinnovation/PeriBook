# ADR 0006 — Plantilla de `pom.xml` copiada, no parent POM local

## Estado
Aceptado

## Contexto
Queríamos un `pom.xml` "padre" con las dependencias comunes (Spring Boot 3.x,
Java 21, springdoc, MapStruct, testing) para que cada microservicio no empiece
de cero. La forma habitual de hacer esto en Maven es un `<parent>` con
`relativePath` apuntando a un POM en un directorio compartido.

El problema: el `Dockerfile` de cada servicio (ADR 0001) construye con contexto
limitado a su propio directorio (`docker build .` desde `auth-service/`). Un
parent POM local, un nivel arriba, no estaría dentro de ese contexto — el build
fallaría dentro de Docker aunque funcionara en la máquina del desarrollador.
Cambiar el contexto de build a la raíz del repo (`docker build -f
auth-service/Dockerfile .`) es posible, pero rompe la promesa de "cada
directorio es 100% autosuficiente" que ya se validó en el ADR 0001 y en el plan
de arquitectura.

## Decisión
Cada `pom.xml` hereda directamente de `spring-boot-starter-parent` (coordenadas
públicas, sin `relativePath`, se resuelve desde Maven Central en cualquier
contexto de build). Las dependencias comunes se mantienen en una **plantilla**
(`build-templates/pom-template.xml`) que se copia y ajusta al crear cada
servicio nuevo — no se hereda en tiempo de build, se copia en tiempo de
scaffolding.

## Consecuencias
- (+) Cada `pom.xml` es autosuficiente: `docker build .` funciona desde
  cualquier directorio de servicio sin contexto adicional.
- (+) Las versiones base (Spring Boot, springdoc, MapStruct) quedan
  documentadas en un solo lugar (la plantilla) para copiar/pegar consistente.
- (-) Actualizar una versión requiere tocar el `pom.xml` de cada servicio
  (no hay herencia automática) — aceptado como costo menor frente a la
  independencia real de build. Si el proyecto creciera mucho, la alternativa
  sería publicar un BOM propio en un registro (GitHub Packages) e importarlo
  por coordenadas — no por archivo local.

## Referencias
build-templates/pom-template.xml; auth-service/pom.xml; ADR 0001
