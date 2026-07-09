#!/usr/bin/env bash
# ==============================================================================
# PeriBook — deploy.sh
#
# Construye las 8 imágenes Docker, inicializa el Swarm (si no está activo),
# crea los secretos, y despliega el stack completo.
#
# Uso:
#   chmod +x infra/deploy.sh
#   ./infra/deploy.sh              # Build + deploy
#   ./infra/deploy.sh --no-build   # Solo deploy (imágenes ya construidas)
# ==============================================================================

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BUILD=true
TAG="${TAG:-1.0.0}"

log() { printf '\033[0;32m[deploy]\033[0m %s\n' "$1"; }
err() { printf '\033[0;31m[deploy]\033[0m %s\n' "$1"; exit 1; }

# ── Argumentos ──────────────────────────────────────────
for arg in "$@"; do
  case "$arg" in
    --no-build) BUILD=false ;;
    *) err "Argumento desconocido: $arg" ;;
  esac
done

cd "$ROOT_DIR"

# ── 1. Bootstrap Swarm ──────────────────────────────────
log "Paso 1/3: Inicializando Docker Swarm y creando secretos..."
chmod +x infra/bootstrap-swarm.sh
./infra/bootstrap-swarm.sh

# ── 2. Build imágenes ───────────────────────────────────
if $BUILD; then
  log "Paso 2/3: Construyendo las 8 imágenes Docker (tag=$TAG)..."

  SERVICES=(
    "auth-service"
    "user-service"
    "post-service"
    "like-service"
    "realtime-service"
    "bff-web"
    "api-gateway"
    "frontend"
  )

  for svc in "${SERVICES[@]}"; do
    log "  Construyendo peribook/$svc:$TAG..."
    docker build -t "peribook/$svc:$TAG" "$svc/" || err "Fallo al construir $svc"
  done

  log "  8 imágenes construidas correctamente."
else
  log "Paso 2/3: Saltando build (--no-build)."
fi

# ── 3. Desplegar stack ──────────────────────────────────
log "Paso 3/3: Desplegando stack 'peribook'..."
docker stack deploy -c infra/docker-stack.yml peribook

echo ""
log "Esperando a que los servicios estén listos..."
sleep 10

echo ""
log "Estado del stack:"
docker stack services peribook

echo ""
log "Despliegue completado."
echo ""
echo "  API Gateway:  http://localhost:8080"
echo "  Swagger UI:   http://localhost:8080/docs"
echo "  Frontend:     http://localhost:4200"
echo "  RabbitMQ UI:  http://localhost:15672 (guest/guest)"
echo ""
echo "  Verificar:    ./infra/verify-deployment.sh"
echo "  Logs:         docker service logs -f peribook_auth-service"
<!-- 2026-07-09 -->
