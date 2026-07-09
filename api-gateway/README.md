# api-gateway

**Puerto:** 8080 | **Base de datos:** No (sin estado)

## Responsabilidad

Punto de entrada único para todo el sistema. Enruta peticiones HTTP a los
microservicios, valida tokens JWT en cada petición, expone la documentación
Swagger y sirve como proxy WebSocket para el servicio de tiempo real.

## Rutas

| Ruta | Destino | Descripción |
|---|---|---|
| `/api/auth/**` | `auth-service:8081` | Autenticación |
| `/api/users/**` | `user-service:8082` | Perfiles |
| `/api/posts/**` | `post-service:8083` | Publicaciones |
| `/api/likes/**` | `like-service:8084` | Likes |
| `/bff/**` | `bff-web:8086` | Feed enriquecido |
| `/ws/**` | `realtime-service:8085` | WebSocket |
| `/docs` | `auth-service:8081` | Swagger UI |
| `/swagger-ui/**` | `auth-service:8081` | Recursos Swagger |

## Stack interno

- **Spring Cloud Gateway** reactivo (WebFlux)
- **Spring Security** con OAuth2 Resource Server reactivo
- **JJWT** para decodificación de tokens con clave pública RSA
- **Resilience4j** para tolerancia a fallos
- **Springdoc OpenAPI** para documentación

## Seguridad

- `/api/auth/login` — público
- `/docs`, `/swagger-ui/**`, `/v3/api-docs/**` — público
- `/ws/**` — público (el handshake SockJS no requiere JWT)
- `/actuator/health` — público
- El resto de endpoints requiere token JWT válido en el header `Authorization: Bearer <token>`

## Desarrollo

```bash
docker compose up          # Levanta el gateway (puerto 8080)
mvn test                   # 3 tests con WebTestClient reactivo
```
