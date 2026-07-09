# Postman — PeriBook API Collection

## Cómo usar

1. Abre Postman → Import → selecciona los dos archivos JSON de esta carpeta:
   - `peribook-collection.json` (colección de endpoints)
   - `peribook-environment.json` (variables de entorno)

2. Selecciona el entorno **"PeriBook — Local Dev"**

3. **Flujo recomendado:**
   - Ejecuta `POST /api/auth/login — Ana` → el token se guarda automáticamente
   - Ejecuta `GET /bff/feed` → ver feed enriquecido
   - Ejecuta `POST /api/posts — Crear publicación` → guarda el `postId`
   - Ejecuta `POST /api/likes — Dar like` → usa el `postId` guardado
   - Ejecuta `POST /api/likes — Like duplicado` → verifica idempotencia

## Endpoints incluidos

| Grupo | Endpoints |
|---|---|
| Auth Service | Login (Ana, Carlos, inválido) |
| User Service | Obtener perfil propio, perfil inexistente |
| Post Service | Listar publicaciones, crear publicación |
| Like Service | Dar like, like duplicado (idempotente) |
| BFF Web | Feed enriquecido |
| Health & Docs | Health check, Swagger UI |

## WebSocket (Postman)

Para probar WebSocket en Postman:
1. New → WebSocket Request
2. URL: `ws://localhost:8080/ws` (Gateway proxy)
3. Una vez conectado, suscríbete vía STOMP:
   - Destination: `/topic/feed`
   - Destination: `/topic/publicacion.{id}.likes`

## Newman (CLI)

```bash
newman run infra/postman/peribook-collection.json \
  --environment infra/postman/peribook-environment.json \
  --reporters cli,html \
  --reporter-html-export target/newman-report.html
```
<!-- 2026-07-09 -->
