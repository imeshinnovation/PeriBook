# PeriBook

Red social en tiempo real construida con microservicios, Java 21 y Angular.
Desarrollada por **Alexander Rubio Cáceres**.

## ¿Qué es PeriBook?

Una plataforma social donde los usuarios pueden publicar mensajes, dar "likes"
y ver actualizaciones en tiempo real. El frontend está inspirado en Facebook
con una paleta de colores verde olivo personalizada.

## Servicios

| Servicio | Puerto | Descripción |
|---|---|---|
| `api-gateway` | 8080 | Punto de entrada único. Enruta peticiones, valida JWT, sirve Swagger |
| `auth-service` | 8081 | Registro y autenticación de usuarios. Emite tokens JWT RS256 |
| `user-service` | 8082 | Perfiles de usuario: alias, nombres, apellidos, fecha de nacimiento |
| `post-service` | 8083 | Creación y listado de publicaciones. Emite eventos a RabbitMQ |
| `like-service` | 8084 | Registro de likes con control de duplicados. Emite eventos a RabbitMQ |
| `realtime-service` | 8085 | Recibe eventos de RabbitMQ y los transmite por WebSocket a los clientes |
| `bff-web` | 8086 | Backend For Frontend. Agrega datos de múltiples servicios en una sola respuesta |
| `frontend` | 4200 | Aplicación Angular (SPA) con diseño responsive verde olivo |

## Cómo levantar el proyecto

### Requisitos
- Docker Compose 2.20 o superior
- Java 21 y Maven 3.9 (solo para desarrollo local)
- Node.js 24 y npm (solo para el frontend)

### Desarrollo local
```bash
# Todo el sistema (13 contenedores: 8 apps + 4 Postgres + RabbitMQ)
docker compose up --build

# Un solo servicio
cd auth-service && docker compose up
```

### Producción (Docker Swarm)
```bash
./infra/deploy.sh              # Construye imágenes y despliega el stack
./infra/verify-deployment.sh   # Verifica que todo esté corriendo
```

## Usuarios de prueba

| Email | Contraseña | Alias |
|---|---|---|
| `ana@peribook.com` | `secreto123` | `ana_writer` |
| `carlos@peribook.com` | `secreto123` | `carlos_reader` |
| `admin@peribook.com` | `admin1234` | `admin_root` |

## Estructura del proyecto

```
PeriBook/
├── .github/workflows/       ← CI/CD con GitHub Actions
├── auth-service/            ← Microservicio de autenticación
├── api-gateway/             ← API Gateway (Spring Cloud Gateway)
├── bff-web/                 ← Backend For Frontend
├── user-service/            ← Microservicio de perfiles
├── post-service/            ← Microservicio de publicaciones
├── like-service/            ← Microservicio de likes
├── realtime-service/        ← Servicio WebSocket + consumidor RabbitMQ
├── frontend/                ← Angular SPA
├── qa-bdd/                  ← Pruebas de aceptación con Serenity BDD
├── infra/                   ← Scripts de infraestructura y despliegue
├── build-templates/         ← Plantillas de configuración Maven
├── docker-compose.yml       ← Orquestador de desarrollo local
└── .env.example             ← Plantilla de variables de entorno
└── DELIVERABLES.md          ← Checklist de entregables
```

**Database-per-Service:** cada microservicio dueño de datos tiene su propia
base de datos Postgres. Los puertos externos son 5432-5435, pero dentro de la
red Docker todos usan el puerto 5432.

**Tiempo real:** las publicaciones y likes se transmiten en vivo vía
RabbitMQ + WebSocket. Los mensajes viajan como JSON sin metadatos de tipo
para mantener la independencia entre servicios.

**Idempotencia:** dar like dos veces a la misma publicación no genera error
ni duplica el registro — simplemente devuelve el like existente.
<!-- 2026-07-09 -->
<!-- 2026-07-09 -->
