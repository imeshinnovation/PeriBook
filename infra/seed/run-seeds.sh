#!/usr/bin/env bash
# ==============================================================================
# PeriBook — run-seeds.sh
#
# Ejecuta los 4 scripts SQL de seed contra las bases de datos en Docker Compose.
# Asume que los contenedores de Postgres están corriendo.
#
# Uso:
#   chmod +x infra/seed/run-seeds.sh
#   ./infra/seed/run-seeds.sh
#
# Variables de entorno (con defaults de docker-compose.yml de cada servicio):
#   AUTH_DB_PORT=5432  USER_DB_PORT=5433  POST_DB_PORT=5434  LIKE_DB_PORT=5435
#   DB_USER=peribook    DB_PASSWORD=peribook
# ==============================================================================

set -euo pipefail

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DB_USER="${DB_USER:-peribook}"
DB_PASS="${DB_PASS:-peribook}"

log() { printf '\033[0;32m[seed]\033[0m %s\n' "$1"; }
err() { printf '\033[0;31m[seed]\033[0m %s\n' "$1"; }

export PGPASSWORD="$DB_PASS"

log "Ejecutando seeds en las 4 bases de datos..."

# ── auth-db (puerto 5432) ────────────────────────────────
log "  auth-db..."
psql -h localhost -p "${AUTH_DB_PORT:-5432}" -U "$DB_USER" -d peribook_auth \
    -f "$DIR/auth-db-seed.sql" -q 2>&1 || err "  auth-db falló"

# ── user-db (puerto 5433) ────────────────────────────────
log "  user-db..."
psql -h localhost -p "${USER_DB_PORT:-5433}" -U "$DB_USER" -d peribook_user \
    -f "$DIR/user-db-seed.sql" -q 2>&1 || err "  user-db falló"

# ── post-db (puerto 5434) ────────────────────────────────
log "  post-db..."
psql -h localhost -p "${POST_DB_PORT:-5434}" -U "$DB_USER" -d peribook_post \
    -f "$DIR/post-db-seed.sql" -q 2>&1 || err "  post-db falló"

# ── like-db (puerto 5435) ────────────────────────────────
log "  like-db..."
psql -h localhost -p "${LIKE_DB_PORT:-5435}" -U "$DB_USER" -d peribook_like \
    -f "$DIR/like-db-seed.sql" -q 2>&1 || err "  like-db falló"

log "✅ Seeds ejecutados en las 4 bases de datos."
echo ""
log "Usuarios de prueba:"
echo "  ana@peribook.com      / secreto123   (ana_writer)"
echo "  carlos@peribook.com   / secreto123   (carlos_reader)"
echo "  admin@peribook.com    / admin1234    (admin_root)"
