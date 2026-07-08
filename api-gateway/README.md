# api-gateway

**Fase:** 2
**ADR relevantes:** 0001, 0003 (enruta a bff-web y a los servicios de dominio)
**Puerto dev:** 8080

## Responsabilidad
Único punto de entrada público: enrutamiento, validación JWT, rate limiting,
Swagger agregado en `/docs`, proxy de WebSocket hacia realtime-service.

## Checklist de esta fase
- [x] `RouteLocator`: `/api/auth/**`, `/api/users/**`, `/api/posts/**`,
      `/api/likes/**`, `/bff/**`, `/ws/**`
- [x] Filtro de validación JWT (llave pública, sin volver a golpear auth-service)
- [x] Swagger agregado (`springdoc.swagger-ui.urls[]`) — ver plan sección 4.12
- [x] `Dockerfile` + `docker-compose.yml` propio
