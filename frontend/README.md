# frontend

**Fase:** 7
**ADR relevantes:** ninguno específico — ver plan, sección 5
**Puerto dev:** 4200

## Responsabilidad
SPA Angular 21, clon visual de Facebook en paleta verde oliva. Habla únicamente
con `api-gateway` (REST y WebSocket).

## Checklist de esta fase
- [x] Standalone components + lazy loading por feature
- [x] NgRx SignalStore (AuthStore, FeedStore)
- [x] Pantallas: Login, Feed, Perfil
- [x] `RealtimeService` conectado a `/ws` vía Gateway (SockJS + STOMP)
- [x] `Dockerfile` + `docker-compose.yml` propio (nginx + Angular)
