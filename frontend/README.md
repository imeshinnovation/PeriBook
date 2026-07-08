# frontend

**Fase:** 7
**ADR relevantes:** ninguno específico — ver plan, sección 5
**Puerto dev:** 4200

## Responsabilidad
SPA Angular 21, clon visual de Facebook en paleta verde oliva. Habla únicamente
con `api-gateway` (REST y WebSocket).

## Checklist de esta fase
- [ ] Standalone components + lazy loading por feature
- [ ] NgRx SignalStore (AuthStore, FeedStore, ProfileStore)
- [ ] Pantallas: Login, Feed, Perfil
- [ ] `RealtimeService` conectado a `ws://.../ws` (vía Gateway)
- [ ] `Dockerfile` + `docker-compose.yml` propio
