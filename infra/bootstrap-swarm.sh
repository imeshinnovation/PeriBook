#!/usr/bin/env bash
# ==============================================================================
# PeriBook — bootstrap-swarm.sh
#
# Inicializa el nodo como Swarm manager, etiqueta el nodo para los servicios
# con estado (bases de datos, RabbitMQ), genera el par de llaves JWT si no
# existen, y crea todos los secretos que consume infra/docker-stack.yml.
#
# Uso:
#   chmod +x infra/bootstrap-swarm.sh
#   ./infra/bootstrap-swarm.sh
#
# Es idempotente: si el swarm ya existe o un secreto ya fue creado, lo detecta
# y continúa sin fallar.
# ==============================================================================

set -euo pipefail

SECRETS_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/secrets"
mkdir -p "$SECRETS_DIR"

log() { printf '\033[0;32m[bootstrap]\033[0m %s\n' "$1"; }
warn() { printf '\033[0;33m[bootstrap]\033[0m %s\n' "$1"; }

# ------------------------------------------------------------------------
# 1. Inicializar Swarm (si no está activo ya)
# ------------------------------------------------------------------------
if docker info --format '{{.Swarm.LocalNodeState}}' | grep -q "active"; then
  log "Este nodo ya es parte de un swarm — se omite 'docker swarm init'."
else
  log "Inicializando Docker Swarm..."
  docker swarm init
fi

# ------------------------------------------------------------------------
# 2. Etiquetar el nodo actual para los servicios con estado (DB, RabbitMQ)
#    — usado por los `placement.constraints` de infra/docker-stack.yml
# ------------------------------------------------------------------------
NODE_ID="$(docker node ls --format '{{.ID}}' --filter role=manager | head -n1)"
log "Etiquetando el nodo manager ($NODE_ID) con peribook-data=true..."
docker node update --label-add peribook-data=true "$NODE_ID" >/dev/null

# ------------------------------------------------------------------------
# 3. Generar el par de llaves JWT (RS256) si no existen todavía
# ------------------------------------------------------------------------
if [[ -f "$SECRETS_DIR/jwt_private_key.pem" && -f "$SECRETS_DIR/jwt_public_key.pem" ]]; then
  log "Las llaves JWT ya existen en infra/secrets/ — se reutilizan."
else
  log "Generando par de llaves RS256 para JWT..."
  openssl genrsa -out "$SECRETS_DIR/jwt_private_key.pem" 2048 2>/dev/null
  openssl rsa -in "$SECRETS_DIR/jwt_private_key.pem" -pubout -out "$SECRETS_DIR/jwt_public_key.pem" 2>/dev/null
fi

# ------------------------------------------------------------------------
# 4. Crear los secretos de Swarm (omitiendo los que ya existan)
# ------------------------------------------------------------------------
create_secret_from_file() {
  local name="$1" file="$2"
  if docker secret inspect "$name" >/dev/null 2>&1; then
    warn "Secreto '$name' ya existe — se omite."
  else
    docker secret create "$name" "$file" >/dev/null
    log "Secreto '$name' creado a partir de $file."
  fi
}

create_secret_from_random() {
  local name="$1"
  if docker secret inspect "$name" >/dev/null 2>&1; then
    warn "Secreto '$name' ya existe — se omite."
  else
    openssl rand -base64 24 | docker secret create "$name" - >/dev/null
    log "Secreto '$name' creado con un valor aleatorio."
  fi
}

create_secret_from_file  jwt_private_key   "$SECRETS_DIR/jwt_private_key.pem"
create_secret_from_file  jwt_public_key    "$SECRETS_DIR/jwt_public_key.pem"
create_secret_from_random postgres_password
create_secret_from_random rabbitmq_password

# ------------------------------------------------------------------------
# 5. Resumen
# ------------------------------------------------------------------------
log "Listo. Secretos disponibles en el swarm:"
docker secret ls --format '  - {{.Name}}'

cat <<'EOF'

Siguiente paso — construir las imágenes y desplegar el stack:

  docker build -t peribook/auth-service:1.0.0     auth-service/
  docker build -t peribook/user-service:1.0.0     user-service/
  docker build -t peribook/post-service:1.0.0     post-service/
  docker build -t peribook/like-service:1.0.0     like-service/
  docker build -t peribook/realtime-service:1.0.0 realtime-service/
  docker build -t peribook/bff-web:1.0.0          bff-web/
  docker build -t peribook/api-gateway:1.0.0      api-gateway/
  docker build -t peribook/frontend:1.0.0         frontend/

  docker stack deploy -c infra/docker-stack.yml peribook
  docker stack services peribook

EOF
