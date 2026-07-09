# infra/seed/ — Datos de prueba

Scripts SQL para poblar las 4 bases de datos con datos de prueba. Útiles para:
- Despliegues en Swarm donde los `DevDataSeeder` de Java no aplican (perfil `dev`
  no está activo en producción)
- Restaurar un estado conocido después de borrar volúmenes
- Testing manual con `psql` sin tener que pasar por la API

## Archivos

| Archivo | Base de datos | Puerto | Contenido |
|---|---|---|---|
| `auth-db-seed.sql` | `peribook_auth` | 5432 | 3 usuarios + table DDL |
| `user-db-seed.sql` | `peribook_user` | 5433 | 2 perfiles (Ana, Carlos) + table DDL |
| `post-db-seed.sql` | `peribook_post` | 5434 | 2 publicaciones + `sp_crear_publicacion` |
| `like-db-seed.sql` | `peribook_like` | 5435 | 1 like + `sp_registrar_like` |

## Cómo ejecutar

### Todo junto (recomendado)

```bash
./infra/seed/run-seeds.sh
```

### Individual

```bash
# auth-db
psql -h localhost -p 5432 -U peribook -d peribook_auth -f infra/seed/auth-db-seed.sql

# user-db
psql -h localhost -p 5433 -U peribook -d peribook_user -f infra/seed/user-db-seed.sql

# post-db
psql -h localhost -p 5434 -U peribook -d peribook_post -f infra/seed/post-db-seed.sql

# like-db
psql -h localhost -p 5435 -U peribook -d peribook_like -f infra/seed/like-db-seed.sql
```

## Usuarios de prueba

| Email | Contraseña | Alias | Rol |
|---|---|---|---|
| `ana@peribook.com` | `secreto123` | `ana_writer` | Usuario estándar |
| `carlos@peribook.com` | `secreto123` | `carlos_reader` | Usuario estándar |
| `admin@peribook.com` | `admin1234` | `admin_root` | Admin |

## Nota

Los seeds de desarrollo (perfil `dev`) los inserta automáticamente cada
microservicio vía `CommandLineRunner` (ver `DevDataSeeder.java` en cada servicio).
Estos scripts SQL son una alternativa directa para entornos donde el perfil `dev`
no está activo.
<!-- 2026-07-09 -->
